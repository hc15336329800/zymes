package cn.jb.boot.biz.item.task;

import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.mapper.MesProcedureMapper;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


//定时同步物料

@Component
@Slf4j
public class SetMidItemStockJob {

	@Resource
	private MidItemStockService midItemStockService;
	@Resource
	private MesProcedureMapper mesProcedureMapper;
	@Resource
	private MesItemStockService mesItemStockService;

	private volatile String startTime = "2024-01-01 00:00:00";
	private static final String lastName = "装车";


	/**
	 * 内部同步 : 工序新增+物料工序新增   自动同步
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	public void process() {

		System.out.println("info:  工序新增+物料工序新增 同步调用开始（频率一分钟）");

//        补全缺失的中间工序记录：如果 mes_procedure 有但 t_mid_item_stock 中没有，就插入t_mid_item_stock
//		syncMissMids();

//        动态更新当前进展工序标识和初始库存数量，更新 last_flag = '01' 与 initial_count
//		updateLastProc();

	}


	//        把 mes_procedure 表中有，但 t_mid_item_stock 表中没有的工序数据，筛选出来，然后插入到 t_mid_item_stock 表中。
	private void syncMissMids() {
		List<MesProcedure> ids = midItemStockService.getMissingMid();
		if (CollectionUtils.isNotEmpty(ids)) {
			List<MidItemStock> list = ids.stream().map(d -> {
				MidItemStock mis = new MidItemStock();
				mis.setProcedureCode(d.getProcedureCode());
				mis.setProcedureName(d.getProcedureName());
				mis.setSeqNo(d.getSeqNo());
				mis.setItemNo(d.getItemNo());
				mis.setLastFlag(JbEnum.CODE_00.getCode());
				return mis;
			}).collect(Collectors.toList());
			midItemStockService.saveBatch(list);
		}
	}


	/**
	 * 更新中间表 t_mid_item_stock 中每个产品的最后一道工序标记和初始库存数量。
	 * 步骤：
	 * 1. 查询最近更新时间 >= startTime 的 item_no；
	 * 2. 对每个 item_no：
	 * - 获取其全部工序；
	 * - 判断“最后一道工序”（优先包含“装车”，否则取 seq_no 最大）；
	 * - 将该产品所有工序的 last_flag 清零；
	 * - 再将最后一道工序标记为 last_flag = '01'，并写入 initial_count。
	 */
	private void updateLastProc() {
		// 1. 查询最近更新过的 item_no 列表（依赖 XML 中 selectNearItemNo）
		List<String> itemNos = mesProcedureMapper.selectNearItemNo(startTime);

		for (String itemNo : itemNos) {
			// 2. 查询该 item_no 所有工序（MyBatis-Plus LambdaQueryWrapper 查询）
			List<MesProcedure> list = mesProcedureMapper.selectList(
					new LambdaQueryWrapper<MesProcedure>()
							.eq(MesProcedure::getItemNo, itemNo)
			);

			// 3. 获取最大工序顺序号 seq_no，作为候选“最后一道工序”
			int maxNo = list.stream()
					.mapToInt(MesProcedure::getSeqNo)
					.max()
					.orElse(0); // 防止空指针

			// 4. 优先查找名称中包含“装车”的工序
			// 当前进展到哪道工序”是怎么被判定的?
			Optional<MesProcedure> optional = list.stream()
					.filter(d -> d.getProcedureName() != null && d.getProcedureName().contains(lastName))
					.findFirst();

			MesProcedure lastMid;
			if (optional.isPresent()) {
				lastMid = optional.get();
			} else {
				// 若无“装车”，则取 seq_no 最大的工序
				lastMid = list.stream()
						.filter(d -> d.getSeqNo() == maxNo)
						.findFirst()
						.orElse(null);
				if (lastMid == null) {
					continue; // 安全兜底
				}
			}

			// 5. 将该 item_no 所有工序在中间表中 last_flag 清为 '00'
			midItemStockService.update(new LambdaUpdateWrapper<MidItemStock>()
					.eq(MidItemStock::getItemNo, lastMid.getItemNo())
					.set(MidItemStock::getLastFlag, JbEnum.CODE_00.getCode()));

			// 6. 查询该产品在 mes_item_stock 中的库存，用于 initial_count
			MesItemStock mis = mesItemStockService.getByItemNo(lastMid.getItemNo());
			if (Objects.isNull(mis)) {
				continue; // 无库存记录跳过
			}

			// 7. 将“最后一道工序”设置为 last_flag='01'，并设置 initial_count=当前库存数
			midItemStockService.update(new LambdaUpdateWrapper<MidItemStock>()
					.eq(MidItemStock::getItemNo, lastMid.getItemNo())
					.eq(MidItemStock::getProcedureCode, lastMid.getProcedureCode())
					.set(MidItemStock::getLastFlag, JbEnum.CODE_01.getCode())
					.set(MidItemStock::getInitialCount, mis.getItemCount()));
		}

		// 8. 更新 startTime 为当前时间，用于下次增量更新（通常在定时任务中）
		startTime = DateUtil.formatDateTime(LocalDateTime.now());
	}


//    private void updateLastProc() {
//        List<String> itemNos = mesProcedureMapper.selectNearItemNo(startTime);
//        for (String itemNo : itemNos) {
//            List<MesProcedure> list =
//                    mesProcedureMapper.selectList(new LambdaQueryWrapper<MesProcedure>().eq(MesProcedure::getItemNo,
//                            itemNo));
//            int maxNo = list.stream().mapToInt(MesProcedure::getSeqNo).max().getAsInt();
//            Optional<MesProcedure> optional =
//                    list.stream().filter(d -> d.getProcedureName().contains(lastName)).findFirst();
//            MesProcedure lastMid;
//            if (optional.isPresent()) {
//                lastMid = optional.get();
//            } else {
//                //最后一道工序
//                Optional<MesProcedure> first = list.stream().filter(d -> d.getSeqNo() == maxNo).findFirst();
//                lastMid = first.get();
//            }
//            midItemStockService.update(new LambdaUpdateWrapper<MidItemStock>().eq(MidItemStock::getItemNo,
//                    lastMid.getItemNo())
//                    .set(MidItemStock::getLastFlag, JbEnum.CODE_00.getCode()));
//            MesItemStock mis = mesItemStockService.getByItemNo(lastMid.getItemNo());
//            if (Objects.isNull(mis)) {
//                continue;
//            }
//            midItemStockService.update(new LambdaUpdateWrapper<MidItemStock>()
//                    .eq(MidItemStock::getItemNo, lastMid.getItemNo())
//                    .eq(MidItemStock::getProcedureCode, lastMid.getProcedureCode())
//                    .set(MidItemStock::getLastFlag, JbEnum.CODE_01.getCode())
//                    .set(MidItemStock::getInitialCount, mis.getItemCount())
//            );
//
//        }
//        startTime = DateUtil.formatDateTime(LocalDateTime.now());
//
//    }


}

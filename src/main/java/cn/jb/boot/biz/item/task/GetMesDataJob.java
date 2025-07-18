package cn.jb.boot.biz.item.task;


import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.mapper.MesProcedureMapper;
import cn.jb.boot.biz.item.service.BomUsedService;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.framework.common.utils.StringUtils;
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


/**
 * MES内部基础数据同步      V1.1
 */
@Component
@Slf4j
public class GetMesDataJob {


	@Resource
	private MidItemStockService midItemStockService;
	@Resource
	private MesProcedureMapper mesProcedureMapper;
	@Resource
	private MesItemStockService mesItemStockService;

	@Resource
	private BomUsedService bomUsedService;

	private static final String lastName = "装车";

//	private volatile String startTime = "2025-06-30 17:39:00";  //内部BOM依赖从这个日期开始更新 ！
	private volatile String startTime = "2025-03-05 11:50:00";  //内部BOM依赖从这个日期开始更新 ！


	//===========================调试===================================

//	//单个bom构建测试
//	@Scheduled(cron = "0 0/1 * * * ?")
//	public void bomByItemCS(){
//
//
//		log.info("临时调试...600159（记得关闭计时器）");
//		System.out.println("临时调试...600159（记得关闭计时器）" );
//		String itemNo="600159";
//		String bomNo ="2516004414-1";
//		    bomByItem(  itemNo,   bomNo);
//
//	}



	//===========================同步bom树（按时间）===================================


//	确保外部传时间覆盖原有 startTime。
	public void setStartTime(String startTime) {
		if (startTime != null && !startTime.isEmpty()) {
			this.startTime = startTime;
			log.info("外部指定的 startTime 已生效: {}", this.startTime);
		}
	}

	/**
	 * 内部同步bom树（按时间） --  重载
	 * 读取：mes_item_stock, mes_item_use     删除/插入：t_bom_used
	 */

	public void bom(String st) {

		long start = System.currentTimeMillis();
		log.info("开始加载BOM用料...");
		bomUsedService.load(st);
		log.info("加载BOM用料完成...cost:{}", System.currentTimeMillis() - start);

	}


	/**
	 * 内部同步bom树（按时间）
	 * 读取：mes_item_stock, mes_item_use     删除/插入：t_bom_used
	 */

	public void bom() {


		  String st = "2025-03-05 11:50:00";  // 从新构建

		long start = System.currentTimeMillis();
		log.info("开始加载BOM用料...");
		bomUsedService.load(st);
		log.info("加载BOM用料完成...cost:{}", System.currentTimeMillis() - start);

//		startTime = DateUtil.formatDateTime(LocalDateTime.now());   //注意重置时间
//		System.out.println("Info:  同步重置时间" + startTime);

	}

	/**
	 * 内部同步bom树（按物料号和bom号）
	 * 读取：mes_item_stock, mes_item_use     删除/插入：t_bom_used
	 */
	public void bomByItem(String itemNo, String bomNo){

 		log.info("开始加载BOM用料...");



		 if ("0".equals(bomNo)){
			 System.out.println("Info:  bomNo=0" );

		 }else {
			 System.out.println("Info:  bomNo="+bomNo );
 			 bomUsedService.loadByItem(itemNo,bomNo); //构建bom树
		 }

//		startTime = DateUtil.formatDateTime(LocalDateTime.now());   //注意重置时间
//		System.out.println("Info:  同步重置时间" + startTime);
	}


	//===========================同步工序 及 中间件量（按物料号）===================================

	public void processItem(String itemNo) {

		//        补全缺失的中间工序记录：如果 mes_procedure 有但 t_mid_item_stock 中没有，就插入t_mid_item_stock
		addUpdateMidItemStockItem( itemNo);
		//        动态更新当前进展工序标识和初始库存数量，更新 last_flag = '01' 与 initial_count
		updateMidItemStock();
	}


	//        V1.1    先查（mes_procedure）然删（t_mid_item_stock）后增（t_mid_item_stock）
	private void addUpdateMidItemStockItem(String itemNo) {
		if (StringUtils.isBlank(itemNo)) {
			log.warn("物料号为空，跳过同步");
			return;
		}

		// 1️⃣ 删除 t_mid_item_stock 中该物料的所有记录
		midItemStockService.remove(
				new LambdaQueryWrapper<MidItemStock>()
						.eq(MidItemStock::getItemNo, itemNo)
		);

		// 2️⃣ 查询 mes_procedure 中该物料的所有工序
		List<MesProcedure> procedures = mesProcedureMapper.selectList(
				new LambdaQueryWrapper<MesProcedure>()
						.eq(MesProcedure::getItemNo, itemNo)
		);

		if (CollectionUtils.isEmpty(procedures)) {
			log.info("物料号 {} 没有工序数据，跳过", itemNo);
			return;
		}

		// 3️⃣ 构建新数据
		List<MidItemStock> list = procedures.stream().map(d -> {
			MidItemStock mis = new MidItemStock();
			mis.setProcedureCode(d.getProcedureCode());
			mis.setProcedureName(d.getProcedureName());
			mis.setSeqNo(d.getSeqNo());
			mis.setItemNo(d.getItemNo());
			mis.setLastFlag(JbEnum.CODE_00.getCode());
			return mis;
		}).collect(Collectors.toList());

		// 4️⃣ 批量插入
		midItemStockService.saveBatch(list);

		log.info("物料号 {} 工序同步完成，新增记录数：{}", itemNo, list.size());
	}




	//===========================同步工序 及 中间件量（按时间）===================================

	/**
	 * 内部同步 : 工序新增+物料工序新增   自动同步（按时间）
	 */
	public void process(String onTime) {

		//        补全缺失的中间工序记录：如果 mes_procedure 有但 t_mid_item_stock 中没有，就插入t_mid_item_stock
		addUpdateMidItemStock(onTime);
		//        动态更新当前进展工序标识和初始库存数量，更新 last_flag = '01' 与 initial_count
		updateMidItemStock(onTime);
	}



	//        把 mes_procedure 表中有，但 t_mid_item_stock 表中没有的工序数据，筛选出来，然后插入到 t_mid_item_stock 表中。（按时间）
	//        V1.1    先删后增
	private void addUpdateMidItemStock(String  st) {
		// 1️⃣ 查出 mes_procedure 中存在的 item_no 列表（用于逐个处理）
		List<String> itemNos = mesProcedureMapper.selectNearItemNo(st);

		for (String itemNo : itemNos) {
			// 2️⃣ 删除 t_mid_item_stock 中该物料的所有记录
			midItemStockService.remove(
					new LambdaQueryWrapper<MidItemStock>()
							.eq(MidItemStock::getItemNo, itemNo)
			);

			// 3️⃣ 查询 mes_procedure 中该物料的所有工序
			List<MesProcedure> procedures = mesProcedureMapper.selectList(
					new LambdaQueryWrapper<MesProcedure>()
							.eq(MesProcedure::getItemNo, itemNo)
			);

			if (CollectionUtils.isEmpty(procedures)) {
				continue;  // 没工序就跳过
			}

			// 4️⃣ 构建新数据
			List<MidItemStock> list = procedures.stream().map(d -> {
				MidItemStock mis = new MidItemStock();
				mis.setProcedureCode(d.getProcedureCode());
				mis.setProcedureName(d.getProcedureName());
				mis.setSeqNo(d.getSeqNo());
				mis.setItemNo(d.getItemNo());
				mis.setLastFlag(JbEnum.CODE_00.getCode());
				return mis;
			}).collect(Collectors.toList());

			// 5️⃣ 批量插入
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
	private void updateMidItemStock(String onTime) {
		// 1. 查询最近更新过的 item_no 列表（依赖 XML 中 selectNearItemNo）
		List<String> itemNos = mesProcedureMapper.selectNearItemNo(onTime);

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



	/**
	 *  重载 ： 按物料号更新
	 */
	private void updateMidItemStock() {
		// 1. 查询最近更新过的 item_no 列表（依赖 XML 中 selectNearItemNo）
		List<String> itemNos = mesProcedureMapper.selectNearItemNo();

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

	}



}

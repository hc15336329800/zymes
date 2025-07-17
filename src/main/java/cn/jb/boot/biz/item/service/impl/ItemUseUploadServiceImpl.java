package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.biz.item.mapper.MesItemUseMapper;
import cn.jb.boot.biz.item.vo.request.MesItemUsedUploadRequest;
import cn.jb.boot.framework.common.exception.ServiceException;
import cn.jb.boot.framework.common.utils.StringUtils;
import cn.jb.boot.util.EasyExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 导入一级用料：
 * Excel → mes_item_use
 * */
@Service
public class ItemUseUploadServiceImpl
		extends ServiceImpl<MesItemUseMapper, MesItemUse> {

	@Resource private MesItemStockMapper stockMapper;
	@Resource private MesItemUseMapper   itemUseMapper;  // 继承 ServiceImpl 仍可注入


	@Resource private BomTreeServiceImpl   bomTreeService;  // 注入树服务

	////////////////////////////////////////////////////////////////////////////新街口//////////////////////////////////////////////////////////////////////////////////




	/**
	 * 上传 bom表 ：Excel → 写 mes_item_use → 写 t_bom_used
	 * 全流程校验 + 同一事务
	 */
	@Transactional
	public void uploadAndRebuild(MultipartFile file) {
		// 1. 模板头校验
 	List<String> expected = Arrays.asList("母件BOM编号（图纸号）", "用料编码", "固定用量");
	List<String> actual = EasyExcelUtil.readHead(file);
//		if (!expected.equals(actual)) {
//			throw new ServiceException("导入失败：模板格式不对，请下载最新模板后再试。");
//		}

		if (actual.stream().noneMatch(h->h.contains("母件BOM编号（图纸号）"))
				|| actual.stream().noneMatch(h->h.contains("所用物料编码"))
				|| !actual.contains("固定用量")

		) {
			throw new ServiceException("导入失败：请确认“图纸号”、“用料编码”、“固定用量”三列存在");
		}


		// 2. 读取 Excel
		List<MesItemUsedUploadRequest> rows =
				EasyExcelUtil.importExcel(file, MesItemUsedUploadRequest.class);

		// 3. 行级校验
		List<String> errors = new ArrayList<>();
		for (int i = 0; i < rows.size(); i++) {
			MesItemUsedUploadRequest r = rows.get(i);
			int rowNum = i + 2;
			if (StringUtils.isBlank(r.getBomNo())) {
				errors.add("第" + rowNum + "行：图纸号不能为空");
			}
			if (StringUtils.isBlank(r.getUseItemNo())) {
				errors.add("第" + rowNum + "行：用料编码不能为空");
			}

			if (r.getFixedUse() == null || r.getFixedUse().compareTo(BigDecimal.ZERO) <= 0) {
				errors.add("第" + rowNum + "行：固定用量必须大于0");
			}
		}
		if (!errors.isEmpty()) {
			throw new ServiceException("导入失败：\n" + String.join("\n", errors));
		}

		// 4. 去空格
		rows.forEach(r -> {
			r.setBomNo(StringUtils.trimToEmpty(r.getBomNo()));
			r.setUseItemNo(StringUtils.trimToEmpty(r.getUseItemNo()));
		});

		// 5. 批量查库存
		Set<String> bomNos  = rows.stream().map(MesItemUsedUploadRequest::getBomNo).collect(Collectors.toSet());
		Set<String> itemNos = rows.stream().map(MesItemUsedUploadRequest::getUseItemNo).collect(Collectors.toSet());

		List<MesItemStock> bomList = stockMapper.selectList(
				new LambdaQueryWrapper<MesItemStock>()
						.eq(MesItemStock::getItemType, "01")
						.in(MesItemStock::getBomNo, bomNos)
		);
		Map<String, MesItemStock> bomMap = bomList.stream()
				.collect(Collectors.toMap(
						MesItemStock::getBomNo,
						Function.identity(),
						(a, b) -> a));

		List<MesItemStock> partList = stockMapper.selectList(
				new LambdaQueryWrapper<MesItemStock>()
						.in(MesItemStock::getItemNo, itemNos)
		);
		Map<String, MesItemStock> itemMap = partList.stream()
				.collect(Collectors.toMap(
						MesItemStock::getItemNo,
						Function.identity(),
						(a, b) -> a));

		// 6. 业务校验：bomNo & useItemNo 必须都存在
		for (int i = 0; i < rows.size(); i++) {
			MesItemUsedUploadRequest r = rows.get(i);
			int rowNum = i + 2;
			if (!bomMap.containsKey(r.getBomNo())) {
				errors.add("第" + rowNum + "行：图纸号 " + r.getBomNo() + " 不存在");
			}
			if (!itemMap.containsKey(r.getUseItemNo())) {
				errors.add("第" + rowNum + "行：用料编码 " + r.getUseItemNo() + " 不存在");
			}
		}
		if (!errors.isEmpty()) {
			throw new ServiceException("导入失败：\n" + String.join("\n", errors));
		}

		// 7. 过滤 & 封装 mes_item_use 实体
		List<MesItemUse> entities = new ArrayList<>();
		for (MesItemUsedUploadRequest r : rows) {
			MesItemStock bom  = bomMap.get(r.getBomNo());
			MesItemStock part = itemMap.get(r.getUseItemNo());
			// 再次防自耗
			if (bom.getItemNo().equals(part.getItemNo())) continue;

			MesItemUse e = new MesItemUse();
			e.setItemNo(bom.getItemNo());
			e.setUseItemNo(part.getItemNo());
			e.setUseItemType(part.getItemType());
			e.setUseItemCount(r.getFixedUse());

			// —— 直接用库存表类型 ——
			// part.getItemType() 本身就是 "00" 或 "01"
			e.setUseItemType(part.getItemType());

			entities.add(e);
		}
		if (entities.isEmpty()) {
			throw new ServiceException("导入失败：无有效用料行");
		}

		// 8. 覆盖写 mes_item_use
		Set<String> roots = entities.stream()
				.map(MesItemUse::getItemNo)
				.collect(Collectors.toSet());
		this.remove(new LambdaQueryWrapper<MesItemUse>().in(MesItemUse::getItemNo, roots));
		this.saveBatch(entities, 1000);

		// 9. 生成并写入 t_bom_used
		for (String root : roots) {
			bomTreeService.rebuildByRoot(root);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//	/**    uploadAndRebuild V1.0
//	 * ① 解析 Excel → 写 mes_item_use
//	 * ② 再对所有根编码生成 t_bom_used
//	 * 整个过程在同一事务中，要么都成功，要么都回滚
//	 */
//	@Transactional
//	public void uploadAndRebuild(MultipartFile file) {
//		// ---- 上传业务（原 upload 逻辑） ----
//		List<MesItemUsedUploadRequest> rows =
//				EasyExcelUtil.importExcel(file, MesItemUsedUploadRequest.class);
//		rows.forEach(r -> {
//			r.setBomNo(StringUtils.trimToEmpty(r.getBomNo()));
//			r.setUseItemNo(StringUtils.trimToEmpty(r.getUseItemNo()));
//		});
//		// 批量查 stock
//		Set<String> bomNos  = rows.stream().map(MesItemUsedUploadRequest::getBomNo).collect(Collectors.toSet());
//		Set<String> itemNos = rows.stream().map(MesItemUsedUploadRequest::getUseItemNo).collect(Collectors.toSet());
//		List<MesItemStock> bomList = stockMapper.selectList(
//				new LambdaQueryWrapper<MesItemStock>()
//						.eq(MesItemStock::getItemType, "01")
//						.in(MesItemStock::getBomNo, bomNos));
//		Map<String, MesItemStock> bomMap = bomList.stream()
//				.collect(Collectors.toMap(MesItemStock::getBomNo, Function.identity(), (a,b)->a));
//		List<MesItemStock> partList = stockMapper.selectList(
//				new LambdaQueryWrapper<MesItemStock>()
//						.in(MesItemStock::getItemNo, itemNos));
//		Map<String, MesItemStock> itemMap = partList.stream()
//				.collect(Collectors.toMap(MesItemStock::getItemNo, Function.identity(), (a,b)->a));
//		// 过滤封装
//		List<MesItemUse> entities = new ArrayList<>();
//		for (MesItemUsedUploadRequest r : rows) {
//			MesItemStock bom  = bomMap.get(r.getBomNo());
//			MesItemStock part = itemMap.get(r.getUseItemNo());
//			if (bom == null || part == null) continue;
//			if (bom.getItemNo().equals(part.getItemNo())) continue;
//			MesItemUse e = new MesItemUse();
//			e.setItemNo(bom.getItemNo());
//			e.setUseItemNo(part.getItemNo());
//			e.setUseItemType(part.getItemType());
//			e.setUseItemCount(r.getFixedUse());
//			entities.add(e);
//		}
//		if (entities.isEmpty()) {
//			throw new ServiceException("无有效行，导入终止");
//		}
//		// 覆盖式写 mes_item_use
//		Set<String> roots = entities.stream()
//				.map(MesItemUse::getItemNo)
//				.collect(Collectors.toSet());
//		this.remove(new LambdaQueryWrapper<MesItemUse>()
//				.in(MesItemUse::getItemNo, roots));
//		this.saveBatch(entities, 1000);
//
//		// ---- 紧接着生成树 ----
//		for (String root : roots) {
//			bomTreeService.rebuildByRoot(root);
//		}
//	}


	@Transactional
	public void upload(MultipartFile file) {


//		/* ① 读取 Excel */
		List<MesItemUsedUploadRequest> rows =
				EasyExcelUtil.importExcel(file, MesItemUsedUploadRequest.class);

		/* ---------- DEBUG START ---------- */
		System.out.println("Excel 行数 = " + rows.size());
		rows.forEach(r -> System.out.println("row = " + r));
		/* ---------- DEBUG END ------------ */

//		/* ② 去空格 */
		rows.forEach(r -> {
			r.setBomNo(StringUtils.trimToEmpty(r.getBomNo()));
			r.setUseItemNo(StringUtils.trimToEmpty(r.getUseItemNo()));
		});

//		/* ③ 批量查库存（两次 IN 查询） */
		Set<String> bomNos  = rows.stream()
				.map(MesItemUsedUploadRequest::getBomNo)
				.collect(Collectors.toSet());
		Set<String> itemNos = rows.stream()
				.map(MesItemUsedUploadRequest::getUseItemNo)
				.collect(Collectors.toSet());

		// ③-1 成品（item_type = '01'）
		List<MesItemStock> bomList = stockMapper.selectList(
				new LambdaQueryWrapper<MesItemStock>()
						.eq(MesItemStock::getItemType, "01")
						.in(MesItemStock::getBomNo, bomNos));
		Map<String, MesItemStock> bomMap = bomList.stream()
				.collect(Collectors.toMap(
						MesItemStock::getBomNo,
						Function.identity(),
						(a, b) -> a));               // 重复取第一条

		// ③-2 所有物料
		List<MesItemStock> partList = stockMapper.selectList(
				new LambdaQueryWrapper<MesItemStock>()
						.in(MesItemStock::getItemNo, itemNos));
		Map<String, MesItemStock> itemMap = partList.stream()
				.collect(Collectors.toMap(
						MesItemStock::getItemNo,
						Function.identity(),
						(a, b) -> a));

//		/* ④ 过滤 + 封装实体 */
		List<MesItemUse> entities = new ArrayList<>();
		for (MesItemUsedUploadRequest r : rows) {
			MesItemStock bom  = bomMap.get(r.getBomNo());
			MesItemStock part = itemMap.get(r.getUseItemNo());
			if (bom == null || part == null) continue;                 // 缺库存
			if (bom.getItemNo().equals(part.getItemNo())) continue;    // 自耗

			MesItemUse e = new MesItemUse();
			e.setItemNo(bom.getItemNo());
			e.setUseItemNo(part.getItemNo());
			e.setUseItemType(part.getItemType());
			e.setUseItemCount(r.getFixedUse());
			entities.add(e);
		}
		if (entities.isEmpty()) throw new ServiceException("无有效行，导入终止");

//		/* ⑤ 覆盖式落库 */
		Set<String> roots = entities.stream()
				.map(MesItemUse::getItemNo)
				.collect(Collectors.toSet());

		// 先删旧
		this.remove(new LambdaQueryWrapper<MesItemUse>()
				.in(MesItemUse::getItemNo, roots));
		// 再批插
		this.saveBatch(entities, 1000);
	}
}

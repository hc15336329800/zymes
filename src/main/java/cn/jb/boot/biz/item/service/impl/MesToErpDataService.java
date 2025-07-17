package cn.jb.boot.biz.item.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.jb.boot.biz.agvcar.mapper.BomManageInfoMapper;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.enums.EnumItemOrigin;
import cn.jb.boot.biz.item.enums.EnumItemType;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.biz.item.mapper.MesProcedureMapper;
import cn.jb.boot.biz.item.mapper.MesToErpDataMapper;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MesItemUseService;
import cn.jb.boot.biz.item.service.MesProcedureService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;




 import org.apache.commons.lang3.StringUtils;




/**
 * ERP 和 MES 数据同步的服务类——仅物料档案
 */
@Service
@Slf4j
public class MesToErpDataService {

	@Autowired
	private BomManageInfoMapper bomManageInfoMapper;

	@Resource
	private MesItemStockService mesItemStockService;

	@Autowired
	private MesItemUseService mesItemUseService;

	@Autowired
	private MesToErpDataMapper mesToErpDataMapper;

	@Autowired
	private MesProcedureService mesProcedureService;

	@Autowired
	private MesProcedureMapper mesProcedureMapper;

	@Autowired
	private MesItemStockMapper mesItemStockMapper;

//=====================================map中切换数据源=========================================




	//===================物料==================



	/**
	 * 物料同步 V1.3
	 * 仅同步 ERP（JSPMATERIAL）表 BYTSTATUS=1的物料数据，完成后将 BYTSTATUS 置为 0。
	 *  整改： 仅处理新增/修改，不处理删除逻辑 （中间表没有删除的标志）
	 */
	public int syncItemStock() {
		// =============== 1. 数据准备 ===============
		// 1.1 拉取ERP数据（Oracle）
		List<Map<String, Object>> erpList;
		try {
			erpList = mesToErpDataMapper.materialMessage();
			log.info("[物料同步] ERP拉取数量：{}", erpList == null ? 0 : erpList.size());
			System.out.println("[物料同步] ERP拉取数量："+ erpList.size());
		} catch (Exception e) {
			log.error("[物料同步] ERP数据拉取失败", e);
			System.out.println("[物料同步] ERP拉取数量："+ e);

			return -1;
		}

		if (CollectionUtils.isEmpty(erpList)) {
//			log.info("[物料同步] 无待同步数据");
//			System.out.println("[物料同步] 无待同步数据");
			return 0;
		}

		// 1.2 初始化容器
		LocalDateTime now = LocalDateTime.now();
		List<String> erpItemCodes = new ArrayList<>();
		List<MesItemStock> toSave = new ArrayList<>();

		// =============== 2. 预加载优化 ===============
		// 2.1 提取ERP物料编码
		for (Map<String, Object> item : erpList) {
			erpItemCodes.add(item.get("STRITEMCODE").toString());
		}

		// =============== 3. 先删除MES中已存在的物料 ===============
		if (!erpItemCodes.isEmpty()) {
			try {
				// 使用Mapper直接删除（需确保MesItemStockMapper中有对应方法）
				int deletedCount = mesItemStockMapper.delete(
						new QueryWrapper<MesItemStock>()
								.in("item_no", erpItemCodes)
				);
				log.info("[物料同步] 已删除{}条旧物料", deletedCount);
				System.out.println("[物料同步] 已删除{}条旧物料"+deletedCount);

			} catch (Exception e) {
				log.error("[物料同步] 删除旧物料失败", e);
				System.out.println("[物料同步] 删除旧物料失败"+ e);
				return -2;
			}
		}

		// =============== 4. 构建新增数据 ===============
		for (Map<String, Object> erpItem : erpList) {
			String itemCode = erpItem.get("STRITEMCODE").toString();

			// 4.1 创建新物料实体
			MesItemStock stock = new MesItemStock();
			stock.setId(IdUtil.fastSimpleUUID()); // 使用hutool工具类生成UUID
			stock.setCreatedBy("1");
			stock.setCreatedTime(now);

			// 4.2 字段映射
			stock.setItemNo(itemCode);
			stock.setItemName(erpItem.get("STRITEMNAME").toString());
			stock.setItemModel(Objects.toString(erpItem.get("STRITEMSTYLE"), ""));
			stock.setItemMeasure(erpItem.get("STRUNITNAME").toString());

			// 数值处理
			Object onhand = erpItem.get("DBLONHAND");
			stock.setErpCount(onhand != null ? new BigDecimal(onhand.toString()) : BigDecimal.ZERO);
			stock.setItemCount(stock.getErpCount());

			// 来源类型处理
			String sourceType = erpItem.get("BYTSOURCE").toString();
			if ("0".equals(sourceType)) { // 自制件
				stock.setItemType(EnumItemType.SELF_MADE.getCode());
				stock.setItemOrigin(EnumItemOrigin.SELF.getCode());
				stock.setBomNo("");
			} else { // 采购件
				stock.setItemType(EnumItemType.PURCHASE.getCode());
				stock.setItemOrigin(EnumItemOrigin.PURCHASE.getCode());
				stock.setBomNo(Objects.toString(erpItem.get("STRBOMCODE"), stock.getItemModel()));
			}

			// 辅助单位处理
			stock.setItemCountAssist(BigDecimal.ZERO);
			stock.setItemMeasureAssist(Objects.toString(
					erpItem.get("STRUNITNAMEAUX"),
					erpItem.get("STRUNITNAME").toString()
			));

			// 状态和审计字段
			stock.setIsValid("01");  //生效
			stock.setUpdatedBy("1");
			stock.setUpdatedTime(now);

			toSave.add(stock);
		}

		// =============== 5. 批量新增数据 ===============
		if (CollectionUtils.isEmpty(toSave)) {
			log.warn("[物料同步] 无有效物料需要保存");
			System.out.println("[物料同步] 无有效物料需要保存 ");
			return 0;
		}

		int saveCount;
		try {
			// 分批处理，每500条一批
			int batchSize = 500;
			for (int i = 0; i < toSave.size(); i += batchSize) {
				int end = Math.min(i + batchSize, toSave.size());
				List<MesItemStock> batchList = toSave.subList(i, end);
				mesItemStockService.saveBatch(batchList);
			}
			saveCount = toSave.size();
			log.info("[物料同步] 成功新增{}条物料", saveCount);
		} catch (Exception e) {
			log.error("[物料同步] 新增物料失败", e);
			System.out.println("[物料同步] 新增物料失败 "+ e);
			return -3;
		}

		// =============== 6. 回写ERP（Oracle） ===============
		if (CollectionUtils.isNotEmpty(erpItemCodes)) {
			try {
				// 分批回写ERP（每1000条一批）
				int batchSize = 1000;
				for (int i = 0; i < erpItemCodes.size(); i += batchSize) {
					int end = Math.min(i + batchSize, erpItemCodes.size());
					List<String> batchCodes = erpItemCodes.subList(i, end);
					mesToErpDataMapper.materUpdate(batchCodes);
				}
				log.info("[物料同步] 已回写ERP状态，共{}条", erpItemCodes.size());
				System.out.println("[物料同步] 已回写ERP状态，共{}条 "+ erpItemCodes.size() );

			} catch (Exception e) {
				log.error("[物料同步] ERP回写失败", e);
				System.out.println("[物料同步]  ERP回写失败"+ e);
				return -4;
			}
		}

		return saveCount;
	}

	/**
	 * 物料同步 V1.2
	 * 仅同步 ERP（JSPMATERIAL）表 BYTSTATUS=1的物料数据，完成后将 BYTSTATUS 置为 0。
	 * 先查询MES中所有 is_valid=01 的记录。
	 * 对比ERP的最新数据，只将ERP中不存在的记录标记为无效。
	 */
	public int syncItemStockV12() {
		// 1. 拉取ERP（Oracle）未同步的物料数据（BYTSTATUS=1）
		List<Map<String, Object>> erpList;
		try {
			erpList = mesToErpDataMapper.materialMessage();
			log.info("ERP拉取到待同步物料数量：{}", erpList.size());
		} catch (Exception e) {
			log.error("ERP数据拉取失败", e);
			return -1;
		}
		if (CollectionUtils.isEmpty(erpList)) {
			log.info("ERP无待同步物料");
			return 0;
		}

		// 2. 提取ERP物料编码集合
		List<String> erpItemCodes = erpList.stream()
				.map(m -> m.get("STRITEMCODE").toString())
				.collect(Collectors.toList());

		// 3. 查询MES中当前所有有效物料（避免全表无效化）
		List<MesItemStock> mesValidItems = mesItemStockService.list(
				new LambdaQueryWrapper<MesItemStock>()
						.eq(MesItemStock::getIsValid, "01")
		);
		log.info("MES当前有效物料数量：{}", mesValidItems.size());

		// 4. 精准标记失效：找出MES中存在但ERP中不存在的物料
		LocalDateTime now = LocalDateTime.now();
		List<String> invalidCodes = mesValidItems.stream()
				.map(MesItemStock::getItemNo)
				.filter(itemNo -> !erpItemCodes.contains(itemNo))
				.collect(Collectors.toList());

		if (!invalidCodes.isEmpty()) {
			try {
				boolean  updated = mesItemStockService.update(
						new LambdaUpdateWrapper<MesItemStock>()
								.set(MesItemStock::getIsValid, "00")
								.set(MesItemStock::getUpdatedBy, "1")
								.set(MesItemStock::getUpdatedTime, now)
								.in(MesItemStock::getItemNo, invalidCodes)
				);
				log.info("已将{}条MES物料标记为无效", updated);
			} catch (Exception e) {
				log.error("标记无效物料失败", e);
				return -2;
			}
		}

		// 5. 构建待同步的物料列表（新增或更新）
		Map<String, MesItemStock> existMap = mesItemStockService.getByItemNos(erpItemCodes);
		List<MesItemStock> toSave = new ArrayList<>(erpList.size());

		for (Map<String, Object> erpItem : erpList) {
			String itemCode = erpItem.get("STRITEMCODE").toString();
			MesItemStock stock = existMap.getOrDefault(itemCode, new MesItemStock());

			// 5.1 新物料初始化
			if (stock.getId() == null) {
				stock.setId(UUID.randomUUID().toString().replace("-", ""));
				stock.setCreatedBy("1");
				stock.setCreatedTime(now);
			}

			// 5.2 字段映射
			stock.setItemNo(itemCode);
			stock.setItemName(erpItem.get("STRITEMNAME").toString());
			stock.setItemModel(Objects.toString(erpItem.get("STRITEMSTYLE"), ""));
			stock.setItemMeasure(erpItem.get("STRUNITNAME").toString());

			// 处理库存数量（防null）
			Object onhand = erpItem.get("DBLONHAND");
			BigDecimal erpQty = onhand != null ? new BigDecimal(onhand.toString()) : BigDecimal.ZERO;
			stock.setErpCount(erpQty);
			stock.setItemCount(erpQty);

			// 5.3 类型判断（物料/BOM）
			String sourceType = erpItem.get("BYTSOURCE").toString();
			if (ItemType.isMaterials(sourceType)) {
				stock.setItemType(ItemType.MATERIALS.getCode());
				stock.setBomNo(""); // 物料无BOM号
			} else {
				stock.setItemType(ItemType.BOM.getCode());
				stock.setBomNo(stock.getItemModel()); // BOM号取规格型号
			}

			// 5.4 公共字段
			stock.setItemCountAssist(BigDecimal.ZERO);
			stock.setItemMeasureAssist(erpItem.get("STRUNITNAME").toString());
			stock.setIsValid("01");
			stock.setUpdatedBy("1");
			stock.setUpdatedTime(now);

			toSave.add(stock);
		}

		// 6. 批量保存到MES（500条/批）
		try {
			boolean success = mesItemStockService.saveOrUpdateBatch(toSave, 500);
			if (!success) {
				log.error("物料批量保存失败");
				return -3;
			}
			log.info("成功同步{}条物料到MES", toSave.size());
		} catch (Exception e) {
			log.error("同步到MES失败", e);
			return -4;
		}

		// 7. 回写ERP同步状态（BYTSTATUS=0）
		try {
			mesToErpDataMapper.materUpdate(erpItemCodes);
			log.info("已回写{}条ERP物料状态", erpItemCodes.size());
		} catch (Exception e) {
			log.error("回写ERP状态失败", e);
			return -5;
		}

		return erpItemCodes.size();
	}



	/**
	 * 物料同步 V1.1
	 * 仅同步 ERP（JSPMATERIAL）表 BYTSTATUS=0 的物料数据，完成后将 BYTSTATUS 置为 1。
	 * 拉取数据、回写都走 Mapper（Oracle），
	 */
//	@Transactional(rollbackFor = Exception.class)   跨库不能使用事务！
	public int syncItemStockV11() {
		// 1. 拉取ERP（Oracle）数据
		List<Map<String, Object>> erpList = null;
		try {
			erpList = mesToErpDataMapper.materialMessage();
			System.out.println("ERP拉取结果：" + erpList);
			log.info("ERP拉取结果：" + erpList);
		} catch (Exception e) {
			System.err.println("【警告】ERP数据拉取失败: " + e.getMessage());
			log.info("【警告】ERP数据拉取失败: " + e.getMessage(), e);
			return -1;
		}
		if (erpList == null || erpList.isEmpty()) {
//			System.err.println("【警告】ERP未查到可同步的物料数据！");
//			log.info("【警告】ERP未查到可同步的物料数据！");
			return 0;
		}

		// 2. 提取编码集合
		List<String> allCodes = erpList.stream()
				.map(m -> m.get("STRITEMCODE").toString())
				.collect(Collectors.toList());

		// 3. 查询MES已有物料
		Map<String, MesItemStock> existMap = mesItemStockService.getByItemNos(allCodes);

		// 4. 标记MES中已移除的物料为无效
		LocalDateTime now = LocalDateTime.now();
		if (!allCodes.isEmpty()) {
			try {
				mesItemStockService.update(
						new LambdaUpdateWrapper<MesItemStock>()
								.set(MesItemStock::getIsValid, "00")
								.set(MesItemStock::getUpdatedBy, "1")
								.set(MesItemStock::getUpdatedTime, now)
								.notIn(MesItemStock::getItemNo, allCodes)
				);
			} catch (Exception e) {
				System.err.println("【警告】更新MES无效物料失败: " + e.getMessage());
				log.info("【警告】更新MES无效物料失败: " + e.getMessage(), e);
				return -2;
			}
		}

		// 5. 组装同步列表
		List<MesItemStock> saveList = new ArrayList<>(erpList.size());
		for (Map<String, Object> row : erpList) {
			String code = row.get("STRITEMCODE").toString();
			MesItemStock stock = existMap.getOrDefault(code, new MesItemStock());
			if (stock.getId() == null) {
				stock.setId(UUID.randomUUID().toString().replace("-", ""));
				stock.setCreatedBy("1");
				stock.setCreatedTime(now);
			}
			stock.setItemNo(code);
			stock.setItemName(row.get("STRITEMNAME").toString());
			stock.setItemModel(Objects.toString(row.get("STRITEMSTYLE"), ""));
			stock.setItemMeasure(row.get("STRUNITNAME").toString());
			Object onhand = row.get("DBLONHAND");
			BigDecimal erpQty = onhand != null ? new BigDecimal(onhand.toString()) : BigDecimal.ZERO;
			stock.setErpCount(erpQty);
			stock.setItemCount(erpQty);
			String src = row.get("BYTSOURCE").toString();
			stock.setItemOrigin(src);
			if (ItemType.isMaterials(src)) {
				stock.setItemType(ItemType.MATERIALS.getCode());
				stock.setBomNo("");
			} else {
				stock.setItemType(ItemType.BOM.getCode());
				stock.setBomNo(stock.getItemModel());
			}
			stock.setItemCountAssist(BigDecimal.ZERO);
			stock.setItemMeasureAssist(row.get("STRUNITNAME").toString());
			stock.setIsValid("01");
			stock.setUpdatedBy("1");
			stock.setUpdatedTime(now);
			saveList.add(stock);
		}

		// 6. MySQL批量插入或更新
		boolean insertOk = false;
		try {
			insertOk = mesItemStockService.saveOrUpdateBatch(saveList, 500);
		} catch (Exception e) {
			System.err.println("【警告】同步到MES库失败: " + e.getMessage());
			log.info("【警告】同步到MES库失败: " + e.getMessage(), e);
			return -3;
		}
		if (!insertOk) {
			System.err.println("【警告】saveOrUpdateBatch 执行失败，未同步到MES库！");
			log.info("【警告】saveOrUpdateBatch 执行失败，未同步到MES库！");
			return -4;
		}

		// 7. 插入成功才回写ERP
		try {
			mesToErpDataMapper.materUpdate(allCodes);
		} catch (Exception e) {
			System.err.println("【警告】回写ERP同步标记失败: " + e.getMessage());
			log.info("【警告】回写ERP同步标记失败: " + e.getMessage(), e);
			return -5;
		}

		// 8. 返回成功条数
		return allCodes.size();
	}


	//===================bom==================

	/**
	 * bom同步 V1.0
	 * 仅同步 ERP（JSPBOM）表 BYTSTATUS=0 的BOM用料树
	 * JSPBOM (ERP) :   拉取 BOM 用料数据（BYTSTATUS=0）
	 * mes_item_use  ： INSERT 或 UPDATE（批量）：插入或更新 MES 的用料明细（树的跟节点）
	 *
	 *
	 * @return 同步数量
	 */
	public int syncBomTree() {
		// 1. 拉ERP BOM用料
		List<Map<String, Object>> bomList = mesToErpDataMapper.bomMessage();
		System.out.println("ERP-BOM拉取结果：" + bomList);
		if (bomList == null || bomList.isEmpty()) {
//			System.err.println("【警告】ERP未查到可同步的bom数据！");
//			log.info("【警告】ERP未查到可同步的bom数据！");
			return 0;
		}

		List<Integer> bomIdList = new ArrayList<>();
		List<MesItemUse> saveList = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();

		for (Map<String, Object> row : bomList) {
			bomIdList.add(Integer.valueOf(row.get("LNGBOMID").toString()));
			MesItemUse use = null;
			// 查重（父+子唯一）
			String itemNo = row.get("STRITEMCODE").toString();
			String useItemNo = row.get("STRNEXTITEMCODE").toString();

			List<MesItemUse> existList = mesItemUseService.list(
					new LambdaQueryWrapper<MesItemUse>()
							.eq(MesItemUse::getItemNo, itemNo)
							.eq(MesItemUse::getUseItemNo, useItemNo)
			);
			if (existList != null && !existList.isEmpty()) {
				use = existList.get(0);
			} else {
				use = new MesItemUse();
				use.setId(UUID.randomUUID().toString().replace("-", ""));
			}

			// 字段填充
			use.setItemNo(itemNo);
			use.setUseItemNo(useItemNo);
			use.setUseItemCount(new BigDecimal(row.get("DBLQUANTITY").toString()));
			use.setVariUse(BigDecimal.ZERO);
			use.setFixedUse(new BigDecimal(row.get("DBLQUANTITY").toString()));
			use.setUseItemMeasure(row.get("STRNEXTITEMUNIT").toString());
			use.setItemMeasureAssist(row.get("STRUNITNAMEAUX").toString());
			use.setFixedUseAssist(new BigDecimal(row.get("DBLQUANTITYAUX").toString()));
			use.setVariUseAssist(BigDecimal.ZERO);
			// 类型
			String itemStyle = row.get("BYTITEMSOURCE").toString();
			use.setUseItemType(itemStyle.equals("0") ? "00" : "01");
			// 时间
			use.setUpdatedTime(now);

			saveList.add(use);
		}

		// 2. 保存到MES
		if (saveList.isEmpty()) {
			System.err.println("【警告】无有效用料明细需要同步！");
			log.info("【警告】无有效用料明细需要同步！");
			return 0;
		}
		mesItemUseService.saveOrUpdateBatch(saveList);

		// 3. 分批回写ERP
		int batch = 1000;
		for (int i = 0; i < bomIdList.size(); i += batch) {
			List<Integer> subIds = bomIdList.subList(i, Math.min(i + batch, bomIdList.size()));
			mesToErpDataMapper.bomUpdate(subIds);
		}
 		log.info("【警告】BOM用料同步完成，同步数={}", saveList.size());
		return saveList.size();
	}



	//===================工序==================


	/**
	 * 工序同步 V1.7
	 * 同步ERP工序表（JSPBOMROUTER）到MES工序表（mes_procedure）。
	 * 只同步ERP端 BYTSTATUS=1 的工序数据（未同步/有变更），同步完成后回写BYTSTATUS=0。
	 * 唯一性判定：bom_no（对应STRBOMCODE）+ procedure_code（对应STRROUTECODE）。
	 * 整改： 删除条件不再是item_no + procedure_code   而是 item_no
	 *
	 */
	public int syncProcedure() {
		// =============== 1. 数据准备 ===============
		// 1.1 拉取ERP数据（Oracle）
		List<Map<String, Object>> erpRouterList;
		try {
			erpRouterList = mesToErpDataMapper.bomRouter();
			log.info("[工序同步] ERP拉取数量：{}", erpRouterList == null ? 0 : erpRouterList.size());
		} catch (Exception e) {
			log.error("[工序同步] ERP数据拉取失败", e);
			return -1;
		}

		if (CollectionUtils.isEmpty(erpRouterList)) {
			log.info("[工序同步] 无待同步数据");
			return 0;
		}

		// 1.2 初始化容器
		LocalDateTime now = LocalDateTime.now();
		List<Integer> successRouterIds = new ArrayList<>();
		List<MesProcedure> toSave = new ArrayList<>();

		// =============== 2. 预加载优化 ===============
		// 2.1 预加载BOM映射（MySQL）
		Set<String> distinctBomNos = new HashSet<>();
		for (Map<String, Object> item : erpRouterList) {
			distinctBomNos.add(item.get("STRBOMCODE").toString());
		}
		Map<String, MesItemStock> bomToStock = mesItemStockService.getByBomNos(new ArrayList<>(distinctBomNos));

		// 2.2 获取所有需要同步的工序键
		Set<String> erpItemNos = new HashSet<>();
		for (Map<String, Object> erpItem : erpRouterList) {
			String bomNo = erpItem.get("STRBOMCODE").toString();
			MesItemStock stock = bomToStock.get(bomNo);
			if (stock != null && StringUtils.isNotEmpty(stock.getItemNo())) {
				erpItemNos.add(stock.getItemNo());
			}
		}


		// =============== 3. 先删除MES中已存在的工序 ===============
		if (!erpItemNos.isEmpty()) {
			try {
				List<String> itemNos = new ArrayList<>(erpItemNos);
				int batchSize = 1000;
				int totalDeleted = 0;
				for (int i = 0; i < itemNos.size(); i += batchSize) {
					List<String> batch = itemNos.subList(i, Math.min(i + batchSize, itemNos.size()));
					int deletedCount = mesProcedureMapper.deleteByItemNos(batch);
					totalDeleted += deletedCount;
				}
				log.info("[工序同步] 已按 item_no 删除工序记录，合计删除 {}", totalDeleted);
			} catch (Exception e) {
				log.error("[工序同步] 删除旧工序失败", e);
				return -3;
			}
		}


		// =============== 4. 构建新增数据 ===============
		for (Map<String, Object> erpItem : erpRouterList) {
			String bomNo = erpItem.get("STRBOMCODE").toString();
			MesItemStock stock = bomToStock.get(bomNo);

			// 4.1 跳过无效BOM
			if (stock == null || StringUtils.isEmpty(stock.getItemNo())) {
				log.warn("[工序同步] 跳过无效BOM: {}", bomNo);
				continue;
			}

			// 4.2 创建新工序实体（不再检查是否存在）
			MesProcedure p = new MesProcedure();
			p.setId(UUID.randomUUID().toString().replace("-", ""));
			p.setCreatedBy("1");
			p.setCreatedTime(now);

			// 字段映射    todo: 参考我给你原始代码  来补齐字段
			p.setBomNo(bomNo);
			p.setItemNo(stock.getItemNo());
			p.setProcedureCode(erpItem.get("STRROUTECODE").toString());
			p.setProcedureName(erpItem.get("STRROUTENAME").toString());
			p.setIsValid("01");
			p.setUpdatedBy("1");
			p.setUpdatedTime(now);

			// 序号
			p.setSeqNo(safeParseInt(erpItem.get("LNGORDER")));
			// 加工工时（定额工时）
			p.setHoursFixed(safeParseDecimal(erpItem.get("DBLROUTERATIONTIME")));
			// 实际工时
			//p.setHoursWork(safeParseDecimal(erpItem.get("DBLROUTEPROCESSTIME"))); // 注意这里和hoursFixed使用相同字段
			p.setHoursWork(safeParseDecimal(erpItem.get("DBLROUTERATIONTIME"))); // 临时补丁，注意这里和hoursFixed使用相同字段


			// 准备工时
			p.setHoursPrepare(safeParseDecimal(erpItem.get("DBLROUTEPREPARETIME")));


			// 工作车间Id
			String deptName = erpItem.get("STRDEPARTMENTNAME") == null ? "" : erpItem.get("STRDEPARTMENTNAME").toString();
			if ("制造部".equals(deptName)) {
				p.setDeptId("312905765054574592"); // 制造部固定id
			} else {
				p.setDeptId("316142126431625216"); // 其他部门
			}

			// 设备Id（实际映射需你补充具体逻辑）
			// 参考片段：procedureItem.setDeviceId(getDeviceName(jspBomRouter.get("STRROUTECODE").toString()));
			p.setDeviceId(getDeviceName(erpItem.get("STRROUTECODE").toString())); // 需提供getDeviceName方法



			toSave.add(p);
			successRouterIds.add(safeParseInt(erpItem.get("LNGBOMID")));
		}

		// =============== 5. 批量新增数据 ===============
		if (CollectionUtils.isEmpty(toSave)) {
			log.warn("[工序同步] 无有效工序需要保存");
			return 0;
		}

		int saveCount;
		try {
			boolean saveResult = mesProcedureService.saveBatch(toSave, 500); // 使用saveBatch而非saveOrUpdateBatch
			saveCount = saveResult ? toSave.size() : 0;
			log.info("[工序同步] 成功新增{}条工序", saveCount);
		} catch (Exception e) {
			log.error("[工序同步] 新增工序失败", e);
			return -3;
		}

		// =============== 6. 回写ERP（Oracle） ===============
		if (CollectionUtils.isNotEmpty(successRouterIds)) {
			try {
				int updateCount = mesToErpDataMapper.routerUpdate(successRouterIds);
				log.info("[工序同步] 已回写{}条ERP状态", updateCount);
			} catch (Exception e) {
				log.error("[工序同步] ERP回写失败", e);
				return -4;
			}
		}

		return saveCount;
	}




	/**
	 * 工序同步 V1.6
	 * 同步ERP工序表（JSPBOMROUTER）到MES工序表（mes_procedure）。
	 * 只同步ERP端 BYTSTATUS=1 的工序数据（未同步/有变更），同步完成后回写BYTSTATUS=0。
	 * 唯一性判定：bom_no（对应STRBOMCODE）+ procedure_code（对应STRROUTECODE）。
	 * 整改： 整改： 仅处理新增/修改，不处理删除逻辑 （中间表没有删除的标志）
	 * 将 item_no + procedure_code 作为唯一键集合，然后批量删除 mes_procedure 中已存在的对应工序（分批防止 SQL 过长）。
	 *
	 */
	public int syncProcedure16() {
		// =============== 1. 数据准备 ===============
		// 1.1 拉取ERP数据（Oracle）
		List<Map<String, Object>> erpRouterList;
		try {
			erpRouterList = mesToErpDataMapper.bomRouter();
			log.info("[工序同步] ERP拉取数量：{}", erpRouterList == null ? 0 : erpRouterList.size());
		} catch (Exception e) {
			log.error("[工序同步] ERP数据拉取失败", e);
			return -1;
		}

		if (CollectionUtils.isEmpty(erpRouterList)) {
			log.info("[工序同步] 无待同步数据");
			return 0;
		}

		// 1.2 初始化容器
		LocalDateTime now = LocalDateTime.now();
		List<Integer> successRouterIds = new ArrayList<>();
		List<MesProcedure> toSave = new ArrayList<>();

		// =============== 2. 预加载优化 ===============
		// 2.1 预加载BOM映射（MySQL）
		Set<String> distinctBomNos = new HashSet<>();
		for (Map<String, Object> item : erpRouterList) {
			distinctBomNos.add(item.get("STRBOMCODE").toString());
		}
		Map<String, MesItemStock> bomToStock = mesItemStockService.getByBomNos(new ArrayList<>(distinctBomNos));

		// 2.2 获取所有需要同步的工序键
		Set<String> erpProcedureKeys = new HashSet<>();
		for (Map<String, Object> erpItem : erpRouterList) {
			String bomNo = erpItem.get("STRBOMCODE").toString();
			MesItemStock stock = bomToStock.get(bomNo);
			if (stock != null && StringUtils.isNotEmpty(stock.getItemNo())) {
				erpProcedureKeys.add(stock.getItemNo() + "|" + erpItem.get("STRROUTECODE").toString());
			}
		}

		// =============== 3. 先删除MES中已存在的工序 ===============
		if (!erpProcedureKeys.isEmpty()) {
			try {
				// 拆分键值对
				List<String> itemNos = new ArrayList<>();
				List<String> procedureCodes = new ArrayList<>();
				for (String key : erpProcedureKeys) {
					String[] parts = key.split("\\|");
					itemNos.add(parts[0]);
					procedureCodes.add(parts[1]);
				}

				// 安全删除（检查列表大小一致）
				if (itemNos.size() == procedureCodes.size()) {
					int deletedCount = mesProcedureMapper.deleteByItemNoAndProcedureCode(
							itemNos.subList(0, Math.min(itemNos.size(), 1000)), // 分批防止SQL过长
							procedureCodes.subList(0, Math.min(procedureCodes.size(), 1000))
					);
					log.info("[工序同步] 已删除{}条旧工序", deletedCount);
				} else {
					log.error("[工序同步] 数据不匹配: itemNos={}, codes={}", itemNos.size(), procedureCodes.size());
					return -2;
				}
			} catch (Exception e) {
				log.error("[工序同步] 删除旧工序失败", e);
				return -3;
			}
		}

		// =============== 4. 构建新增数据 ===============
		for (Map<String, Object> erpItem : erpRouterList) {
			String bomNo = erpItem.get("STRBOMCODE").toString();
			MesItemStock stock = bomToStock.get(bomNo);

			// 4.1 跳过无效BOM
			if (stock == null || StringUtils.isEmpty(stock.getItemNo())) {
				log.warn("[工序同步] 跳过无效BOM: {}", bomNo);
				continue;
			}

			// 4.2 创建新工序实体（不再检查是否存在）
			MesProcedure p = new MesProcedure();
			p.setId(UUID.randomUUID().toString().replace("-", ""));
			p.setCreatedBy("1");
			p.setCreatedTime(now);

			// 字段映射    todo: 参考我给你原始代码  来补齐字段
			p.setBomNo(bomNo);
			p.setItemNo(stock.getItemNo());
			p.setProcedureCode(erpItem.get("STRROUTECODE").toString());
			p.setProcedureName(erpItem.get("STRROUTENAME").toString());
			p.setIsValid("01");
			p.setUpdatedBy("1");
			p.setUpdatedTime(now);

			// 序号
			p.setSeqNo(safeParseInt(erpItem.get("LNGORDER")));
			// 加工工时（定额工时）
			p.setHoursFixed(safeParseDecimal(erpItem.get("DBLROUTERATIONTIME")));
			// 实际工时
			//p.setHoursWork(safeParseDecimal(erpItem.get("DBLROUTEPROCESSTIME"))); // 注意这里和hoursFixed使用相同字段
			p.setHoursWork(safeParseDecimal(erpItem.get("DBLROUTERATIONTIME"))); // 临时补丁，注意这里和hoursFixed使用相同字段


			// 准备工时
			p.setHoursPrepare(safeParseDecimal(erpItem.get("DBLROUTEPREPARETIME")));


			// 工作车间Id
			String deptName = erpItem.get("STRDEPARTMENTNAME") == null ? "" : erpItem.get("STRDEPARTMENTNAME").toString();
			if ("制造部".equals(deptName)) {
				p.setDeptId("312905765054574592"); // 制造部固定id
			} else {
				p.setDeptId("316142126431625216"); // 其他部门
			}

			// 设备Id（实际映射需你补充具体逻辑）
			// 参考片段：procedureItem.setDeviceId(getDeviceName(jspBomRouter.get("STRROUTECODE").toString()));
			p.setDeviceId(getDeviceName(erpItem.get("STRROUTECODE").toString())); // 需提供getDeviceName方法



			toSave.add(p);
			successRouterIds.add(safeParseInt(erpItem.get("LNGBOMID")));
		}

		// =============== 5. 批量新增数据 ===============
		if (CollectionUtils.isEmpty(toSave)) {
			log.warn("[工序同步] 无有效工序需要保存");
			return 0;
		}

		int saveCount;
		try {
			boolean saveResult = mesProcedureService.saveBatch(toSave, 500); // 使用saveBatch而非saveOrUpdateBatch
			saveCount = saveResult ? toSave.size() : 0;
			log.info("[工序同步] 成功新增{}条工序", saveCount);
		} catch (Exception e) {
			log.error("[工序同步] 新增工序失败", e);
			return -3;
		}

		// =============== 6. 回写ERP（Oracle） ===============
		if (CollectionUtils.isNotEmpty(successRouterIds)) {
			try {
				int updateCount = mesToErpDataMapper.routerUpdate(successRouterIds);
				log.info("[工序同步] 已回写{}条ERP状态", updateCount);
			} catch (Exception e) {
				log.error("[工序同步] ERP回写失败", e);
				return -4;
			}
		}

		return saveCount;
	}



	private BigDecimal safeParseDecimal(Object value) {
		try {
			return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
		} catch (Exception e) {
			log.warn("数值转换失败: {}", value);
			return BigDecimal.ZERO;
		}
	}


	/**
	 * 工序同步 V1.5
	 * 同步ERP工序表（JSPBOMROUTER）到MES工序表（mes_procedure）。
	 * 只同步ERP端 BYTSTATUS=0 的工序数据（未同步/有变更），同步完成后回写BYTSTATUS=1。
	 * 唯一性判定：bom_no（对应STRBOMCODE）+ procedure_code（对应STRROUTECODE）。
	 * 整改： 仅处理新增/修改，不处理删除逻辑 （中间表没有删除的标志）
	 *
	 */
	public int syncProcedure15() {
		// =============== 1. 数据准备 ===============
		// 1.1 拉取ERP数据（Oracle）
		List<Map<String, Object>> erpRouterList;
		try {
			erpRouterList = mesToErpDataMapper.bomRouter();
			log.info("[工序同步] ERP拉取数量：{}", erpRouterList == null ? 0 : erpRouterList.size());
		} catch (Exception e) {
			log.error("[工序同步] ERP数据拉取失败", e);
			return -1;
		}

		if (CollectionUtils.isEmpty(erpRouterList)) {
			log.info("[工序同步] 无待同步数据");
			return 0;
		}

		// 1.2 初始化容器
		LocalDateTime now = LocalDateTime.now();
		List<Integer> successRouterIds = new ArrayList<>();
		List<MesProcedure> toSave = new ArrayList<>();

		// =============== 2. 预加载优化 ===============
		// 2.1 预加载BOM映射（MySQL）
		Set<String> distinctBomNos = new HashSet<>();
		for (Map<String, Object> item : erpRouterList) {
			distinctBomNos.add(item.get("STRBOMCODE").toString());
		}
		Map<String, MesItemStock> bomToStock = mesItemStockService.getByBomNos(new ArrayList<>(distinctBomNos));

		// 2.2 预加载MES现有有效工序（仅is_valid=01）
		Map<String, MesProcedure> existingMap = new HashMap<>();
		List<MesProcedure> existingProcedures = mesProcedureService.list(
				new LambdaQueryWrapper<MesProcedure>()
						.eq(MesProcedure::getIsValid, "01")
		);
		for (MesProcedure p : existingProcedures) {
			existingMap.put(p.getItemNo() + "|" + p.getProcedureCode(), p);
		}

		// =============== 3. 构建同步数据（仅新增/修改） ===============
		for (Map<String, Object> erpItem : erpRouterList) {
			String bomNo = erpItem.get("STRBOMCODE").toString();
			MesItemStock stock = bomToStock.get(bomNo);

			// 3.1 跳过无效BOM
			if (stock == null || StringUtils.isEmpty(stock.getItemNo())) {
				log.warn("[工序同步] 跳过无效BOM: {}", bomNo);
				continue;
			}

			// 3.2 构建唯一键
			String itemNo = stock.getItemNo();
			String procedureCode = erpItem.get("STRROUTECODE").toString();
			String uniqueKey = itemNo + "|" + procedureCode;

			// 3.3 创建/更新工序实体
			MesProcedure p = existingMap.getOrDefault(uniqueKey, new MesProcedure());
			if (p.getId() == null) {
				p.setId(UUID.randomUUID().toString().replace("-", ""));
				p.setCreatedBy("1");
				p.setCreatedTime(now);
			}

			// 字段映射
			p.setBomNo(bomNo);
			p.setItemNo(itemNo);
			p.setProcedureCode(procedureCode);
			p.setProcedureName(erpItem.get("STRROUTENAME").toString());
			p.setIsValid("01");
			p.setUpdatedBy("1");
			p.setUpdatedTime(now);

			// 数值处理
			p.setSeqNo(parseInt(erpItem.get("LNGORDER")));
			p.setHoursFixed(parseDecimal(erpItem.get("DBLROUTERATIONTIME")));
			p.setHoursWork(parseDecimal(erpItem.get("DBLROUTEPROCESSTIME")));

			toSave.add(p);
			successRouterIds.add(parseInt(erpItem.get("LNGBOMID")));
		}

		// =============== 4. 数据持久化 ===============
		if (CollectionUtils.isEmpty(toSave)) {
			log.warn("[工序同步] 无有效工序需要保存");
			return 0;
		}

		// 4.1 批量保存（非事务）
		int saveCount;
		try {
			boolean saveResult = mesProcedureService.saveOrUpdateBatch(toSave, 500);
			saveCount = saveResult ? toSave.size() : 0;
		} catch (Exception e) {
			log.error("[工序同步] 保存异常", e);
			return -3;
		}

		// =============== 5. 回写ERP（Oracle） ===============
		if (CollectionUtils.isNotEmpty(successRouterIds)) {
			try {
				int updateCount = mesToErpDataMapper.routerUpdate(successRouterIds);
				log.info("[工序同步] 已回写{}条ERP状态", updateCount);
			} catch (Exception e) {
				log.error("[工序同步] ERP回写失败", e);
				return -4;
			}
		}

		return saveCount;
	}

	// 在当前类中添加这两个方法（替换原错误调用）
	private int parseInt(Object value) {
		try {
			return value != null ? Integer.parseInt(value.toString()) : 0;
		} catch (Exception e) {
			log.warn("数值转换失败: {}", value);
			return 0;
		}
	}

	private BigDecimal parseDecimal(Object value) {
		try {
			return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
		} catch (Exception e) {
			log.warn("数值转换失败: {}", value);
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 工序同步 V1.3
	 *使用BomNo索引，并保留bomNo字段备用
	 * 同步ERP工序表（JSPBOMROUTER）到MES工序表（mes_procedure）。
	 * 只同步ERP端 BYTSTATUS=0 的工序数据（未同步/有变更），同步完成后回写BYTSTATUS=1。
	 * 唯一性判定：bom_no（对应STRBOMCODE）+ procedure_code（对应STRROUTECODE）。
	 * 问题： erp表中删除这条数据， mes中同步失败！
	 */
	public int syncProcedure13() {
		// =============== 1. 数据准备 ===============
		// 1.1 拉取ERP数据
		List<Map<String, Object>> erpRouterList;
		try {
			erpRouterList = mesToErpDataMapper.bomRouter();
			log.info("[工序同步] ERP拉取数量：{}", erpRouterList.size());
		} catch (Exception e) {
			log.error("[工序同步] ERP数据拉取失败", e);
			return -1; // 错误码-1表示ERP拉取失败
		}

		if (CollectionUtils.isEmpty(erpRouterList)) { // 使用CollectionUtils判空
			log.info("[工序同步] 无待同步数据");
			return 0;
		}

		// 1.2 初始化容器
		LocalDateTime now = LocalDateTime.now();
		List<Integer> successRouterIds = new ArrayList<>();
		List<MesProcedure> toSave = new ArrayList<>();

		// =============== 2. 预加载优化 ===============
		// 2.1 预加载BOM映射
		Set<String> distinctBomNos = erpRouterList.stream()
				.map(r -> r.get("STRBOMCODE").toString())
				.collect(Collectors.toSet());
		Map<String, MesItemStock> bomToStock = mesItemStockService.getByBomNos(new ArrayList<>(distinctBomNos));

		// 2.2 预加载现有有效工序
		List<MesProcedure> existingProcedures = mesProcedureService.list(
				new LambdaQueryWrapper<MesProcedure>()
						.eq(MesProcedure::getIsValid, "01")
		);

		// =============== 3. 失效旧数据 ===============
		Set<String> erpProcedureKeys = erpRouterList.stream()
				.map(r -> buildProcedureKey(
						r.get("STRBOMCODE").toString(),
						r.get("STRROUTECODE").toString()))
				.collect(Collectors.toSet());

		List<String> invalidIds = existingProcedures.stream()
				.filter(p -> !erpProcedureKeys.contains(buildProcedureKey(p.getBomNo(), p.getProcedureCode())))
				.map(MesProcedure::getId)
				.collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(invalidIds)) {
			boolean invalidCount = mesProcedureService.update(
					new LambdaUpdateWrapper<MesProcedure>()
							.set(MesProcedure::getIsValid, "00")
							.in(MesProcedure::getId, invalidIds)
			);
			log.info("[工序同步] 已失效{}条旧工序", invalidCount);
		}

		// =============== 4. 构建同步数据 ===============
		for (Map<String, Object> erpItem : erpRouterList) {
			String bomNo = erpItem.get("STRBOMCODE").toString();
			MesItemStock stock = bomToStock.get(bomNo);

			// 4.1 跳过无效BOM
			if (stock == null || StringUtils.isEmpty(stock.getItemNo())) {
				log.warn("[工序同步] 跳过无效BOM: {}", bomNo);
				continue;
			}

			// 4.2 构建工序唯一标识
			String procedureCode = erpItem.get("STRROUTECODE").toString();
			String uniqueKey = buildProcedureKey(stock.getItemNo(), procedureCode);

			// 4.3 查找现有工序（利用预加载数据优化性能）
			MesProcedure exist = existingProcedures.stream()
					.filter(p -> uniqueKey.equals(buildProcedureKey(p.getItemNo(), p.getProcedureCode())))
					.findFirst()
					.orElse(null);

			// 4.4 创建/更新工序实体
			MesProcedure p = exist != null ? exist : new MesProcedure();
			if (p.getId() == null) {
				p.setId(UUID.randomUUID().toString().replace("-", ""));
				p.setCreatedBy("1");
				p.setCreatedTime(now);
			}

			// 4.5 字段映射
			p.setBomNo(bomNo);
			p.setItemNo(stock.getItemNo());
			p.setProcedureCode(procedureCode);
			p.setProcedureName(erpItem.get("STRROUTENAME").toString());
			p.setSeqNo(safeParseInt(erpItem.get("LNGORDER")));
			p.setHoursFixed(safeParseBigDecimal(erpItem.get("DBLROUTERATIONTIME")));
			p.setHoursWork(safeParseBigDecimal(erpItem.get("DBLROUTEPROCESSTIME")));
			p.setIsValid("01");
			p.setUpdatedBy("1");
			p.setUpdatedTime(now);

			toSave.add(p);
			successRouterIds.add(safeParseInt(erpItem.get("LNGBOMID"))); // 只记录成功同步的ID
		}

		// =============== 5. 数据持久化 ===============
		if (CollectionUtils.isEmpty(toSave)) {
			log.warn("[工序同步] 无有效工序需要保存");
			return 0;
		}

		try {
			boolean saveResult = mesProcedureService.saveOrUpdateBatch(toSave, 500);
			if (!saveResult) {
				log.error("[工序同步] 批量保存失败");
				return -2; // 错误码-2表示保存失败
			}
			log.info("[工序同步] 成功保存{}条工序", toSave.size());
		} catch (Exception e) {
			log.error("[工序同步] 保存异常", e);
			return -3; // 错误码-3表示保存异常
		}

		// =============== 6. 回写ERP ===============
		if (CollectionUtils.isNotEmpty(successRouterIds)) {
			try {
				mesToErpDataMapper.routerUpdate(successRouterIds);
				log.info("[工序同步] 已回写{}条ERP状态", successRouterIds.size());
			} catch (Exception e) {
				log.error("[工序同步] ERP回写失败", e);
				return -4; // 错误码-4表示回写失败
			}
		}

		return toSave.size(); // 返回成功同步数量
	}

	// =============== 工具方法 ===============
	private String buildProcedureKey(String itemNo, String procedureCode) {
		return itemNo + "|" + procedureCode;
	}

	private BigDecimal safeParseBigDecimal(Object value) {
		try {
			return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
		} catch (Exception e) {
			log.warn("[工序同步] 数值转换失败: {}", value);
			return BigDecimal.ZERO;
		}
	}

	private Integer safeParseInt(Object value) {
		try {
			return value != null ? Integer.valueOf(value.toString()) : 0;
		} catch (Exception e) {
			log.warn("[工序同步] 整数转换失败: {}", value);
			return 0;
		}
	}

	/**
	 * 工序同步 V1.2
	 *使用BomNo索引，并保留bomNo字段备用
	 * 同步ERP工序表（JSPBOMROUTER）到MES工序表（mes_procedure）。
	 * 只同步ERP端 BYTSTATUS=0 的工序数据（未同步/有变更），同步完成后回写BYTSTATUS=1。
	 * 唯一性判定：bom_no（对应STRBOMCODE）+ procedure_code（对应STRROUTECODE）。
	 */
	public int syncProcedureV12() {
		// 1. 拉取 ERP 侧待同步（BYTSTATUS=0）的工序数据
		List<Map<String, Object>> erpRouterList = mesToErpDataMapper.bomRouter();
		System.out.println("ERP-工序拉取结果：" + erpRouterList);
		if (erpRouterList == null || erpRouterList.isEmpty()) {
//			System.err.println("【警告】ERP未查到可同步的工序数据！");
//			log.info("【警告】ERP未查到可同步的工序数据！");
			return 0;
		}

		// 用于批量回写ERP的BOM工序ID集合
		List<Integer> routerIdList = new ArrayList<>();
		// MES侧待保存/更新的工序实体集合
		List<MesProcedure> saveList = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();

		// 预取所有需要的 BOM→MesItemStock 映射，避免循环内重复查询
		List<String> allBomNos = erpRouterList.stream()
				.map(r -> r.get("STRBOMCODE").toString())
				.distinct()
				.collect(Collectors.toList());
		Map<String, MesItemStock> bomToStock = mesItemStockService.getByBomNos(allBomNos);

		for (Map<String, Object> row : erpRouterList) {
			// 记录 ERP 工序 ID（LNGBOMID），后续用于回写同步状态
			routerIdList.add(Integer.valueOf(row.get("LNGBOMID").toString()));

			// --- 新增：先通过 BOM 号映射到 MES 的 item_no ---
			String bomNo = row.get("STRBOMCODE").toString();
			MesItemStock stock = bomToStock.get(bomNo);
			// 若在 MES 中找不到对应物料，则跳过此条工序
			if (stock == null || stock.getItemNo() == null || stock.getItemNo().isEmpty()) {
				System.err.println("【警告】BOM编码[" + bomNo + "]在MES中找不到对应物料，跳过同步！");
				log.info("【警告】BOM编码[{}]在MES中找不到对应物料，跳过同步！", bomNo);
				continue;
			}
			String itemNo = stock.getItemNo();

			// 唯一性判定：使用 item_no + procedure_code 保证幂等
			String procedureCode = row.get("STRROUTECODE").toString();
			LambdaQueryWrapper<MesProcedure> qw = new LambdaQueryWrapper<MesProcedure>()
					.eq(MesProcedure::getItemNo, itemNo)
					.eq(MesProcedure::getProcedureCode, procedureCode);
			// getOne(qw, false)：只要有一条就返回，不抛异常
			MesProcedure exist = mesProcedureService.getOne(qw, false);

			// 如果已存在则复用，否则新建
			MesProcedure p = exist != null ? exist : new MesProcedure();
			if (p.getId() == null) {
				// 新建时要填充主键、创建人、创建时间
				p.setId(UUID.randomUUID().toString().replace("-", ""));
				p.setCreatedBy("1");
				p.setCreatedTime(now);
			}

			// 将原始 ERP BOM 编码存入 bom_no 字段（保留来源信息）
			p.setBomNo(bomNo);         // 【修正点1：保留 STRBOMCODE】
			p.setItemNo(itemNo);       // 【修正点2：使用 MES item_no 进行关联】
			p.setProcedureCode(procedureCode);
			p.setSeqNo(Integer.valueOf(row.get("LNGORDER").toString()));
			p.setProcedureName(row.get("STRROUTENAME").toString());

			// 加工工时 → hoursFixed
			String time = row.get("DBLROUTERATIONTIME") == null ? "0" : row.get("DBLROUTERATIONTIME").toString();
			p.setHoursFixed(new BigDecimal(time));

			// 工作工时 → hoursWork
			String processTime = row.get("DBLROUTEPROCESSTIME") == null ? "0" : row.get("DBLROUTEPROCESSTIME").toString();
			p.setHoursWork(new BigDecimal(processTime));

			// 准备工时 → hoursPrepare
			String prepTime = row.get("DBLROUTEPREPARETIME") == null ? "0" : row.get("DBLROUTEPREPARETIME").toString();
			p.setHoursPrepare(new BigDecimal(prepTime));

			// 部门映射：制造部 / 其他
			String deptName = row.get("STRDEPARTMENTNAME") == null ? "" : row.get("STRDEPARTMENTNAME").toString();
			p.setDeptId("制造部".equals(deptName)
					? "312905765054574592"
					: "316142126431625216");

			// 设备映射（保持原逻辑，可根据实际重写）
			p.setDeviceId(getDeviceName(procedureCode));

			// 更新人、更新时间赋值
			p.setUpdatedBy("1");
			p.setUpdatedTime(now);

			saveList.add(p);
		}

		// 批量保存或更新到 MES 工序表
		if (saveList.isEmpty()) {
			System.err.println("【警告】无有效工序数据需要同步！");
			log.info("【警告】无有效工序数据需要同步！");
			return 0;
		}
		mesProcedureService.saveOrUpdateBatch(saveList);

		// 批量回写 ERP 工序表同步状态 BYTSTATUS → 1
		if (!routerIdList.isEmpty()) {
			mesToErpDataMapper.routerUpdate(routerIdList);
		}

//		System.err.println("【警告】工序同步完成，同步数=" + saveList.size());
//		log.info("【警告】工序同步完成，同步数={}", saveList.size());
		return saveList.size();
	}

	/**
	 * 工序同步 V1.1
	 *使用bomNo索引
	 * 同步ERP工序表（JSPBOMROUTER）到MES工序表（mes_procedure）。
	 * 只同步ERP端 BYTSTATUS=0 的工序数据（未同步/有变更），同步完成后回写BYTSTATUS=1。
	 * 唯一性判定：bom_no（对应STRBOMCODE）+ procedure_code（对应STRROUTECODE）。
	 */
	public int syncProcedureBomNo() {
		// 1. 拉取ERP侧待同步（BYTSTATUS=0）的工序数据
		List<Map<String, Object>> erpRouterList = mesToErpDataMapper.bomRouter();
		if (erpRouterList == null || erpRouterList.isEmpty()) {
			log.info("ERP工序数据为空, 不做同步");
			return 0;
		}

		List<Integer> routerIdList = new ArrayList<>();
		List<MesProcedure> saveList = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();

		for (Map<String, Object> row : erpRouterList) {
			// 记录ERP工序ID（LNGBOMID），用于批量回写状态
			routerIdList.add(Integer.valueOf(row.get("LNGBOMID").toString()));

			// 1. 唯一性判定：bom_no + procedure_code
			String bomNo = row.get("STRBOMCODE").toString();
			String procedureCode = row.get("STRROUTECODE").toString();
			LambdaQueryWrapper<MesProcedure> qw = new LambdaQueryWrapper<MesProcedure>()
					.eq(MesProcedure::getBomNo, bomNo)
					.eq(MesProcedure::getProcedureCode, procedureCode);
			MesProcedure exist = mesProcedureService.getOne(qw, false);

			// 2. 新增或更新实体
			MesProcedure p = exist != null ? exist : new MesProcedure();
			if (p.getId() == null) {
				p.setId(UUID.randomUUID().toString().replace("-", ""));
				p.setCreatedBy("1");
				p.setCreatedTime(now);
			}
			p.setBomNo(bomNo); // 【修正点】存STRBOMCODE到bom_no字段
			p.setProcedureCode(procedureCode); // 工序编码
			p.setSeqNo(Integer.valueOf(row.get("LNGORDER").toString())); // 顺序号
			p.setProcedureName(row.get("STRROUTENAME").toString()); // 工序名称

			// 工时字段映射
			String time = row.get("DBLROUTERATIONTIME") == null ? "0" : row.get("DBLROUTERATIONTIME").toString();
			p.setHoursFixed(new BigDecimal(time));

			String processTime = row.get("DBLROUTEPROCESSTIME") == null ? "0" : row.get("DBLROUTEPROCESSTIME").toString();
			p.setHoursWork(new BigDecimal(processTime));

			String prepTime = row.get("DBLROUTEPREPARETIME") == null ? "0" : row.get("DBLROUTEPREPARETIME").toString();
			p.setHoursPrepare(new BigDecimal(prepTime));

			// 部门、设备
			String deptName = row.get("STRDEPARTMENTNAME") == null ? "" : row.get("STRDEPARTMENTNAME").toString();
			p.setDeptId("制造部".equals(deptName) ? "312905765054574592" : "316142126431625216");
			p.setDeviceId(getDeviceName(procedureCode)); // 【原逻辑】

			// 更新时间与更新人
			p.setUpdatedBy("1");
			p.setUpdatedTime(now);

			saveList.add(p);
		}

		// 3. 批量保存/更新
		if (!saveList.isEmpty()) {
			mesProcedureService.saveOrUpdateBatch(saveList);
		}

		// 4. 回写ERP工序表同步状态
		if (!routerIdList.isEmpty()) {
			mesToErpDataMapper.routerUpdate(routerIdList);
		}

		log.info("工序同步完成, 本次同步: {} 条", saveList.size());
		return saveList.size();
	}


	/**
	 * 工序同步 V1.0
	 *  使用ItemNo索引  并且没有使用BYTSTATUS=0  ，而是使用的全局对比
	 * @return
	 */
	public int syncProcedureItemNo() {
		// 1. 拉取ERP侧待同步的工序数据
		List<Map<String, Object>> erpRouterList = mesToErpDataMapper.bomRouter();
		if (erpRouterList == null || erpRouterList.isEmpty()) {
			log.info("ERP工序数据为空, 不做同步");
			return 0;
		}

		// 用于批量回写ERP的BOM工序ID集合
		List<Integer> routerIdList = new ArrayList<>();
		// MES侧待保存/更新的工序实体集合
		List<MesProcedure> saveList = new ArrayList<>();

		for (Map<String, Object> row : erpRouterList) {
			// ERP工序明细表主键LNGBOMID，后续用于ERP同步状态回写
			routerIdList.add(Integer.valueOf(row.get("LNGBOMID").toString()));

			// 1. 获取产品item_no（通常ERP的STRBOMCODE即MES的item_no）
			String itemNo = row.get("STRBOMCODE").toString();

			// 2. 工序唯一性判断：item_no+procedure_code唯一
			String procedureCode = row.get("STRROUTECODE").toString();
			LambdaQueryWrapper<MesProcedure> qw = new LambdaQueryWrapper<MesProcedure>()
					.eq(MesProcedure::getItemNo, itemNo)
					.eq(MesProcedure::getProcedureCode, procedureCode);
			// getOne(qw, false)：只要有一个返回，不会抛异常
			MesProcedure exist = mesProcedureService.getOne(qw, false);

			// 如果存在则复用，否则新建
			// - 幂等同步，避免重复插入
			MesProcedure p = exist != null ? exist : new MesProcedure();
			if (p.getId() == null) {
				// 主键赋值
				p.setId(UUID.randomUUID().toString().replace("-", ""));
				// 创建人、创建时间赋值
				p.setCreatedBy("1");
				p.setCreatedTime(LocalDateTime.now());
			}
			// 基础字段赋值
			p.setItemNo(itemNo); // 产品编码
			p.setSeqNo(Integer.valueOf(row.get("LNGORDER").toString())); // ERP工序顺序号
			p.setProcedureCode(procedureCode); // ERP工序编码
			p.setProcedureName(row.get("STRROUTENAME").toString()); // ERP工序名称

			// 加工工时(DBLROUTERATIONTIME) → hoursFixed
			String time = row.get("DBLROUTERATIONTIME") == null ? "0" : row.get("DBLROUTERATIONTIME").toString();
			p.setHoursFixed(new BigDecimal(time));

			// 工作工时(DBLROUTEPROCESSTIME) → hoursWork
			String processTime = row.get("DBLROUTEPROCESSTIME") == null ? "0" : row.get("DBLROUTEPROCESSTIME").toString();
			p.setHoursWork(new BigDecimal(processTime));

			// 准备工时(DBLROUTEPREPARETIME) → hoursPrepare
			String prepTime = row.get("DBLROUTEPREPARETIME") == null ? "0" : row.get("DBLROUTEPREPARETIME").toString();
			p.setHoursPrepare(new BigDecimal(prepTime));

			// 部门映射（制造部/其他）
			String deptName = row.get("STRDEPARTMENTNAME") == null ? "" : row.get("STRDEPARTMENTNAME").toString();
			p.setDeptId("制造部".equals(deptName) ? "312905765054574592" : "316142126431625216");

			// 设备编码（通过工序code反查，实际请根据业务重写）  设备映射
			p.setDeviceId(getDeviceName(row.get("STRROUTECODE").toString()));

			// 更新时间与更新人
			p.setUpdatedBy("1");
			p.setUpdatedTime(LocalDateTime.now());
			saveList.add(p);
		}

		// 3. 批量保存/更新到MES工序表
		if (!saveList.isEmpty()) {
			mesProcedureService.saveOrUpdateBatch(saveList);
		}

		// 4. 同步成功后批量回写ERP工序表的同步状态
		if (!routerIdList.isEmpty()) {
			mesToErpDataMapper.routerUpdate(routerIdList);
		}

		log.info("工序同步完成, 本次同步: {} 条", saveList.size());
		return saveList.size();
	}



	//====================================需要整改！=========================================


	// 写死的设备映射表！！   ERP工序编码  =  设备ID
//	private String getDeviceName(String procedureCode) {
//		String resultValue = "";
//		switch (procedureCode) {
//			case "1":
//				resultValue = "424949962023788544";
//				break;
//			case "2":
//				resultValue = "424859821687070720";
//				break;
//			case "3":
//				resultValue = "424949962023788544";
//				break;
//			case "4":
//				resultValue = "424949846894338048";
//				break;
//			case "5":
//				resultValue = "424950828344696832";
//				break;
//			case "8":
//				resultValue = "424871737725706240";
//				break;
//			case "10":
//				resultValue = "424860069373304832";
//				break;
//			case "11":
//				resultValue = "424860069373304832";
//				break;
//			case "12":
//				resultValue = "424859913508773888";
//				break;
//			case "13":
//				resultValue = "425242411535327232";
//				break;
//			case "14":
//				resultValue = "424950493689569280";
//				break;
//			case "16":
//				resultValue = "424951176589369344";
//				break;
//			case "19":
//				resultValue = "424859994123296768";
//				break;
//			case "22":
//				resultValue = "424859821687070720";
//				break;
//			case "23":
//				resultValue = "424859994123296768";
//				break;
//			case "24":
//				resultValue = "424960035571785728";
//				break;
//			case "25":
//				resultValue = "424951282155806720";
//				break;
//			case "26":
//				resultValue = "424973040892141568";
//				break;
//			case "27":
//				resultValue = "425239381603672064";
//				break;
//			case "28":
//				resultValue = "424973656825683968";
//				break;
//			case "29":
//				resultValue = "424974103321927680";
//				break;
//			case "30":
//				resultValue = "424974103321927680";
//				break;
//			case "31":
//				resultValue = "424859706293379072";
//				break;
//			case "32":
//				resultValue = "424950640133693440";
//				break;
//			case "34":
//				resultValue = "424860069373304832";
//				break;
//			case "37":
//				resultValue = "424859913508773888";
//				break;
//			case "38":
//				resultValue = "424950120539119616";
//				break;
//			case "39":
//				resultValue = "424950346687602688";
//				break;
//			case "40":
//				resultValue = "424859706293379072";
//				break;
//			case "42":
//				resultValue = "424859994123296768";
//				break;
//			case "43":
//				resultValue = "424950120539119616";
//				break;
//			case "61":
//				resultValue = "424859913508773888";
//				break;
//			case "63":
//				resultValue = "424974103321927680";
//				break;
//			case "64":
//				resultValue = "424950740994121728";
//				break;
//			case "65":
//				resultValue = "424950932417961984";
//				break;
//			case "67":
//				resultValue = "424950740994121728";
//				break;
//			case "81":
//				resultValue = "424950932417961984";
//				break;
//			case "84":
//				resultValue = "424859913508773888";
//				break;
//			case "87":
//				resultValue = "424949962023788544";
//				break;
//			case "88":
//				resultValue = "424859821687070720";
//				break;
//			case "90":
//				resultValue = "424951108410957824";
//				break;
//			case "98":
//				resultValue = "424951176589369344";
//				break;
//			case "99":
//				resultValue = "424979166555693056";
//				break;
//			case "100":
//				resultValue = "424949505515741184";
//				break;
//			case "112":
//				resultValue = "424859706293379072";
//				break;
//			case "113":
//				resultValue = "424983016339562496";
//				break;
//			case "114":
//				resultValue = "424973040892141568";
//				break;
//			case "115":
//				resultValue = "424951282155806720";
//				break;
//			case "116":
//				resultValue = "424974103321927680";
//				break;
//			case "117":
//				resultValue = "424859994123296768";
//				break;
//			case "118":
//				resultValue = "424859994123296768";
//				break;
//			case "122":
//				resultValue = "424951108410957824";
//				break;
//			case "123":
//				resultValue = "424974103321927680";
//				break;
//			case "126":
//				resultValue = "424859913508773888";
//				break;
//			case "128":
//				resultValue = "424859913508773888";
//				break;
//			case "134":
//				resultValue = "424859913508773888";
//				break;
//			case "135":
//				resultValue = "424948342544293888";
//				break;
//			case "137":
//				resultValue = "424860069373304832";
//				break;
//			case "138":
//				resultValue = "424859913508773888";
//				break;
//			case "140":
//				resultValue = "424860069373304832";
//				break;
//			case "141":
//				resultValue = "424859913508773888";
//				break;
//			case "142":
//				resultValue = "424859994123296768";
//				break;
//			case "150":
//				resultValue = "424950932417961984";
//				break;
//			case "152":
//				resultValue = "424950493689569280";
//				break;
//			case "156":
//				resultValue = "424860069373304832";
//				break;
//			case "159":
//				resultValue = "424860069373304832";
//				break;
//			case "163":
//				resultValue = "424860069373304832";
//				break;
//			case "165":
//				resultValue = "424859994123296768";
//				break;
//			case "166":
//				resultValue = "424974103321927680";
//				break;
//			case "167":
//				resultValue = "424950493689569280";
//				break;
//			case "200":
//				resultValue = "424950493689569280";
//				break;
//			case "205":
//				resultValue = "424950932417961984";
//				break;
//			case "206":
//				resultValue = "424950932417961984";
//				break;
//			case "215":
//				resultValue = "424974103321927680";
//				break;
//			case "216":
//				resultValue = "424951176589369344";
//				break;
//			case "217":
//				resultValue = "424860069373304832";
//				break;
//			case "218":
//				resultValue = "424859913508773888";
//				break;
//			case "219":
//				resultValue = "424859821687070720";
//				break;
//			case "259":
//				resultValue = "424859913508773888";
//				break;
//			case "273":
//				resultValue = "424950828344696832";
//				break;
//			case "297":
//				resultValue = "424859821687070720";
//				break;
//			case "309":
//				resultValue = "424859994123296768";
//				break;
//			case "310":
//				resultValue = "424950493689569280";
//				break;
//			case "311":
//				resultValue = "424974103321927680";
//				break;
//		}
//		return resultValue;
//	}


//	erp设备和mes设备映射
	private static final Map<String, String> PROCEDURE_DEVICE_MAP = new HashMap<>();
	static {
		PROCEDURE_DEVICE_MAP.put("1", "424949962023788480"); // 数控激光切割机
		PROCEDURE_DEVICE_MAP.put("2", "424950245286109184"); // 数控折弯机
		PROCEDURE_DEVICE_MAP.put("3", "424949962023788480"); // 数控激光切割机
		PROCEDURE_DEVICE_MAP.put("4", "424949846894338048"); // 打磨机
		PROCEDURE_DEVICE_MAP.put("5", "424950828344696832"); // 调平机
		PROCEDURE_DEVICE_MAP.put("6", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("7", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("8", "424950740994121664"); // 数控激光切管机
		PROCEDURE_DEVICE_MAP.put("9", "424859821687070720"); // 角磨机
		PROCEDURE_DEVICE_MAP.put("10", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("11", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("12", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("13", "0"); // 未知设备
		PROCEDURE_DEVICE_MAP.put("14", "424950493689569280"); // 数控加工机床
		PROCEDURE_DEVICE_MAP.put("15", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("16", "424951176589369280"); // 抛丸机
		PROCEDURE_DEVICE_MAP.put("17", "424949846894338048"); // 打磨机
		PROCEDURE_DEVICE_MAP.put("18", "424948342544293824"); // 攻丝机
		PROCEDURE_DEVICE_MAP.put("19", "424950828344696832"); // 调平机
		PROCEDURE_DEVICE_MAP.put("20", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("21", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("22", "424859821687070720"); // 角磨机
		PROCEDURE_DEVICE_MAP.put("23", "424859994123296768"); // 调整设备
		PROCEDURE_DEVICE_MAP.put("24", "424960035571785664"); // 叉车转运
		PROCEDURE_DEVICE_MAP.put("25", "424951282155806720"); // 上件升降机
		PROCEDURE_DEVICE_MAP.put("26", "424973040892141568"); // 喷塑枪
		PROCEDURE_DEVICE_MAP.put("27", "425239381603672064"); // 下件升降机
		PROCEDURE_DEVICE_MAP.put("28", "424973656825683968"); // 顺丝调整设备
		PROCEDURE_DEVICE_MAP.put("29", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("30", "427747339575123968"); // 调平打包
		PROCEDURE_DEVICE_MAP.put("31", "0"); // 未知设备
		PROCEDURE_DEVICE_MAP.put("32", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("34", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("35", "424950346687602624"); // 坡口机
		PROCEDURE_DEVICE_MAP.put("36", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("37", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("38", "424950120539119616"); // 数控火焰切割机
		PROCEDURE_DEVICE_MAP.put("39", "424950346687602624"); // 坡口机
		PROCEDURE_DEVICE_MAP.put("40", "424960035571785664"); // 叉车转运
		PROCEDURE_DEVICE_MAP.put("41", "424950346687602624"); // 坡口机
		PROCEDURE_DEVICE_MAP.put("42", "424859994123296768"); // 调整设备
		PROCEDURE_DEVICE_MAP.put("43", "424950120539119616"); // 数控火焰切割机
		PROCEDURE_DEVICE_MAP.put("44", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("50", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("51", "424950493689569280"); // 数控加工机床
		PROCEDURE_DEVICE_MAP.put("52", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("54", "424951282155806720"); // 上件升降机
		PROCEDURE_DEVICE_MAP.put("55", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("56", "424950245286109184"); // 数控折弯机
		PROCEDURE_DEVICE_MAP.put("60", "424950346687602624"); // 坡口机
		PROCEDURE_DEVICE_MAP.put("61", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("63", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("64", "424950740994121664"); // 数控激光切管机
		PROCEDURE_DEVICE_MAP.put("65", "424950932417961984"); // 焊接机器人
		PROCEDURE_DEVICE_MAP.put("66", "424871737725706240"); // 弯管机
		PROCEDURE_DEVICE_MAP.put("67", "424950740994121664"); // 数控激光切管机
		PROCEDURE_DEVICE_MAP.put("69", "424949962023788480"); // 数控激光切割机
		PROCEDURE_DEVICE_MAP.put("70", "0"); // 未知设备
		PROCEDURE_DEVICE_MAP.put("71", "424859821687070720"); // 角磨机
		PROCEDURE_DEVICE_MAP.put("72", "424859821687070720"); // 角磨机
		PROCEDURE_DEVICE_MAP.put("73", "424951108410957760"); // 冲压机
		PROCEDURE_DEVICE_MAP.put("74", "424871737725706240"); // 弯管机
		PROCEDURE_DEVICE_MAP.put("75", "424950245286109184"); // 数控折弯机
		PROCEDURE_DEVICE_MAP.put("76", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("79", "424859821687070720"); // 角磨机
		PROCEDURE_DEVICE_MAP.put("81", "424950932417961984"); // 焊接机器人
		PROCEDURE_DEVICE_MAP.put("82", "424950932417961984"); // 焊接机器人
		PROCEDURE_DEVICE_MAP.put("83", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("84", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("86", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("88", "424949846894338048"); // 打磨机
		PROCEDURE_DEVICE_MAP.put("89", "424948342544293824"); // 攻丝机
		PROCEDURE_DEVICE_MAP.put("90", "424859994123296768"); // 调整设备
		PROCEDURE_DEVICE_MAP.put("92", "424950493689569280"); // 数控加工机床
		PROCEDURE_DEVICE_MAP.put("93", "424949846894338048"); // 打磨机
		PROCEDURE_DEVICE_MAP.put("94", "424950493689569280"); // 数控加工机床
		PROCEDURE_DEVICE_MAP.put("95", "424951176589369280"); // 抛丸机
		PROCEDURE_DEVICE_MAP.put("96", "424948342544293824"); // 攻丝机
		PROCEDURE_DEVICE_MAP.put("97", "424951176589369280"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("98", "424951176589369280"); // 抛丸机
		PROCEDURE_DEVICE_MAP.put("99", "424979166555693056"); // 吹水设备
		PROCEDURE_DEVICE_MAP.put("100", "424949505515741184"); // 打标机
		PROCEDURE_DEVICE_MAP.put("101", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("102", "424859706293379136"); // 扎捆机
		PROCEDURE_DEVICE_MAP.put("103", "424950346687602624"); // 坡口机
		PROCEDURE_DEVICE_MAP.put("104", "424950493689569280"); // 数控加工机床
		PROCEDURE_DEVICE_MAP.put("105", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("106", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("107", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("108", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("109", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("110", "424859994123296768"); // 调整设备
		PROCEDURE_DEVICE_MAP.put("111", "424859994123296768"); // 调整设备
		PROCEDURE_DEVICE_MAP.put("112", "427747339575123968"); // 调平打包
		PROCEDURE_DEVICE_MAP.put("113", "424983016339562496"); // 打磨遮蔽
		PROCEDURE_DEVICE_MAP.put("114", "424973040892141568"); // 喷塑枪
		PROCEDURE_DEVICE_MAP.put("115", "424951282155806720"); // 上件升降机
		PROCEDURE_DEVICE_MAP.put("116", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("117", "424859994123296768"); // 调整设备
		PROCEDURE_DEVICE_MAP.put("118", "424859994123296768"); // 调整设备
		PROCEDURE_DEVICE_MAP.put("119", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("120", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("121", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("122", "424859994123296768"); // 调整设备
		PROCEDURE_DEVICE_MAP.put("123", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("124", "424948342544293824"); // 攻丝机
		PROCEDURE_DEVICE_MAP.put("125", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("126", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("127", "424948342544293824"); // 攻丝机
		PROCEDURE_DEVICE_MAP.put("128", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("129", "424948342544293824"); // 攻丝机
		PROCEDURE_DEVICE_MAP.put("131", "424948342544293824"); // 攻丝机
		PROCEDURE_DEVICE_MAP.put("132", "424948342544293824"); // 攻丝机
		PROCEDURE_DEVICE_MAP.put("133", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("134", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("135", "424948342544293824"); // 攻丝机
		PROCEDURE_DEVICE_MAP.put("136", "424948342544293824"); // 攻丝机
		PROCEDURE_DEVICE_MAP.put("137", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("138", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("139", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("140", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("141", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("142", "424859994123296768"); // 调整设备
		PROCEDURE_DEVICE_MAP.put("143", "424859821687070720"); // 角磨机
		PROCEDURE_DEVICE_MAP.put("144", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("145", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("146", "424950932417961984"); // 焊接机器人
		PROCEDURE_DEVICE_MAP.put("148", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("151", "424950932417961984"); // 焊接机器人
		PROCEDURE_DEVICE_MAP.put("152", "424950493689569280"); // 数控加工机床
		PROCEDURE_DEVICE_MAP.put("153", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("154", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("155", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("156", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("157", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("158", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("159", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("161", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("162", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("163", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("164", "424859913508773824"); // 焊机
		PROCEDURE_DEVICE_MAP.put("165", "424859994123296768"); // 调整设备
		PROCEDURE_DEVICE_MAP.put("166", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("167", "424950493689569280"); // 数控加工机床
		PROCEDURE_DEVICE_MAP.put("168", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("169", "424983016339562496"); // 打磨遮蔽
		PROCEDURE_DEVICE_MAP.put("170", "425239381603672064"); // 下件升降机
		PROCEDURE_DEVICE_MAP.put("171", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("173", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("174", "424860069373304832"); // 预装胎
		PROCEDURE_DEVICE_MAP.put("175", "424950120539119616"); // 数控火焰切割机
		PROCEDURE_DEVICE_MAP.put("178", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("179", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("180", "425239381603672064"); // 下件升降机
		PROCEDURE_DEVICE_MAP.put("181", "424974103321927680"); // 装配工具
		PROCEDURE_DEVICE_MAP.put("182", "424950640133693440"); // 钻孔机床
		PROCEDURE_DEVICE_MAP.put("200", "424950493689569280"); // 数控加工机床
		PROCEDURE_DEVICE_MAP.put("999", "424974103321927680"); // 装配工具
	}

	private String getDeviceName(String procedureCode) {
		return PROCEDURE_DEVICE_MAP.getOrDefault(procedureCode, "");
	}


	//=====================================服务中切换数据源   下下策=========================================


	/**
	 * 1. 从ERP（Oracle）读取物料
	 */
	@DS("db3")
	public List<Map<String, Object>> fetchErpMaterialList() {
		return bomManageInfoMapper.materialMessage();
	}

	/**
	 * 2. ERP（Oracle）回写同步状态
	 */
	@DS("db3")
	public void updateErpSyncStatus(List<String> codes) {
		bomManageInfoMapper.materUpdate(codes);
	}

	/**
	 * 3. 主同步方法（默认MySQL/MES库，顶层加事务）
	 */
	@Transactional(rollbackFor = Exception.class)
	public int syncItemStock服务中切换数据源() {
		// 第一步：从Oracle拉取ERP未同步数据
		List<Map<String, Object>> erpList = fetchErpMaterialList();
		if (erpList == null || erpList.isEmpty()) {
			return 0;
		}

		// 第二步：编码集合
		List<String> allCodes = erpList.stream()
				.map(m -> m.get("STRITEMCODE").toString())
				.collect(Collectors.toList());

		// 第三步：查MySQL中已存在的物料
		Map<String, MesItemStock> existMap = mesItemStockService.getByItemNos(allCodes);

		// 第四步：MySQL中无此物料的标记失效
		LocalDateTime now = LocalDateTime.now();
		mesItemStockService.update(
				new LambdaUpdateWrapper<MesItemStock>()
						.set(MesItemStock::getIsValid, "00")
						.set(MesItemStock::getUpdatedBy, "1")
						.set(MesItemStock::getUpdatedTime, now)
						.notIn(MesItemStock::getItemNo, allCodes)
		);

		// 第五步：构造同步列表
		List<MesItemStock> saveList = erpList.stream().map(row -> {
			String code = row.get("STRITEMCODE").toString();
			MesItemStock stock = existMap.getOrDefault(code, new MesItemStock());

			if (stock.getId() == null) {
				stock.setId(UUID.randomUUID().toString().replace("-", ""));
				stock.setCreatedBy("1");
				stock.setCreatedTime(now);
			}

			stock.setItemNo(code);
			stock.setItemName(row.get("STRITEMNAME").toString());
			stock.setItemModel(Objects.toString(row.get("STRITEMSTYLE"), ""));
			stock.setItemMeasure(row.get("STRUNITNAME").toString());
			BigDecimal erpQty = Optional.ofNullable(row.get("DBLONHAND"))
					.map(Object::toString).map(BigDecimal::new).orElse(BigDecimal.ZERO);
			stock.setErpCount(erpQty);
			stock.setItemCount(erpQty);

			String src = row.get("BYTSOURCE").toString();
			stock.setItemOrigin(src);
			if (ItemType.isMaterials(src)) {
				stock.setItemType(ItemType.MATERIALS.getCode());
				stock.setBomNo("");
			} else {
				stock.setItemType(ItemType.BOM.getCode());
				stock.setBomNo(stock.getItemModel());
			}
			stock.setItemCountAssist(BigDecimal.ZERO);
			stock.setItemMeasureAssist(row.get("STRUNITNAME").toString());
			stock.setIsValid("01");
			stock.setUpdatedBy("1");
			stock.setUpdatedTime(now);

			return stock;
		}).collect(Collectors.toList());

		// 第六步：MySQL批量更新
		mesItemStockService.saveOrUpdateBatch(saveList, 500);

		// 第七步：回写ERP同步状态
		updateErpSyncStatus(allCodes);

		// 返回本次同步数量
		return allCodes.size();
	}



}

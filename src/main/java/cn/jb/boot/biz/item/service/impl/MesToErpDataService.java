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


	//===================bom==================

	/**
	 * bom同步 V1.0
	 * 仅同步 ERP（JSPBOM）表 BYTSTATUS=0 的BOM用料树
	 * JSPBOM (ERP) :   拉取 BOM 用料数据（BYTSTATUS=0）
	 * mes_item_use  ： INSERT 或 UPDATE（批量）：插入或更新 MES 的用料明细（树的跟节点）
	 * 更新： 先删后增
	 *
	 *
	 * @return 同步数量
	 */
	public int syncBomTree() {
		// 1. 拉ERP BOM用料
		List<Map<String, Object>> bomList = mesToErpDataMapper.bomMessage();
		System.out.println("ERP-BOM拉取结果：" + bomList);
		if (bomList == null || bomList.isEmpty()) {
			return 0;
		}

		List<Integer> bomIdList = new ArrayList<>();
		List<MesItemUse> saveList = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();

		// 新增：收集所有itemNo，批量去重
		Set<String> itemNoSet = bomList.stream()
				.map(row -> row.get("STRITEMCODE").toString())
				.collect(Collectors.toSet());

		// 新增：批量删除所有itemNo的旧数据
		if (!itemNoSet.isEmpty()) {
			mesItemUseService.remove(
					new LambdaQueryWrapper<MesItemUse>().in(MesItemUse::getItemNo, itemNoSet)
			);
		}

		// 原有循环无需变动
		for (Map<String, Object> row : bomList) {
			bomIdList.add(Integer.valueOf(row.get("LNGBOMID").toString()));
			MesItemUse use = new MesItemUse();
			use.setId(UUID.randomUUID().toString().replace("-", ""));

			String itemNo = row.get("STRITEMCODE").toString();
			String useItemNo = row.get("STRNEXTITEMCODE").toString();

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

			String itemStyle = row.get("BYTITEMSOURCE").toString();
			use.setUseItemType(itemStyle.equals("0") ? "00" : "01");
			use.setUpdatedTime(now);

			saveList.add(use);
		}

		// 2. 保存到MES
		if (saveList.isEmpty()) {
			System.err.println("【警告】无有效用料明细需要同步！");
			log.info("【警告】无有效用料明细需要同步！");
			return 0;
		}
		mesItemUseService.saveBatch(saveList);

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




	private BigDecimal safeParseDecimal(Object value) {
		try {
			return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
		} catch (Exception e) {
			log.warn("数值转换失败: {}", value);
			return BigDecimal.ZERO;
		}
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

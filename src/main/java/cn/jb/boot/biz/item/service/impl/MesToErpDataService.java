package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.agvcar.mapper.BomManageInfoMapper;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.mapper.MesToErpDataMapper;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MesItemUseService;
import cn.jb.boot.biz.item.service.MesProcedureService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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


//=====================================map中切换数据源=========================================




	//===================物料==================
	/**
	 * 物料同步 V2.0
	 * 仅同步 ERP（JSPMATERIAL）表 BYTSTATUS=0 的物料数据，完成后将 BYTSTATUS 置为 1。
	 * 拉取数据、回写都走 Mapper（Oracle），
	 */
//	@Transactional(rollbackFor = Exception.class)   跨库不能使用事务！
	public int syncItemStock() {
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
								.set(MesItemStock::getIsValid, "0")
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
	 * 工序同步 V1.2
	 *使用BomNo索引，并保留bomNo字段备用
	 * 同步ERP工序表（JSPBOMROUTER）到MES工序表（mes_procedure）。
	 * 只同步ERP端 BYTSTATUS=0 的工序数据（未同步/有变更），同步完成后回写BYTSTATUS=1。
	 * 唯一性判定：bom_no（对应STRBOMCODE）+ procedure_code（对应STRROUTECODE）。
	 */
	public int syncProcedure() {
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

			// 设备编码（通过工序code反查，实际请根据业务重写）
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


	// 写死的设备映射表！！
	private String getDeviceName(String procedureCode) {
		String resultValue = "";
		switch (procedureCode) {
			case "1":
				resultValue = "424949962023788544";
				break;
			case "2":
				resultValue = "424859821687070720";
				break;
			case "3":
				resultValue = "424949962023788544";
				break;
			case "4":
				resultValue = "424949846894338048";
				break;
			case "5":
				resultValue = "424950828344696832";
				break;
			case "8":
				resultValue = "424871737725706240";
				break;
			case "10":
				resultValue = "424860069373304832";
				break;
			case "11":
				resultValue = "424860069373304832";
				break;
			case "12":
				resultValue = "424859913508773888";
				break;
			case "13":
				resultValue = "425242411535327232";
				break;
			case "14":
				resultValue = "424950493689569280";
				break;
			case "16":
				resultValue = "424951176589369344";
				break;
			case "19":
				resultValue = "424859994123296768";
				break;
			case "22":
				resultValue = "424859821687070720";
				break;
			case "23":
				resultValue = "424859994123296768";
				break;
			case "24":
				resultValue = "424960035571785728";
				break;
			case "25":
				resultValue = "424951282155806720";
				break;
			case "26":
				resultValue = "424973040892141568";
				break;
			case "27":
				resultValue = "425239381603672064";
				break;
			case "28":
				resultValue = "424973656825683968";
				break;
			case "29":
				resultValue = "424974103321927680";
				break;
			case "30":
				resultValue = "424974103321927680";
				break;
			case "31":
				resultValue = "424859706293379072";
				break;
			case "32":
				resultValue = "424950640133693440";
				break;
			case "34":
				resultValue = "424860069373304832";
				break;
			case "37":
				resultValue = "424859913508773888";
				break;
			case "38":
				resultValue = "424950120539119616";
				break;
			case "39":
				resultValue = "424950346687602688";
				break;
			case "40":
				resultValue = "424859706293379072";
				break;
			case "42":
				resultValue = "424859994123296768";
				break;
			case "43":
				resultValue = "424950120539119616";
				break;
			case "61":
				resultValue = "424859913508773888";
				break;
			case "63":
				resultValue = "424974103321927680";
				break;
			case "64":
				resultValue = "424950740994121728";
				break;
			case "65":
				resultValue = "424950932417961984";
				break;
			case "67":
				resultValue = "424950740994121728";
				break;
			case "81":
				resultValue = "424950932417961984";
				break;
			case "84":
				resultValue = "424859913508773888";
				break;
			case "87":
				resultValue = "424949962023788544";
				break;
			case "88":
				resultValue = "424859821687070720";
				break;
			case "90":
				resultValue = "424951108410957824";
				break;
			case "98":
				resultValue = "424951176589369344";
				break;
			case "99":
				resultValue = "424979166555693056";
				break;
			case "100":
				resultValue = "424949505515741184";
				break;
			case "112":
				resultValue = "424859706293379072";
				break;
			case "113":
				resultValue = "424983016339562496";
				break;
			case "114":
				resultValue = "424973040892141568";
				break;
			case "115":
				resultValue = "424951282155806720";
				break;
			case "116":
				resultValue = "424974103321927680";
				break;
			case "117":
				resultValue = "424859994123296768";
				break;
			case "118":
				resultValue = "424859994123296768";
				break;
			case "122":
				resultValue = "424951108410957824";
				break;
			case "123":
				resultValue = "424974103321927680";
				break;
			case "126":
				resultValue = "424859913508773888";
				break;
			case "128":
				resultValue = "424859913508773888";
				break;
			case "134":
				resultValue = "424859913508773888";
				break;
			case "135":
				resultValue = "424948342544293888";
				break;
			case "137":
				resultValue = "424860069373304832";
				break;
			case "138":
				resultValue = "424859913508773888";
				break;
			case "140":
				resultValue = "424860069373304832";
				break;
			case "141":
				resultValue = "424859913508773888";
				break;
			case "142":
				resultValue = "424859994123296768";
				break;
			case "150":
				resultValue = "424950932417961984";
				break;
			case "152":
				resultValue = "424950493689569280";
				break;
			case "156":
				resultValue = "424860069373304832";
				break;
			case "159":
				resultValue = "424860069373304832";
				break;
			case "163":
				resultValue = "424860069373304832";
				break;
			case "165":
				resultValue = "424859994123296768";
				break;
			case "166":
				resultValue = "424974103321927680";
				break;
			case "167":
				resultValue = "424950493689569280";
				break;
			case "200":
				resultValue = "424950493689569280";
				break;
			case "205":
				resultValue = "424950932417961984";
				break;
			case "206":
				resultValue = "424950932417961984";
				break;
			case "215":
				resultValue = "424974103321927680";
				break;
			case "216":
				resultValue = "424951176589369344";
				break;
			case "217":
				resultValue = "424860069373304832";
				break;
			case "218":
				resultValue = "424859913508773888";
				break;
			case "219":
				resultValue = "424859821687070720";
				break;
			case "259":
				resultValue = "424859913508773888";
				break;
			case "273":
				resultValue = "424950828344696832";
				break;
			case "297":
				resultValue = "424859821687070720";
				break;
			case "309":
				resultValue = "424859994123296768";
				break;
			case "310":
				resultValue = "424950493689569280";
				break;
			case "311":
				resultValue = "424974103321927680";
				break;
		}
		return resultValue;
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
						.set(MesItemStock::getIsValid, "0")
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

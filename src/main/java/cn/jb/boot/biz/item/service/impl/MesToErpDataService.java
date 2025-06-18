package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.agvcar.mapper.BomManageInfoMapper;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.mapper.MesToErpDataMapper;
import cn.jb.boot.biz.item.service.MesItemStockService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
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
	private MesToErpDataMapper mesToErpDataMapper;


//=====================================map中切换数据源=========================================


	/**
	 * 仅同步 ERP（JSPMATERIAL）表 BYTSTATUS=0 的物料数据，完成后将 BYTSTATUS 置为 1。
	 * 所有字段、辅助逻辑不删减。
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
			System.err.println("【警告】ERP未查到可同步的物料数据！");
			log.info("【警告】ERP未查到可同步的物料数据！");
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


	//=====================================服务中切换数据源=========================================


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

package cn.jb.boot.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.biz.item.vo.response.UseItemTreeResp;
// ───────────【新增】引入物料主数据相关类
import cn.jb.boot.biz.item.entity.MesItemStock;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * bom树构造器 （平平展树） （  将 List<BomUsed> list  + String root   构建为前端树结构）
 * 工具类（静态方法版）
 *
 * 不再使用字典表，直接查询 mes_item_stock → getByItemNos 返回 Map<itemNo, MesItemStock>。
 */
public class BomUsedNewUtil {


	/**
	 * 对外暴露：根据用料列表 + 根物料编号，生成用料树
	 */
	public static UseItemTreeResp tree(List<BomUsed> list, String itemNo) {
		// 一次性收集所有用到的 itemNo
		Set<String> itemNos = new HashSet<>();
		itemNos.add(itemNo);
		for (BomUsed bu : list) {
			itemNos.add(bu.getUseItemNo());
		}

		// 批量查询主数据
		MesItemStockMapper mapper = SpringUtil.getBean(MesItemStockMapper.class);
		Map<String, MesItemStock> stockMap = mapper.getByItemNos(new ArrayList<>(itemNos));

		// 构造树
		return toTree(list, itemNo, stockMap);
	}

	/**
	 * 递归：构造当前节点，并填充其所有子节点
	 */
	private static UseItemTreeResp toTree(List<BomUsed> list, String itemNo, Map<String, MesItemStock> stockMap) {

		// 打印根节点匹配检查日志
		System.out.println("根节点匹配检查: 当前itemNo=" + itemNo);

		UseItemTreeResp resp = new UseItemTreeResp();
		resp.setItemNo(itemNo);
		setItemInfo(resp, itemNo, stockMap);
		resp.setParentCode(itemNo);
		resp.setFixedUsed(BigDecimal.ONE);
		resp.setChildren(children(list, itemNo, stockMap,new HashSet<>()));
		return resp;
	}

	/**
	 * 递归查找所有直接子项
	 */
	private static List<UseItemTreeResp> children(List<BomUsed> list, String itemNo, Map<String, MesItemStock> stockMap, Set<String> visited) {
		if (visited.contains(itemNo)) {
			System.out.println("检测到循环依赖，跳过 itemNo=" + itemNo);
			return Collections.emptyList();
		}
		visited.add(itemNo);
		List<UseItemTreeResp> resps = new ArrayList<>();
		for (BomUsed bu : list) {
			if (itemNo.equals(bu.getUseItemNo())) {
				continue;
			}
			if (itemNo.equals(bu.getParentCode())) {
				System.out.println("匹配到子项: itemNo=" + itemNo + ", 子项=" + bu.getUseItemNo());
				UseItemTreeResp resp = new UseItemTreeResp();
				resp.setFixedUsed(bu.getFixedUsed());
				resp.setUsedId(bu.getUsedId());
				resp.setItemNo(bu.getUseItemNo());
				resp.setParentCode(bu.getParentCode());
				resp.setItemType(bu.getUseItemType());
				setItemInfo(resp, bu.getUseItemNo(), stockMap);
				resp.setChildren(children(list, bu.getUseItemNo(), stockMap, visited));
				resps.add(resp);
			}
		}
		visited.remove(itemNo); // 【修复循环依赖：回溯时移除，防止兄弟分支误判】
		return resps;
	}

	/**
	 * 查询 mes_item_stock，填充 itemName 和 bomNo
	 *
	 * @param resp   需要被填充名称和 bomNo
	 * @param itemNo 要查询的物料编号
	 */
	private static void setItemInfo(UseItemTreeResp resp, String itemNo, Map<String, MesItemStock> stockMap) {
		if (stockMap != null && stockMap.containsKey(itemNo)) {
			MesItemStock stock = stockMap.get(itemNo);
			String bomNo = stock.getBomNo();
			String itemName = StringUtils.isNotBlank(bomNo)
					? stock.getItemName() + "(" + bomNo + ")"
					: stock.getItemName() + "(" + stock.getItemNo() + ")";
			resp.setItemName(itemName);
			resp.setBomNo(bomNo);
			resp.setItemType(stock.getItemType());
		}
	}

}

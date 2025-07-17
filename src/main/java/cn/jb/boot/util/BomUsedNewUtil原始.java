package cn.jb.boot.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.mapper.MesItemStockMapper;
import cn.jb.boot.biz.item.vo.response.UseItemTreeResp;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * bom树构造器  （  将 List<BomUsed> list  + String root   构建为前端树结构）
 * 工具类（静态方法版）
 *
 * 不再使用字典表，直接查询 mes_item_stock → getByItemNos 返回 Map<itemNo, MesItemStock>。
 */
public class BomUsedNewUtil原始 {


	/**
	 * 对外暴露：根据用料列表 + 根物料编号，生成用料树
	 */
	public static UseItemTreeResp tree(List<BomUsed> list, String itemNo) {
		return toTree(list, itemNo);
	}

	/**
	 * 递归：构造当前节点，并填充其所有子节点
	 */
	private static UseItemTreeResp toTree(List<BomUsed> list, String itemNo) {
		UseItemTreeResp resp = new UseItemTreeResp();
		resp.setItemNo(itemNo);
		// ← 调用同名 static 方法，签名一致
		setItemInfo(resp, itemNo);
		resp.setItemType(ItemType.BOM.getCode());
		resp.setParentCode(itemNo);
		resp.setFixedUsed(BigDecimal.ONE);
		resp.setChildren(children(list, itemNo));
		return resp;
	}

	/**
	 * 递归查找所有直接子项
	 */
	private static List<UseItemTreeResp> children(List<BomUsed> list, String itemNo) {
		List<UseItemTreeResp> resps = new ArrayList<>();
		for (BomUsed bu : list) {
			// 防止自己引用自己导致死循环
			if (itemNo.equals(bu.getUseItemNo())) {
				continue;
			}
			if (itemNo.equals(bu.getParentCode())) {
				UseItemTreeResp resp = new UseItemTreeResp();
				resp.setFixedUsed(bu.getFixedUsed());
				resp.setUsedId(bu.getUsedId());
				resp.setItemNo(bu.getUseItemNo());
				resp.setParentCode(bu.getParentCode());
				resp.setItemType(bu.getUseItemType());
				// ← 调用同名 static 方法，签名一致
				setItemInfo(resp, bu.getUseItemNo());
				resp.setChildren(children(list, bu.getUseItemNo()));
				resps.add(resp);
			}
		}
		return resps;
	}

	/**
	 * 查询 mes_item_stock，填充 itemName 和 bomNo
	 *
	 * @param resp   需要被填充名称和 bomNo
	 * @param itemNo 要查询的物料编号
	 */
	private static void setItemInfo(UseItemTreeResp resp, String itemNo) {
		// 1. 从 Spring 容器拿到 Mapper
		MesItemStockMapper mapper = SpringUtil.getBean(MesItemStockMapper.class);
		// 2. 调用 XML 中那条 <select id="getByItemNos"...>，它返回 Map<String,MesItemStock>
		Map<String, MesItemStock> stockMap = mapper.getByItemNos(Collections.singletonList(itemNo));
		// 3. 如果不为空，就从 Map 里取，拼 name(bomNo)
		if (stockMap != null && stockMap.containsKey(itemNo)) {
			MesItemStock stock = stockMap.get(itemNo);
			String bomNo = stock.getBomNo();
			String itemName = StringUtils.isNotBlank(bomNo)
					? stock.getItemName() + "(" + bomNo + ")"
					: stock.getItemName() + "(" + stock.getItemNo() + ")";
			resp.setItemName(itemName);
			resp.setBomNo(bomNo);
		}
	}


}

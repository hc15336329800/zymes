package cn.jb.boot.biz.item.dto;


import cn.jb.boot.biz.item.vo.response.UseItemTreeResp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//工具类：把扁平列表组装成递归树
public class TreeUtil {
	/**
	 * 构建递归树
	 * @param rows 全部行（包含根和子项）
	 * @param rootNo 根节点的 itemNo
	 */
	public static UseItemTreeResp buildTree(List<UseItemTreeRow> rows, String rootNo) {
		// 按 parentCode 分组
		Map<String, List<UseItemTreeRow>> group = rows.stream()
				.collect(Collectors.groupingBy(UseItemTreeRow::getParentCode));

		// 先查根节点的名称（可能没在 rows 里）
		String rootName = rows.stream()
				.filter(r -> rootNo.equals(r.getItemNo()))
				.map(UseItemTreeRow::getItemName)
				.findFirst()
				.orElseGet(() -> /* 兜底查一次库存表 */ "");

		// 构造根节点
		UseItemTreeResp root = new UseItemTreeResp();
		root.setItemNo(rootNo);
		root.setItemName(rootName);
		root.setParentCode(rootNo);
		// 根节点上 fixedUsed/bomNo/itemType/usedId 可根据业务决定是否填充
		root.setChildren(buildChildren(rootNo, group));
		return root;
	}

	private static List<UseItemTreeResp> buildChildren(String parentNo,
													   Map<String, List<UseItemTreeRow>> group) {
		List<UseItemTreeResp> children = new ArrayList<>();
		List<UseItemTreeRow> list = group.get(parentNo);
		if (list == null) return children;
		for (UseItemTreeRow row : list) {
			UseItemTreeResp node = new UseItemTreeResp();
			node.setItemNo(row.getUseItemNo());
			node.setItemName(row.getItemName());
			node.setParentCode(row.getParentCode()) ;
//			node.setUsedId(row.getUsedId());  用户
			node.setFixedUsed(row.getFixedUsed());
			node.setBomNo(row.getBomNo());
			node.setItemType(row.getItemType());
			// 递归
			node.setChildren(buildChildren(row.getUseItemNo(), group));
			children.add(node);
		}
		return children;
	}
}

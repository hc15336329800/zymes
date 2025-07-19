package cn.jb.boot.util;

import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.biz.item.entity.BomUsed;
import cn.jb.boot.biz.item.vo.response.UseItemTreeResp;
import cn.jb.boot.system.vo.DictDataVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BomUsedUtil {

    public static UseItemTreeResp tree(List<BomUsed> list, String itemNo) {
        return toTree(list, itemNo);
    }

    private static UseItemTreeResp toTree(List<BomUsed> list, String itemNo) {
        UseItemTreeResp resp = new UseItemTreeResp();
        resp.setItemNo(itemNo);
        setItemInfo(resp);
        resp.setItemType(ItemType.BOM.getCode());
        resp.setParentCode(itemNo);
        resp.setFixedUsed(BigDecimal.ONE);
        resp.setChildren(children(list, itemNo));
        return resp;
    }

    private static List<UseItemTreeResp> children(List<BomUsed> list, String itemNo) {
        List<UseItemTreeResp> resps = new ArrayList<>();
        for (BomUsed bu : list) {
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
                setItemInfo(resp);
                List<UseItemTreeResp> children = children(list, bu.getUseItemNo());
                resp.setChildren(children);
                resps.add(resp);
            }
        }
        return resps;
    }

    private static void setItemInfo(UseItemTreeResp resp) {
        String itemNo = resp.getItemNo();
        DictDataVo dict = DictUtil.getDictData(DictType.BOM_NO, itemNo);
        if (Objects.nonNull(dict)) {
            String bomNo = dict.getDictLabel();
            String itemName = StringUtils.isNotBlank(bomNo) ?
                    dict.getName() + "(" + bomNo + ")" : dict.getName() + "(" + itemNo + ")";
            resp.setItemName(itemName);
            resp.setBomNo(bomNo);
        }
    }


    /**
     * 将 UseItemTreeResp 树结构平展为 BomUsed 列表，用于批量保存
     * @param root 整棵树的根节点
     * @return BomUsed 实体列表
     */
    public static List<BomUsed> flattenTree(UseItemTreeResp root) {
        List<BomUsed> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        traverse(root, root.getItemNo(), result);
        return result;
    }
    /**
     * 递归遍历每个节点，生成对应的 BomUsed 并收集
     * @param node 当前节点
     * @param treeRootItemNo 整棵树的根 itemNo，用作每条记录的 itemNo
     * @param out 收集结果
     */
    private static void traverse(UseItemTreeResp node, String treeRootItemNo, List<BomUsed> out) {
        // 构造 BomUsed 实体
        BomUsed bu = new BomUsed();
        bu.setItemNo(treeRootItemNo);
        bu.setUseItemNo(node.getItemNo());
        bu.setParentCode(node.getParentCode());
        bu.setUsedId(node.getUsedId());
        bu.setFixedUsed(node.getFixedUsed());
        bu.setUseItemType(node.getItemType());
        bu.setBomNo(node.getBomNo());
        out.add(bu);

        // 递归子节点
        if (CollectionUtils.isNotEmpty(node.getChildren())) {
            for (UseItemTreeResp child : node.getChildren()) {
                traverse(child, treeRootItemNo, out);
            }
        }}

}

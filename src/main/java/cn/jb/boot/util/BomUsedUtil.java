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
}
package cn.jb.boot.framework.com.listenter;


import cn.jb.boot.biz.sales.entity.SaleOrder;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.util.DictUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * 读取excel  监听
 *
 * @Author: xyb
 * @Description:
 * @Date: 2023-04-30 下午 01:36
 **/
public class SaleOrderReadListener extends AnalysisEventListener<Object> {

    private List<Object> list = new ArrayList<>();

    private List<SaleOrder> saleOrders;
    private String orderNo;

    private final Logger log = LoggerFactory.getLogger(SaleOrderReadListener.class);

    public SaleOrderReadListener(List<SaleOrder> saleOrders, String orderNo) {
        this.saleOrders = saleOrders;
        this.orderNo = orderNo;
    }


    @Override
    public void invoke(Object data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //第二行开始才是数据
        for (int i = 0; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
            String custName = dataMap.get(0);
            String bomNo = dataMap.get(1);
            String count = dataMap.get(2);
            SaleOrder order = new SaleOrder();
            order.setNeedNum(new BigDecimal(count));
            String itemNo = getItemNo(bomNo);
            if (StringUtils.isBlank(itemNo)) {
                continue;
            }
            order.setItemNo(itemNo);
            order.setOrderNo(orderNo);
            order.setCustName(custName);
            order.setApprovedOrderedNum(BigDecimal.ZERO);
            order.setProducedNum(BigDecimal.ZERO);
            order.setOrderStatus(JbEnum.CODE_00.getCode());
            saleOrders.add(order);

        }

    }

    private static String getItemNo(String bomNo) {
        List<DictDataVo> dictCache = DictUtil.getDictCache(DictType.BOM_NO);
        if (CollectionUtils.isEmpty(dictCache)) {
            return null;
        }
        String itemNo = null;
        for (DictDataVo dictDataVo : dictCache) {
            if (bomNo.equals(dictDataVo.getDictLabel())) {
                itemNo = dictDataVo.getDictValue();
            }
        }
        return itemNo;
    }
}
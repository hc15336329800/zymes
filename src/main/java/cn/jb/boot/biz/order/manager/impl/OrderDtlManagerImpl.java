package cn.jb.boot.biz.order.manager.impl;

import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.service.MesItemUseService;
import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.biz.order.entity.OrderDtl;
import cn.jb.boot.biz.order.manager.OrderDtlManager;
import cn.jb.boot.biz.order.mapper.OrderDtlMapper;
import cn.jb.boot.biz.order.model.OrderDtlReport;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.util.ArithUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderDtlManagerImpl implements OrderDtlManager {
    @Resource
    private OrderDtlMapper orderDtlMapper;
    @Resource
    private MidItemStockService midItemStockService;
    @Resource
    private MesItemUseService mesItemUseService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void report(List<OrderDtlReport> dtls) {
        for (OrderDtlReport dtl : dtls) {
            updateMidAndOrder(dtl.getCount(), dtl.getOrderDtlId(), dtl.getProcedureCode());
        }
    }

    public void updateMidAndOrder(BigDecimal reportCount, String dtlId, String procedureCode) {
        OrderDtl dtl = orderDtlMapper.selectById(dtlId);
        String itemNo = dtl.getItemNo();
        List<MidItemStock> mids =
                midItemStockService.list(new LambdaQueryWrapper<MidItemStock>().eq(MidItemStock::getItemNo, itemNo)
                        .orderByAsc(MidItemStock::getSeqNo)
                );
        List<MesItemUse> itemUses =
                mesItemUseService.list(new LambdaQueryWrapper<MesItemUse>().eq(MesItemUse::getItemNo, itemNo));
        List<String> useItemNos = itemUses.stream().map(MesItemUse::getUseItemNo).collect(Collectors.toList());
        Map<String, MidItemStock> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(useItemNos)) {
            map = midItemStockService.selectStock(useItemNos);
        }
        List<MidItemStock> ups = new ArrayList<>();
        for (int i = 0; i < mids.size(); i++) {
            MidItemStock mid = mids.get(i);
            if (Objects.isNull(mid)) {
                continue;
            }
            if (procedureCode.equals(mid.getProcedureCode())) {
                //最后一道工序
                if (JbEnum.CODE_01.getCode().equals(mid.getLastFlag())) {
                    //更新订单生产量
                    BigDecimal prdCount = ArithUtil.add(reportCount, dtl.getProductionCount());
                    dtl.setProductionCount(prdCount);
                    if(dtl.getItemCount().equals(prdCount)){
                        dtl.setOrderDtlStatus("08");
                    }
                    orderDtlMapper.updateById(dtl);
                }
                MidItemStock up = new MidItemStock();
                up.setId(mid.getId());
                up.setReportCount(ArithUtil.add(mid.getReportCount(), reportCount));
                ups.add(up);
                if (i == 0) {
                    for (MesItemUse used : itemUses) {
                        String useItemNo = used.getUseItemNo();
                        MidItemStock pre = map.get(useItemNo);
                        if (Objects.nonNull(pre)) {
                            MidItemStock upm = new MidItemStock();
                            upm.setId(pre.getId());
                            upm.setReportUsed(ArithUtil.add(pre.getReportUsed(), ArithUtil.mul(reportCount, used.getUseItemCount())));
                            ups.add(upm);
                        }
                    }
                } else {
                    MidItemStock pre = mids.get(i - 1);
                    MidItemStock upm = new MidItemStock();
                    upm.setId(pre.getId());
                    upm.setReportUsed(ArithUtil.add(pre.getReportUsed(), reportCount));
                    ups.add(upm);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(ups)) {
            midItemStockService.updateBatchById(ups);
        }


    }


}

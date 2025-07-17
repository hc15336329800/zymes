package cn.jb.boot.biz.depository.manager;


import cn.jb.boot.biz.depository.entity.OutStoreMid;
import cn.jb.boot.biz.depository.entity.OutStoreOrder;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.order.entity.OrderDtl;
import cn.jb.boot.biz.order.entity.ProcAllocation;
import cn.jb.boot.biz.work.entity.WorkOrder;

import java.math.BigDecimal;
import java.util.List;

/**
 * 领料单
 */
public interface OutStoreManager {
    /**
     * 生成领料单
     *
     * @param orderDtl
     */
    void generateOutStore(List<OrderDtl> dtls, List<String> itemNoList);

    void createOutStore(BigDecimal count, String itemNo, String procedureCode, String bizId, String bizType, String userId);

}

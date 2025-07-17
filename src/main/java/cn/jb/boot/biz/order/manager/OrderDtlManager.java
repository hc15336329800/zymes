package cn.jb.boot.biz.order.manager;

import cn.jb.boot.biz.order.model.OrderDtlReport;

import java.util.List;

public interface OrderDtlManager {
    /**
     * 报工计算生产单
     *
     * @param dtls
     */
    void report(List<OrderDtlReport> dtls);
}

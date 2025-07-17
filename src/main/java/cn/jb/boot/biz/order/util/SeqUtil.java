package cn.jb.boot.biz.order.util;


import cn.jb.boot.biz.order.mapper.OrderDtlMapper;
import cn.jb.boot.biz.work.mapper.WorkOrderMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SeqUtil {

    private static final int l = -1;
    /**
     * AssignSeq
     **/
    private static AtomicInteger ASS_SEQ = new AtomicInteger(l);

    /**
     * 工单号码
     **/
    private static AtomicLong ORDER_SEQ = new AtomicLong(l);
    private static AtomicLong ORDER_DTL_SEQ = new AtomicLong(l);


    @Resource
    private OrderDtlMapper dtlMapper;
    @Resource
    private WorkOrderMapper workOrderMapper;

    public synchronized String workOrderNo() {
        long no = ORDER_SEQ.getAndIncrement();
        if (no == l) {
            Long dbNo = workOrderMapper.maxOrderNo();
            dbNo = dbNo == null ? 0 : dbNo;
            ORDER_SEQ.set(dbNo + 1);
            no = ORDER_SEQ.getAndIncrement();
        }
        return "GD-" + no;
    }

    public synchronized String getOrderNo() {
        long no = ORDER_DTL_SEQ.getAndIncrement();
        if (no == l) {
            Long dbNo = dtlMapper.maxOrderNo();
            dbNo = dbNo == null ? 0 : dbNo;
            ORDER_DTL_SEQ.set(dbNo + 1);
            no = ORDER_DTL_SEQ.getAndIncrement();
        }
        return "DD-" + no;
    }
}

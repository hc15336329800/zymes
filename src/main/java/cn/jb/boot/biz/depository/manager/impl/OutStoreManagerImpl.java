package cn.jb.boot.biz.depository.manager.impl;

import cn.jb.boot.biz.depository.entity.OutStoreMid;
import cn.jb.boot.biz.depository.entity.OutStoreOrder;
import cn.jb.boot.biz.depository.manager.OutStoreManager;
import cn.jb.boot.biz.depository.service.OutStoreMidService;
import cn.jb.boot.biz.depository.service.OutStoreOrderService;
import cn.jb.boot.biz.item.entity.MesItemUse;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.enums.ItemType;
import cn.jb.boot.biz.item.mapper.MesItemUseMapper;
import cn.jb.boot.biz.item.mapper.MesProcedureMapper;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.biz.order.entity.OrderDtl;
import cn.jb.boot.biz.order.entity.ProcAllocation;
import cn.jb.boot.biz.order.service.ProcAllocationService;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OutStoreManagerImpl implements OutStoreManager {

    @Autowired
    private OutStoreOrderService outStoreOrderService;

    @Autowired
    private MidItemStockService midService;

    @Resource
    private OutStoreMidService outStoreMidService;

    @Resource
    private MesProcedureMapper procedureMapper;
    @Resource
    private ProcAllocationService procAllocationService;


    @Resource
    private MesItemUseMapper itemUseMapper;


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void generateOutStore(List<OrderDtl> dtls, List<String> itemNoList) {

        List<String> itemNos = dtls.stream().map(OrderDtl::getItemNo).collect(Collectors.toList());
        List<String> oids = dtls.stream().map(OrderDtl::getId).collect(Collectors.toList());
        List<MidItemStock> mids = midService.list(new LambdaQueryWrapper<MidItemStock>().in(MidItemStock::getItemNo,
                itemNos));
        Map<String, MidItemStock> midMap = mids.stream().collect(Collectors.toMap(MidItemStock::getItemProc,
                Function.identity()));
        List<ProcAllocation> procs =
                procAllocationService.list(new LambdaQueryWrapper<ProcAllocation>().in(ProcAllocation::getOrderDtlId,
                        oids));
        Map<String, List<ProcAllocation>> procMap =
                procs.stream().collect(Collectors.groupingBy(ProcAllocation::getItemNo));
        List<OutStoreMid> smids = new ArrayList<>();
        try {
            for (OrderDtl dtl : dtls) {
                String itemNo = dtl.getItemNo();
                BigDecimal needCount = dtl.getMidCount();
                List<ProcAllocation> pas = procMap.get(itemNo);
                if (CollectionUtils.isNotEmpty(pas)) {
                    for (int i = 0; i < pas.size(); i++) {
                        ProcAllocation pa = pas.get(i);
                        String itemProcKey = StringUtil.getItemProcKey(pa.getItemNo(), pa.getProcedureCode());
                        MidItemStock mid = midMap.get(itemProcKey);
                        if (Objects.nonNull(mid)) {
                            //可用 = 期初+报工释放剩余量 -分配使用量-出库量
                            BigDecimal lessCount = mid.getInitialCount().add(mid.getReportLessCount())
                                    .subtract(mid.getOutStoreUsed()).subtract(mid.getAllocUsed());
                            if (ArithUtil.gt(lessCount, BigDecimal.ZERO)) {
                                //需要的数量减去本次使用的中间件
                                BigDecimal useCount = ArithUtil.gt(needCount, lessCount) ? lessCount : needCount;
                                mid.setAllocUsed(ArithUtil.add(mid.getAllocUsed(), useCount));
                                OutStoreMid storeMid = buildOrderMid(dtl, mid, useCount);
                                smids.add(storeMid);
                                pa.setMidCount(ArithUtil.add(pa.getMidCount(), useCount));
                                pa.setTotalCount(ArithUtil.sub(pa.getTotalCount(), useCount));
                            }
                        }
                    }

                }


            }
            this.saveOrUpdate(mids, procs, smids);
        } catch (Throwable e) {
            log.error("领料单异常", e);
        }

    }

    /**
     * 保存或更新
     *
     * @param mids
     * @param procs
     * @param smids
     */
    public void saveOrUpdate(List<MidItemStock> mids, List<ProcAllocation> procs, List<OutStoreMid> smids) {
        if (CollectionUtils.isNotEmpty(mids)) {
            List<MidItemStock> collect = mids.stream().map(data -> {
                MidItemStock mid = new MidItemStock();
                mid.setId(data.getId());
                mid.setAllocUsed(data.getAllocUsed());
                return mid;
            }).collect(Collectors.toList());
            midService.updateBatchById(collect);
        }
        if (CollectionUtils.isNotEmpty(procs)) {
            List<ProcAllocation> list = procs.stream().map(data -> {
                ProcAllocation pa = new ProcAllocation();
                pa.setId(data.getId());
                pa.setMidCount(data.getMidCount());
                pa.setTotalCount(data.getTotalCount());
                return pa;
            }).collect(Collectors.toList());
            procAllocationService.updateBatchById(list);
        }
        if (CollectionUtils.isNotEmpty(smids)) {
            outStoreMidService.saveBatch(smids);
        }
    }

    private static OutStoreMid buildOrderMid(OrderDtl dtl, MidItemStock mid, BigDecimal useCount) {
        OutStoreMid storeMid = new OutStoreMid();
        storeMid.setMidId(mid.getId());
        storeMid.setItemCount(useCount);
        storeMid.setOrderDtlId(dtl.getId());
        return storeMid;
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createOutStore(BigDecimal count, String itemNo, String procedureCode, String bizId, String bizType, String userId) {
        List<OutStoreOrder> osos = new ArrayList<>();
        try {
            List<MesProcedure> procedures =
                    procedureMapper.selectList(new LambdaQueryWrapper<MesProcedure>().eq(MesProcedure::getItemNo,
                            itemNo).orderByAsc(MesProcedure::getSeqNo));
            int min = procedures.stream().mapToInt(MesProcedure::getSeqNo).min().getAsInt();
            MesProcedure p = null;
            for (MesProcedure procedure : procedures) {
                if (procedure.getProcedureCode().equals(procedureCode)) {
                    if (procedure.getSeqNo() != min) {
                        log.info("{}工单不是第一道工序，不产生领料单", bizId);
                        return;
                    } else {
                        p = procedure;
                    }
                }
            }
            List<MesItemUse> itemUses =
                    itemUseMapper.selectList(new LambdaQueryWrapper<MesItemUse>().eq(MesItemUse::getItemNo,
                            itemNo).eq(MesItemUse::getUseItemType, ItemType.MATERIALS.getCode()));
            if (CollectionUtils.isEmpty(itemUses)) {
                return;
            }
            for (MesItemUse miu : itemUses) {
                OutStoreOrder oso = new OutStoreOrder();
                oso.setDeptId(p.getDeptId());
                oso.setItemNo(itemNo);
                oso.setUseItemNo(miu.getUseItemNo());
                oso.setAssignCount(count);
                oso.setPlanCount(ArithUtil.mul(count, miu.getUseItemCount()));
                oso.setRealCount(BigDecimal.ZERO);
                oso.setBizId(bizId);
                oso.setBizType(bizType);
                oso.setOutUser(userId);
                osos.add(oso);
            }
        } catch (Exception e) {
            log.error("原材料领料单生成异常,bizId:{}", bizId, e);
        }
        if (CollectionUtils.isNotEmpty(osos)) {
            outStoreOrderService.saveBatch(osos);
        }
    }


}

package cn.jb.boot.biz.order.service.impl;

import cn.jb.boot.biz.order.entity.ProcAllocation;
import cn.jb.boot.biz.order.mapper.ProcAllocationMapper;
import cn.jb.boot.biz.order.service.OrderDtlService;
import cn.jb.boot.biz.order.service.ProcAllocationService;
import cn.jb.boot.biz.order.util.SeqUtil;
import cn.jb.boot.biz.order.vo.request.BatchProcAllocReq;
import cn.jb.boot.biz.order.vo.request.DistListRequest;
import cn.jb.boot.biz.order.vo.request.DistOuterReq;
import cn.jb.boot.biz.order.vo.request.OuterPubPageRequest;
import cn.jb.boot.biz.order.vo.request.ProcAllocationPageRequest;
import cn.jb.boot.biz.order.vo.request.ProcAllocationUpdateRequest;
import cn.jb.boot.biz.order.vo.request.SingleProcAllocReq;
import cn.jb.boot.biz.order.vo.response.DistInfoResponse;
import cn.jb.boot.biz.order.vo.response.OuterDistInfoResponse;
import cn.jb.boot.biz.order.vo.response.ProcAllocationInfoResponse;
import cn.jb.boot.biz.order.vo.response.ProcAllocationPageResponse;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.work.entity.WorkOrder;
import cn.jb.boot.biz.work.entity.WorkOrderRecord;
import cn.jb.boot.biz.work.service.WorkOrderRecordService;
import cn.jb.boot.biz.work.service.WorkOrderService;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.DictUtil;
import cn.jb.boot.util.PageConvert;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.SnowFlake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tika.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

 import cn.jb.boot.biz.order.vo.request.OrderDtlUpdateStatusRequest;



/**
 * 工序分配表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Service
public class ProcAllocationServiceImpl extends ServiceImpl<ProcAllocationMapper, ProcAllocation> implements ProcAllocationService {

    @Resource
    private ProcAllocationMapper mapper;
    @Resource
    private WorkOrderService workOrderService;
    @Resource
    private SeqUtil seqUtil;

    @Resource
    private WorkOrderRecordService workOrderRecordService;


//    查询部件的工序列表
    @Override
    public BaseResponse<List<ProcAllocationPageResponse>> pageInfo(Paging page, ProcAllocationPageRequest params) {
        PageUtil<ProcAllocationPageResponse, ProcAllocationPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return PageConvert.convert(pu.page(page, params), ProcAllocationPageResponse.class, (t, r) -> {
            t.setItemName(DictUtil.getDictName(DictType.BOM_NO, t.getItemNo()));
        });
    }

    @Override
    public List<ProcAllocationInfoResponse> getByIds(List<String> ids) {
        List<ProcAllocation> list = this.listByIds(ids);
        return PojoUtil.copyList(list, ProcAllocationInfoResponse.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateStatus(ProcAllocationUpdateRequest params) {
        List<ProcAllocation> list = params.getIds().stream().map(d -> {
            ProcAllocation pa = new ProcAllocation();
            pa.setId(d);
            pa.setProcStatus(params.getStatus());
            return pa;
        }).collect(Collectors.toList());
        this.updateBatchById(list);
        List<String> ids = list.stream().map(ProcAllocation::getId).collect(Collectors.toList());
        List<WorkOrder> wos = workOrderService.list(new LambdaQueryWrapper<WorkOrder>().in(WorkOrder::getAllocId, ids));
        if (CollectionUtils.isNotEmpty(wos)) {
            workOrderService.updateBatchById(wos);
        }
    }

    @Override
    public List<DistInfoResponse> distList(DistListRequest params) {
        List<DistInfoResponse> returnList = mapper.distList(params);
        for(DistInfoResponse data:returnList){
            data.setWorkItemCount(String.valueOf(data.getTotalCount()));
        }
        return returnList;
    }


    @Resource
    private OrderDtlService orderDtlService;


    //    批量工单下达
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createWorkOrder(BatchProcAllocReq params) {
        // 1. 获取批量分配请求里的明细列表
        List<SingleProcAllocReq> list = params.getList();
        // 2. 收集明细里的主键id
        List<String> ids = list.stream().map(SingleProcAllocReq::getId).collect(Collectors.toList());
        // 3. 根据id批量查出已有的工序分配记录
        List<ProcAllocation> pas = this.listByIds(ids);
        // 4. 把查到的工序分配列表转为map，key为id，value为分配对象，便于后续处理
        Map<String, ProcAllocation> map = pas.stream().collect(Collectors.toMap(ProcAllocation::getId,
                Function.identity()));
        // 5. 保存工单（工序）核心逻辑（shiftType为班次类型,GroupId班组，list为本次分配明细，map为历史数据）
        saveWorkOrder(params.getShiftType(),params.getGroupId(), list, map);
        // 6. 批量更新工序分配记录
        this.updateBatchById(pas);

// —— todo：下发完成后，检查每个 orderDtlId，若该子件下所有工序已分配，则改状态为09
        List<String> detailIds = pas.stream()
                .map(ProcAllocation::getOrderDtlId)
                .distinct()
                .collect(Collectors.toList());
        for (String detailId : detailIds) {
            // 查询该明细下的所有工序分配记录
            List<ProcAllocation> allPas = this.list(
                    new LambdaQueryWrapper<ProcAllocation>()
                            .eq(ProcAllocation::getOrderDtlId, detailId)
            );
            // 判断是否全部已分配（工厂+外协 >= 总数）
            boolean allScheduled = allPas.stream().allMatch(pa ->
                    pa.getWorkerAllocCount()
                            .add(pa.getOuterAllocCount())
                            .compareTo(pa.getTotalCount()) >= 0
            );
            if (allScheduled) {
                // 构建更新请求
                OrderDtlUpdateStatusRequest req = new OrderDtlUpdateStatusRequest();
                req.setId(detailId);
                req.setOrderDtlStatus("09");
                // 调用服务更新状态为“已排产”
                orderDtlService.updateStatus(req);
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByWorkId(ComId params) {
        WorkOrder wo = workOrderService.getById(params.getId());
        ProcAllocation pa = this.getById(wo.getAllocId());
        BigDecimal sub = ArithUtil.sub(pa.getWorkerAllocCount(), wo.getPlanTotalCount());
        pa.setWorkerAllocCount(sub);
        if (wo.getAssignCount().compareTo(BigDecimal.ZERO) > 0) {
            throw new CavException("已下达，无法删除");
        }
        WorkOrderRecord record = new WorkOrderRecord();
        record.setWorkOrderId(wo.getId());
        record.setItemCount(ArithUtil.sub(BigDecimal.ZERO, wo.getPlanTotalCount()));
        wo.setDataStatus(Constants.STATUS_01);
        workOrderRecordService.save(record);
        workOrderService.updateById(wo);
        this.updateById(pa);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void distributionOuter(DistOuterReq params) {
        List<DistOuterReq.DistDtl> list = params.getList();
        List<String> ids = list.stream().map(DistOuterReq.DistDtl::getId).collect(Collectors.toList());
        List<ProcAllocation> pas = this.listByIds(ids);
        Map<String, ProcAllocation> map = pas.stream().collect(Collectors.toMap(ProcAllocation::getId, Function.identity(), (v1, v2) -> v1));
        List<ProcAllocation> paList = new ArrayList<>();
        for (DistOuterReq.DistDtl dtl : list) {
            String id = dtl.getId();
            BigDecimal count = dtl.getCount();
            ProcAllocation pa = map.get(id);
            BigDecimal workerAllocCount = pa.getWorkerAllocCount();
            BigDecimal total = ArithUtil.add(workerAllocCount, pa.getOuterAllocCount());
            if (ArithUtil.gt(ArithUtil.add(total, count), pa.getTotalCount())) {
                throw new CavException("外协分配数量超过待分配数量,工序:" + pa.getProcedureName());
            }
            ProcAllocation up = new ProcAllocation();
            up.setId(id);
            up.setOuterAllocCount(count);
            paList.add(up);
        }
        this.updateBatchById(paList);
    }

    @Override
    public List<OuterDistInfoResponse> outerDistList(ComIdsReq params) {
        List<ProcAllocation> procAllocations = this.listByIds(params.getIds());
        return PojoUtil.copyList(procAllocations, OuterDistInfoResponse.class);
    }

    @Override
    public BaseResponse<List<OuterDistInfoResponse>> outerPubList(Paging page, OuterPubPageRequest params) {
        PageUtil<OuterDistInfoResponse, OuterPubPageRequest> pu = (p, q) -> mapper.outerPubList(p, q);
        return pu.page(page, params);
    }


    private void saveWorkOrder(String shiftType, String groupId, List<SingleProcAllocReq> list, Map<String,
            ProcAllocation> map) {
        List<String> woIds = list.stream().map(SingleProcAllocReq::getWorkOrderId).collect(Collectors.toList());
        Map<String, WorkOrder> woMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(woIds)) {
            List<WorkOrder> workOrders = workOrderService.listByIds(woIds);
            woMap = workOrders.stream().collect(Collectors.toMap(WorkOrder::getId, Function.identity()));
        }
        List<WorkOrderRecord> records = new ArrayList<>();

        List<WorkOrder> woList = new ArrayList<>();
        for (SingleProcAllocReq sq : list) {
            ProcAllocation pa = map.get(sq.getId());
            WorkOrderRecord record = new WorkOrderRecord();
            WorkOrder wo = new WorkOrder();
            wo.setId(sq.getWorkOrderId());
            wo.setAllocId(sq.getId());
            wo.setShiftType(shiftType);
            wo.setDeviceId(sq.getDeviceId());
            wo.setPlanTotalCount(sq.getAllocCount());
            wo.setDeptId(pa.getDeptId());
            wo.setItemNo(pa.getItemNo());
            wo.setHoursFixed(pa.getHoursFixed());
            wo.setOrderDtlId(pa.getOrderDtlId());
            wo.setProcedureCode(pa.getProcedureCode());
            wo.setProcedureName(pa.getProcedureName());

            wo.setState("就绪"); // 【新增】设置工单状态为“就绪”
            wo.setGroupId(groupId);   // 【新增】班次

            if (StringUtils.isEmpty(sq.getWorkOrderId())) {
                String workOrderNo = seqUtil.workOrderNo();
                wo.setWorkOrderNo(workOrderNo);
                wo.setId(SnowFlake.genId());
                pa.setWorkerAllocCount(ArithUtil.add(pa.getWorkerAllocCount(), wo.getPlanTotalCount()));
                record.setItemCount(wo.getPlanTotalCount());
            } else {
                WorkOrder workOrder = woMap.get(wo.getId());
                if (sq.getAllocCount().compareTo(workOrder.getAssignCount()) < 0) {
                    throw new CavException("分配数量不能小于已下达数量数量");
                }
                BigDecimal sub = ArithUtil.sub(wo.getPlanTotalCount(), workOrder.getPlanTotalCount());
                pa.setWorkerAllocCount(ArithUtil.add(pa.getWorkerAllocCount(), sub));
                record.setItemCount(sub);

            }
            record.setWorkOrderId(wo.getId());
            wo.setProcStatus(pa.getProcStatus());
            wo.setDataStatus(Constants.STATUS_00);
            woList.add(wo);
            if (record.getItemCount().compareTo(BigDecimal.ZERO) > 0) {
                records.add(record);
            }
            if (pa.getWorkerAllocCount().add(pa.getOuterAllocCount()).compareTo(pa.getTotalCount()) > 0) {
                throw new CavException("分配数量超过了可分配总数!");
            }
        }
        if (CollectionUtils.isNotEmpty(records)) {
            workOrderRecordService.saveBatch(records);
        }
        workOrderService.saveOrUpdateBatch(woList);

    }


}

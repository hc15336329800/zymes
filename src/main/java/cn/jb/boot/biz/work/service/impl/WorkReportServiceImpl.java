package cn.jb.boot.biz.work.service.impl;

import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.biz.order.entity.OrderDtl;
import cn.jb.boot.biz.order.manager.ProcAllocationManager;
import cn.jb.boot.biz.order.service.OrderDtlService;
import cn.jb.boot.biz.work.entity.WorkOrder;
import cn.jb.boot.biz.work.entity.WorkReport;
import cn.jb.boot.biz.work.enums.ReportStatus;
import cn.jb.boot.biz.work.mapper.WorkReportMapper;
import cn.jb.boot.biz.work.service.WorkOrderService;
import cn.jb.boot.biz.work.service.WorkReportService;
import cn.jb.boot.biz.work.vo.request.ReportUpdateStatusRequest;
import cn.jb.boot.biz.work.vo.request.WorkReportPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkReportPageResponse;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.ThreadPool;
import cn.jb.boot.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import cn.jb.boot.biz.item.entity.MidItemStock;

/**
 * 工单报工表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Service
public class WorkReportServiceImpl extends ServiceImpl<WorkReportMapper, WorkReport> implements WorkReportService {

    @Resource
    private WorkReportMapper mapper;
    @Resource
    private ProcAllocationManager procAllocationManager;
    @Resource
    private WorkOrderService workOrderService;
    // 新增依赖
    @Resource
    private MidItemStockService midItemStockService;
    @Resource
    private OrderDtlService orderDtlService;
    @Override
    public BaseResponse<List<WorkReportPageResponse>> pageInfo(Paging page, WorkReportPageRequest params) {
        PageUtil<WorkReportPageResponse, WorkReportPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

//    报工审批按钮
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateStatus(ReportUpdateStatusRequest params) {
        // 获取要更新的报工记录ID列表
        List<String> ids = params.getIds();
        // 批量查询报工记录
        List<WorkReport> workReports = this.listByIds(ids);
        // 提取所有关联的工单ID
        List<String> wordIds = workReports.stream().map(WorkReport::getWorkOrderId).collect(Collectors.toList());
        // 批量查询关联工单
        List<WorkOrder> workOrders = workOrderService.listByIds(wordIds);
        // 构建工单ID到工单对象的映射
        Map<String, WorkOrder> map = workOrders.stream().collect(Collectors.toMap(WorkOrder::getId,
                Function.identity()));
        List<WorkReport> ups = new ArrayList<>(); // 需要更新的报工记录集合
        List<WorkOrder> upWoList = new ArrayList<>(); // 需要更新的工单集合
        for (WorkReport wr : workReports) {
            // 只处理“待审核”状态的报工记录
            if (ReportStatus.TO_CHECK.getCode().equals(wr.getStatus())) {
                wr.setStatus(params.getStatus());// 设置新状态
                wr.setRemark(params.getMessage());// 设置备注
                wr.setQuaUser(UserUtil.uid()); // 填充审核人
                wr.setQuaTime(LocalDateTime.now()); // 填充审核时间
                ups.add(wr); // 加入待更新列表
            }
            // 若本次操作为“审核拒绝”，同步回退工单的待审核数量
            if(ReportStatus.CHECK_REJECT.getCode().equals(params.getStatus())){
                WorkOrder workOrder = map.get(wr.getWorkOrderId());
                WorkOrder wo = new WorkOrder();
                wo.setId(workOrder.getId());
                // 回退工单的 toReviewRealCount 字段
                wo.setToReviewRealCount(ArithUtil.sub(workOrder.getToReviewRealCount(),wr.getRealCount()));
                upWoList.add(wo);// 加入待更新工单列表
            }
        }
        // 批量更新工单报工表  t_work_report
        if (CollectionUtils.isNotEmpty(ups)) {
            this.updateBatchById(ups);
        }
        // 批量更新工单表  t_work_order
        if (CollectionUtils.isNotEmpty(upWoList)) {
            workOrderService.updateBatchById(upWoList);
        }
    }


    //批量更新报工单的验收状态    新增0812 ：更新已生产数量   (微测试  感觉不太行)
    //  当执行    @PostMapping("/update_verify_status") 报工验收接口的时候。  需要根据bom号查询t_mid_item_stock的last_flag字段是否为01（表示最后一个工序）。是的话  t_order_dtl表的production_count（已生产数量）字段  进行+1
//    @Override
//    @Transactional(rollbackFor = Throwable.class)
//    public void updateVerifyStatus(ReportUpdateStatusRequest params) {
//        List<String> ids = params.getIds();
//        List<WorkReport> workReports = this.listByIds(ids);
//        List<WorkReport> passList = new ArrayList<>();
//
//        List<String> wordIds = workReports.stream()
//                .map(WorkReport::getWorkOrderId).collect(Collectors.toList());
//        Map<String, WorkOrder> map = workOrderService.listByIds(wordIds).stream()
//                .collect(Collectors.toMap(WorkOrder::getId, Function.identity()));
//
//        List<WorkReport> upList = new ArrayList<>();
//        List<WorkOrder> upWoList = new ArrayList<>();
//
//        for (WorkReport wr : workReports) {
//            if (ReportStatus.TO_REVIEW.getCode().equals(wr.getStatus())) {
//                wr.setStatus(params.getStatus());
//                wr.setRemark(params.getMessage());
//                wr.setVerifyUser(UserUtil.uid());
//                wr.setVerifyTime(LocalDateTime.now());
//
//                WorkOrder workOrder = map.get(wr.getWorkOrderId());
//
//                if (ReportStatus.REVIEW_PASS.getCode().equals(params.getStatus())) {
//                    wr.setPid(workOrder.getAllocId());
//                    WorkOrder wo = new WorkOrder();
//                    wo.setId(workOrder.getId());
//                    wo.setToReviewRealCount(ArithUtil.sub(workOrder.getToReviewRealCount(), wr.getRealCount()));
//                    wo.setToReviewDeffCount(ArithUtil.sub(workOrder.getToReviewDeffCount(), wr.getDeffCount()));
//                    wo.setRealCount(ArithUtil.add(workOrder.getRealCount(), wr.getRealCount()));
//                    wo.setDeffCount(ArithUtil.add(workOrder.getDeffCount(), wr.getDeffCount()));
//                    upWoList.add(wo);
//                    passList.add(wr);
//
//                    // 仅在最后一道工序时，更新订单明细的已生产数量
//                    MidItemStock mid = midItemStockService.getOne(
//                            new LambdaQueryWrapper<MidItemStock>()
//                                    .eq(MidItemStock::getItemNo, workOrder.getItemNo())
//                                    .eq(MidItemStock::getProcedureCode, workOrder.getProcedureCode()));
//                    if (mid != null && "01".equals(mid.getLastFlag())) {
//                        OrderDtl dtl = orderDtlService.getById(workOrder.getOrderDtlId());
//                        if (dtl != null) {
//                            OrderDtl upDtl = new OrderDtl();
//                            upDtl.setId(dtl.getId());
//                            upDtl.setProductionCount(
//                                    ArithUtil.add(dtl.getProductionCount(), BigDecimal.ONE));
//                            orderDtlService.updateById(upDtl);
//                        }
//                    }
//                }
//
//                if (ReportStatus.REVIEW_REJECT.getCode().equals(params.getStatus())) {
//                    WorkOrder wo = new WorkOrder();
//                    wo.setId(workOrder.getId());
//                    wo.setToReviewRealCount(
//                            ArithUtil.sub(workOrder.getToReviewRealCount(), wr.getRealCount()));
//                    upWoList.add(wo);
//                }
//                upList.add(wr);
//            }
//        }
//
//        if (!upWoList.isEmpty()) {
//            upWoList.sort(Comparator.comparing(WorkOrder::getId));
//            workOrderService.updateBatchById(upWoList);
//        }
//        if (!upList.isEmpty()) {
//            this.updateBatchById(upList);
//        }
//        if (!passList.isEmpty()) {
//            ThreadPool.singleExecute(() -> procAllocationManager.updateWorkReport(passList));
//        }
//    }
//

//    批量更新报工单的验收状态  修复死锁
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateVerifyStatus(ReportUpdateStatusRequest params) {
        List<String> ids = params.getIds();
        List<WorkReport> workReports = this.listByIds(ids);
        List<WorkReport> passList = new ArrayList<>();

        // 关联工单一次性查询并转为 Map
        List<String> wordIds = workReports.stream()
                .map(WorkReport::getWorkOrderId).collect(Collectors.toList());
        Map<String, WorkOrder> map = workOrderService.listByIds(wordIds).stream()
                .collect(Collectors.toMap(WorkOrder::getId, Function.identity()));

        List<WorkReport> upList = new ArrayList<>();
        List<WorkOrder> upWoList = new ArrayList<>();

        for (WorkReport wr : workReports) {
            if (ReportStatus.TO_REVIEW.getCode().equals(wr.getStatus())) {
                wr.setStatus(params.getStatus());
                wr.setRemark(params.getMessage());
                wr.setVerifyUser(UserUtil.uid());
                wr.setVerifyTime(LocalDateTime.now());

                WorkOrder workOrder = map.get(wr.getWorkOrderId());

                if (ReportStatus.REVIEW_PASS.getCode().equals(params.getStatus())) {
                    wr.setPid(workOrder.getAllocId());
                    WorkOrder wo = new WorkOrder();
                    wo.setId(workOrder.getId());
                    wo.setToReviewRealCount(ArithUtil.sub(workOrder.getToReviewRealCount(), wr.getRealCount()));
                    wo.setToReviewDeffCount(ArithUtil.sub(workOrder.getToReviewDeffCount(), wr.getDeffCount()));
                    wo.setRealCount(ArithUtil.add(workOrder.getRealCount(), wr.getRealCount()));
                    wo.setDeffCount(ArithUtil.add(workOrder.getDeffCount(), wr.getDeffCount()));
                    upWoList.add(wo);
                    passList.add(wr);
                }

                if (ReportStatus.REVIEW_REJECT.getCode().equals(params.getStatus())) {
                    WorkOrder wo = new WorkOrder();
                    wo.setId(workOrder.getId());
                    wo.setToReviewRealCount(ArithUtil.sub(workOrder.getToReviewRealCount(), wr.getRealCount()));
                    upWoList.add(wo);
                }

                upList.add(wr);
            }
        }

        // 统一按 ID 升序排序后批量更新工单，避免死锁
        if (!upWoList.isEmpty()) {
            upWoList.sort(Comparator.comparing(WorkOrder::getId));
            workOrderService.updateBatchById(upWoList);
        }

        // 批量更新报工
        if (!upList.isEmpty()) {
            this.updateBatchById(upList);
        }

        // 验收通过的报工单异步处理
        if (!passList.isEmpty()) {
            ThreadPool.singleExecute(() -> procAllocationManager.updateWorkReport(passList));
        }
    }



    /**
     * 批量更新报工单的验收状态，并同步更新关联工单的相关数量字段。
     * 注：本方法不涉及 t_work_report_dtl（明细表）插入，仅主表和工单表。
     * @param params 验收状态修改参数（包含报工ID、目标状态、备注等）
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateVerifyStatusV1(ReportUpdateStatusRequest params) {
        // 1. 获取所有待处理报工ID
        List<String> ids = params.getIds();
        // 2. 批量查询报工记录
        List<WorkReport> workReports = this.listByIds(ids);
        // 3. 记录通过验收的报工记录（用于后续异步处理）
        List<WorkReport> passList = new ArrayList<>();
        // 4. 提取所有关联工单ID
        List<String> wordIds = workReports.stream().map(WorkReport::getWorkOrderId).collect(Collectors.toList());
        // 5. 记录需要批量更新的报工记录
        List<WorkReport> upList = new ArrayList<>();
        // 6. 遍历每一条待处理报工记录
        for (WorkReport wr : workReports) {
            // 7. 临时存放需要批量更新的工单
            List<WorkOrder> upWoList = new ArrayList<>();
            // 8. 查询所有关联工单，转为Map结构便于后续取值
            List<WorkOrder> workOrders = workOrderService.listByIds(wordIds);
            // 9. 只处理“待验收”状态的报工
            Map<String, WorkOrder> map = workOrders.stream().collect(Collectors.toMap(WorkOrder::getId,
                    Function.identity()));
            if (ReportStatus.TO_REVIEW.getCode().equals(wr.getStatus())) {
                // 10. 更新报工状态、备注、验收人、验收时间
                wr.setStatus(params.getStatus());
                wr.setRemark(params.getMessage());
                wr.setVerifyUser(UserUtil.uid());
                wr.setVerifyTime(LocalDateTime.now());
                // 11. 获取当前报工关联的工单对象
                WorkOrder workOrder = map.get(wr.getWorkOrderId());
                // 12. 验收通过场景：更新分配ID、同步工单数量字段
                if (ReportStatus.REVIEW_PASS.getCode().equals(params.getStatus())) {
                    wr.setPid(workOrder.getAllocId());// 记录分配ID
                    WorkOrder wo = new WorkOrder();
                    wo.setId(workOrder.getId());
                    // 同步工单数量相关字段（待验收数量减少，实际数量增加）
                    wo.setToReviewRealCount(ArithUtil.sub(workOrder.getToReviewRealCount(), wr.getRealCount()));
                    wo.setToReviewDeffCount(ArithUtil.sub(workOrder.getToReviewDeffCount(), wr.getDeffCount()));
                    wo.setRealCount(ArithUtil.add(workOrder.getRealCount(), wr.getRealCount()));
                    wo.setDeffCount(ArithUtil.add(workOrder.getDeffCount(), wr.getDeffCount()));
                    upWoList.add(wo);
                    // 验收通过的报工，加入后续异步处理队列
                    passList.add(wr);
                }
                // 13. 验收拒绝场景：仅回退待验收良品数量
                if(ReportStatus.REVIEW_REJECT.getCode().equals(params.getStatus())){
                    WorkOrder wo = new WorkOrder();
                    wo.setId(workOrder.getId());
                    wo.setToReviewRealCount(ArithUtil.sub(workOrder.getToReviewRealCount(),wr.getRealCount()));
                    upWoList.add(wo);
                }
                // 14. 当前报工加入批量更新列表
                upList.add(wr);
            }
            // 15. 批量更新工单表（t_work_order）
            workOrderService.updateBatchById(upWoList);
        }
        // //（已废弃）统一批量更新所有工单
        //        if (CollectionUtils.isNotEmpty(upWoList)) {
        //            workOrderService.updateBatchById(upWoList);
        //        }

        // 16. 批量更新报工表（t_work_report）
        if (CollectionUtils.isNotEmpty(upList)) {
            this.updateBatchById(upList);
        }
        // 17. 验收通过的报工单，异步执行后续分配业务（生成工资表）
        if (CollectionUtils.isNotEmpty(passList)) {
            ThreadPool.singleExecute(() -> {
                procAllocationManager.updateWorkReport(passList);
            });
        }
    }
}

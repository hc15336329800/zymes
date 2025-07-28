package cn.jb.boot.biz.work.service.impl;

import cn.jb.boot.biz.depository.manager.OutStoreManager;
import cn.jb.boot.biz.device.entity.DeviceInfo;
import cn.jb.boot.biz.device.service.DeviceInfoService;
import cn.jb.boot.biz.group.entity.GroupDtl;
import cn.jb.boot.biz.group.enums.LeaderType;
import cn.jb.boot.biz.group.service.GroupDtlService;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.tray.mapper.TrayManageInfoMapper;
import cn.jb.boot.biz.work.entity.WorkAssign;
import cn.jb.boot.biz.work.entity.WorkOrder;
import cn.jb.boot.biz.work.entity.WorkReport;
import cn.jb.boot.biz.work.enums.ReportStatus;
import cn.jb.boot.biz.work.enums.ReportTypeEnum;
import cn.jb.boot.biz.work.mapper.WorkOrderMapper;
import cn.jb.boot.biz.work.service.WorkAssignService;
import cn.jb.boot.biz.work.service.WorkOrderService;
import cn.jb.boot.biz.work.service.WorkReportService;
import cn.jb.boot.biz.work.vo.request.*;
import cn.jb.boot.biz.work.vo.response.WorkOrderPageResponse;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.common.core.domain.AjaxResult;
import cn.jb.boot.framework.config.SpringContextUtil;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 工单表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 21:52:39
 */
@Service
@Slf4j
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {

    @Resource
    private WorkOrderMapper mapper;
    @Resource
    private WorkAssignService workAssignService;
    @Resource
    private WorkReportService workReportService;
    @Resource
    private httpGetDataServiceImpl httpGetDataService;
    @Resource
    private GroupDtlService groupDtlService;
    @Resource
    private TrayManageInfoMapper trayManageInfoMapper;
    @Resource
    private MesItemStockService mesItemStockService;
    @Resource
    private DeviceInfoService DeviceInfoService;

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<WorkOrderPageResponse>> pageInfo(Paging page, WorkOrderPageRequest params) {
        PageUtil<WorkOrderPageResponse, WorkOrderPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }



//    下达工单
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addAssign(WorkAssignCreateRequest params) {
        //原下达相关业务
        WorkOrder wo = this.getById(params.getWorkOrderId());

        // 新增：状态判断
//        if ("已下达".equals(wo.getState())) {
//            throw new CavException("当前工单已下达，不能重复下达！");
//        }

        BigDecimal totalAssign = ArithUtil.add(wo.getAssignCount(), params.getAssignCount());
        if (totalAssign.compareTo(wo.getPlanTotalCount()) > 0) {
            throw new CavException("下达数量超过分配数量");
        }
        wo.setAssignCount(totalAssign);


        wo.setState("已下达");        // ★★★ 新增，设置状态为“已下达” ★★★

        this.updateById(wo);// 更新工单
        WorkAssign workAssign = PojoUtil.copyBean(params, WorkAssign.class);
        workAssignService.save(workAssign);
        String uid = UserUtil.uid();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                ThreadPool.commonExecute(() -> SpringContextUtil.getBeanByClass(OutStoreManager.class)
                        .createOutStore(workAssign.getAssignCount(), wo.getItemNo(), wo.getProcedureCode(), wo.getId(), Constants.STATUS_01, uid));
            }
        });
        //托盘相关业务,将下达数据的物料与设备信息保存到表中
        if (!params.getDeviceName().contains("折弯机")) {
            Map<String, Object> mapperData = new HashMap<>();
            //获取32位随机数作为主键
            UUID uuid = UUID.randomUUID();
            String randomCode = uuid.toString().replace("-", "");
            mapperData.put("id", randomCode.substring(0, 32));
            //获取物料名称
            String itemname = "";
            Map<String, Object> it = new HashMap<>();
            it.put("itemno", params.getItemNo());
            Map<String, Object> itemData = mapper.getItemByItemNo(it);
            if (itemData != null) {
                itemname = itemData.get("item_name").toString();
            }
            mapperData.put("bomNo", params.getBomNo());
            mapperData.put("itemno", params.getItemNo());
            mapperData.put("itemname", itemname);
            mapperData.put("deviceName", params.getDeviceName());
            mapperData.put("realCount", "0");
            mapperData.put("deffCount", "0");
            mapperData.put("satus", "0");
            mapperData.put("datetime", DateUtil.nowDateTime());
            mapper.saveItemand(mapperData);
        }
    }


//    批量下达
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addAllAssign(List<WorkAssignCreateRequest> list) {
        for (WorkAssignCreateRequest params : list) {
            WorkOrder wo = this.getById(params.getWorkOrderId());

//            // 验证：是否已下达
//            if ("已下达".equals(wo.getState())) {
//                throw new CavException("工单【" + wo.getWorkOrderNo() + "】已下达，不能重复下达！");
//            }

            // 验证：数量是否超限
            BigDecimal totalAssign = ArithUtil.add(wo.getAssignCount(), params.getAssignCount());
            if (totalAssign.compareTo(wo.getPlanTotalCount()) > 0) {
                throw new CavException("工单【" + wo.getWorkOrderNo() + "】下达数量超出分配数量！");
            }

            // 更新工单
            wo.setAssignCount(totalAssign);
            wo.setState("已下达");
            this.updateById(wo);

            // 保存下达记录
            WorkAssign workAssign = PojoUtil.copyBean(params, WorkAssign.class);
            workAssignService.save(workAssign);

            // 后续逻辑（如出库通知、托盘等）可调用公共方法
        }
    }


//    @Override
//    @Transactional(rollbackFor = Throwable.class)
//    public void addAllAssign(List<String> ids) {
//        for(String id:ids){
//            WorkAssignCreateRequest params = new WorkAssignCreateRequest();
//            WorkOrder workOrder = this.getById(id);
//            params.setWorkOrderId(workOrder.getId());
//            params.setAssignCount(workOrder.getPlanTotalCount().subtract(workOrder.getAssignCount()));
//            params.setItemNo(workOrder.getItemNo());
//            DeviceInfo deviceInfo = DeviceInfoService.getById(workOrder.getDeviceId());
//            params.setDeviceName(deviceInfo.getDeviceName());
//            MesItemStock itemStock =mesItemStockService.getByItemNo(workOrder.getItemNo());
//            params.setBomNo(itemStock.getBomNo());
//            addAssign(params);
//        }
//    }

    /**
     * 正品+待验收正品+待验收次品 -(次品)<= 下达数量
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addReport(WorkReportCreateRequest params) {
        WorkOrder wo = this.getById(params.getWorkOrderId());
        wo.setToReviewDeffCount(wo.getToReviewDeffCount().add(params.getDeffCount()));
        wo.setToReviewRealCount(wo.getToReviewRealCount().add(params.getRealCount()));
        if(wo.getToReviewRealCount().compareTo(wo.getAssignCount()) > 0){
            throw new CavException("报工总数量超过下达数量");
        }
        BigDecimal ac = wo.getAssignCount();
        BigDecimal sub = ArithUtil.add(wo.getRealCount(), wo.getToReviewRealCount(), wo.getToReviewDeffCount()).subtract(wo.getDeffCount());
        if (sub.compareTo(ac) > 0) {
            throw new CavException("报工数量超过下达数量");
        }
        this.updateById(wo);
        WorkReport workReport = PojoUtil.copyBean(params, WorkReport.class);
        workReport.setStatus(Constants.STATUS_00);
        workReportService.save(workReport);
        String groupId = params.getGroupId();
        List<GroupDtl> dtls = groupDtlService.list(new LambdaQueryWrapper<GroupDtl>().eq(GroupDtl::getGroupId, groupId));
        if (CollectionUtils.isEmpty(dtls)) {
            throw new CavException("分组没有成员，请重新选择分组");
        }
        BigDecimal total = ArithUtil.add(dtls.stream().map(GroupDtl::getPercentage).toArray(BigDecimal[]::new));
        if (ReportTypeEnum.PERSON_NUMBER.getCode().equals(params.getReportType())) {
            if (total.compareTo(BigDecimal.valueOf(100)) != 0) {
                throw new CavException("按件数分，比例不为100%,请重新选择分组");
            }
        } else {
            Optional<GroupDtl> any = dtls.stream().filter(d -> LeaderType.LEADER.getCode().equals(d.getLeaderType())).findAny();
            if (!any.isPresent()) {
                throw new CavException("合工没有组长");
            }
        }

    }

    /**
     *
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addAllReport(WorkAllReportCreateRequest params) {
        List<String> ids = params.getIds();
        for(String id:ids){
            WorkReportCreateRequest workReportCreateRequest = new WorkReportCreateRequest();
            WorkOrder workorder = this.getById(id);
            workReportCreateRequest.setWorkOrderId(id);
            workReportCreateRequest.setDeffCount(BigDecimal.ZERO);
            workReportCreateRequest.setGroupId(params.getGroupId());
            workReportCreateRequest.setRealCount(workorder.getPlanTotalCount().subtract(workorder.getToReviewRealCount()).subtract(workorder.getRealCount()));
            workReportCreateRequest.setReportType(params.getReportType());
            addReport(workReportCreateRequest);
        }
    }

    /**
     * 开始加工和加工完成各取一次设备总数，总数相减为本次数量
     */
    @Override
    public void saveItemand(Map<String, Object> params) {
        String ip = "";
        Map<String, Object> paramMap = (Map) params.get("params");
        Map<String, Object> mapperData = new HashMap<>();
        Map<String, Object> completeData = new HashMap<>();
        String deviceName = paramMap.get("deviceName").toString();
        if (deviceName.contains("库卡")) {
            ip = "182.192.0.233";
        } else if (deviceName.contains("发那科")) {
            ip = "182.192.0.236";
        } else {
            return;
        }
        //开始加工,新增数据
        if (paramMap.get("dataStatus").equals(0)) {
            //获取32位随机数作为主键
            UUID uuid = UUID.randomUUID();
            String randomCode = uuid.toString().replace("-", "");
            mapperData.put("id", randomCode.substring(0, 32));
            mapperData.put("itemno", paramMap.get("itemNo"));
            mapperData.put("deviceName", paramMap.get("deviceName"));
            //获取折弯机当时加工总数
            String allCount = httpGetDataService.getDataFromModbus(ip);
            mapperData.put("realCount", "");
            mapperData.put("deffCount", allCount);
            mapperData.put("satus", "0");
            mapperData.put("datetime", DateUtil.nowDateTime());
            mapper.saveItemand(mapperData);
        } else {
            //加工结束,更新数据
            //根据设备获取托盘数据
            Map<String, Object> taryParam = new HashMap<>();
            taryParam.put("deviceName", paramMap.get("deviceName"));
            Map<String, Object> taryData = trayManageInfoMapper.getTrayData(taryParam);
            //获取折弯机当时加工总数
            String allCount = httpGetDataService.getDataFromModbus(ip);
            completeData.put("realCount", allCount);
            completeData.put("trayid", taryData.get("trayid"));
            completeData.put("location", taryData.get("location_code"));
            completeData.put("deviceName", paramMap.get("deviceName"));
            mapper.updateItemand(completeData);
        }
    }

    /**
     * 根据设备和物料获取加工件数
     */
    @Override
    public String getRealnumber(Map<String, Object> params) {
        String result = "";
        try {
            Map<String, Object> itemParams = new HashMap<>();
            String deviceName = params.get("deviceName").toString();
            String bomNo = params.get("bomNo").toString();
            itemParams.put("deviceName", deviceName);
            itemParams.put("bomNo", bomNo);
            Map<String, Object> resultData = mapper.getItemNumber(itemParams);
            result = resultData.get("realCount").toString();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return result;
    }

    @Override
    public BaseResponse<List<WorkOrderPageResponse>> orderPageInfo(Paging page, WorkOrderRequest params) {
        PageUtil<WorkOrderPageResponse, WorkOrderRequest> pu = (p, q) -> mapper.orderPageInfo(p, q);
        return pu.page(page, params);
    }



    /**
     * 批量报工处理逻辑
     * 特点：先对全部请求数据进行校验，全部校验通过后统一写入数据库，保证数据一致性。
     * 事务：若保存或更新过程中出现异常，返回提醒    继续执行，  不执行回滚
     * @param requestList 报工请求数据列表
     * @return AjaxResult 返回成功数量及失败工单号
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public AjaxResult addReportAll(List<WorkReportCreateRequest> requestList) {
        // 日志对象，类上声明： private static final Logger log = LoggerFactory.getLogger(WorkOrderServiceImpl.class);

        if (CollectionUtils.isEmpty(requestList)) {
            return AjaxResult.error("请求参数不能为空");
        }

        List<String> failedWorkOrderNos = new ArrayList<>(); // 存放失败工单号
        int successCount = 0;

        WorkOrder wo = null; // 提前声明，作用域扩大到 try-catch 外


        for (WorkReportCreateRequest params : requestList) {
            String workOrderId = params.getWorkOrderId();

            try {
                // 获取工单信息
                 wo = this.getById(workOrderId);
                if (wo == null) {
                    failedWorkOrderNos.add("工单ID: " + workOrderId + "（不存在）");
                    continue;
                }


                // 校验是否已有待审核或待验收的报工记录  -- 重点
                long pending = workReportService.count(
                        new LambdaQueryWrapper<WorkReport>()
                                .eq(WorkReport::getWorkOrderId, workOrderId)
                                .in(WorkReport::getStatus,
                                        ReportStatus.TO_CHECK.getCode(),
                                        ReportStatus.TO_REVIEW.getCode())
                );
                if (pending > 0) {
                    failedWorkOrderNos.add(wo.getWorkOrderNo() + " 已有报工待审核，禁止重复提交");
                    continue;
                }


                // 校验：如果工单已有待验收正品数量，则直接跳过本次报工
                if (wo.getToReviewRealCount().compareTo(BigDecimal.ZERO) > 0) {
                    log.info("工单 {} 已存在待验收正品数量 {}，跳过报工", wo.getWorkOrderNo(), wo.getToReviewRealCount());
                    continue;
                }

                // 更新待验收数量
                wo.setToReviewDeffCount(wo.getToReviewDeffCount().add(params.getDeffCount()));
                wo.setToReviewRealCount(wo.getToReviewRealCount().add(params.getRealCount()));


//                // 更新待验收数量，批量报工默认以已下达数量为正品数
//                BigDecimal assignCount = wo.getAssignCount();
//                wo.setToReviewDeffCount(wo.getToReviewDeffCount().add(params.getDeffCount()));
//                wo.setToReviewRealCount(wo.getToReviewRealCount().add(assignCount));


                // 校验：待验收正品 <= 下达数量
                if (wo.getToReviewRealCount().compareTo(wo.getAssignCount()) > 0) {
                    throw new CavException("工单【" + wo.getWorkOrderNo() + "】报工总数量超过下达数量");
                }

                // 校验：正品+待验收正品+待验收次品 - 次品 <= 下达数量
                BigDecimal sub = ArithUtil.add(
                        wo.getRealCount(),
                        wo.getToReviewRealCount(),
                        wo.getToReviewDeffCount()
                ).subtract(wo.getDeffCount());
                if (sub.compareTo(wo.getAssignCount()) > 0) {
                    throw new CavException("工单【" + wo.getWorkOrderNo() + "】报工数量超过下达数量");
                }



                // 更新工单信息
                this.updateById(wo);



                // 构建报工记录并保存
                WorkReport workReport = PojoUtil.copyBean(params, WorkReport.class);
                workReport.setRealCount(params.getRealCount() );          // 使用已下达数量
                workReport.setStatus(Constants.STATUS_00); // 待审核状态
                workReportService.save(workReport);

                successCount++;

            } catch (CavException e) {
                // 捕获业务异常，记录失败工单号
                failedWorkOrderNos.add(wo != null ? wo.getWorkOrderNo() : "工单ID: " + workOrderId);
                log.warn("工单 {} 批量报工业务失败: {}", workOrderId, e.getMessage());
            } catch (Exception e) {
                // 系统异常，抛出触发回滚
                throw new RuntimeException("工单【" + workOrderId + "】报工处理异常", e);
            }
        }

        // 构造返回
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failedWorkOrders", failedWorkOrderNos);

        return AjaxResult.success("批量报工处理完成", result);
    }


}

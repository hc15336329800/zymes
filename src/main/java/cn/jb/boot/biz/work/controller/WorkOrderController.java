package cn.jb.boot.biz.work.controller;

import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.work.service.WorkOrderService;
import cn.jb.boot.biz.work.vo.request.*;
import cn.jb.boot.biz.work.vo.response.WorkOrderPageResponse;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.common.core.domain.AjaxResult;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 21:52:39
 */
@RestController
@RequestMapping("/api/work/work_order")
@Tag(name = "work-order-controller", description = "工单表接口")
public class WorkOrderController {

    @Resource
    private WorkOrderService service;

// 单个工单下达
    @PostMapping("/add_assign")
    @Operation(summary = "新增工单下达表记录")
    public BaseResponse<String> addAssign(@RequestBody @Valid BaseRequest<WorkAssignCreateRequest> request) {
//        WorkAssignCreateRequest params = MsgUtil.params(request);
//        service.addAssign(params);
//        return MsgUtil.ok();
        WorkAssignCreateRequest params = MsgUtil.params(request);
        try {
            service.addAssign(params);
            return MsgUtil.ok("下达成功");
        } catch (RuntimeException ex) {
            return MsgUtil.fail(   ex.getMessage()+"\": 工单已下单, 请勿重复下达。\" ");
        }
    }

//    报工按钮
    @PostMapping("/add_report")
    @Operation(summary = "新增工单报工表记录")
    public BaseResponse<String> addReport(@RequestBody @Valid BaseRequest<WorkReportCreateRequest> request) {
        WorkReportCreateRequest params = MsgUtil.params(request);
        service.addReport(params);
        return MsgUtil.ok();
    }


    /**
     * 批量报工接口
     * 接口路径：/work_order/add_report_all
     * 特点：先全校验，再统一保存报工记录、更新工单信息。
     * 返回：成功记录数 + 失败工单号列表
     */
    @PostMapping("/add_report_all")
    public AjaxResult addReportAll(@Valid @RequestBody List<WorkReportCreateRequest> requestList) {
        return service.addReportAll(requestList);
    }
    /**
     *  工单管理  -  列表
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询工单表分页列表")
    public BaseResponse<List<WorkOrderPageResponse>> pageList(@RequestBody @Valid BaseRequest<WorkOrderPageRequest> request) {
        WorkOrderPageRequest params = MsgUtil.params(request);

        // ★★★ 新增：自动写入当前登录用户 ID
        params.setUserId(UserUtil.uid());   // = UserUtil.user().getID()

        return service.pageInfo(request.getPage(), params);
    }




    @PostMapping("/saveItemand")
    @Operation(summary = "新增确认完成下达物料及件数")
    public BaseResponse<String> saveItemand(@RequestBody Map<String, Object> params) {
        service.saveItemand(params);
        return MsgUtil.ok();
    }

    @PostMapping("/getRealnumber")
    @Operation(summary = "获取切割数量")
    public BaseResponse<String> getRealnumber(@RequestBody Map<String, Object> params) {
        String result = service.getRealnumber(params);
        return MsgUtil.ok(result);
    }



    //批量下达 new
    @PostMapping("/add_All_assign")
    @Operation(summary = "下达所有任务")
    public BaseResponse<String> addAllassign(@RequestBody @Valid BaseRequest<List<WorkAssignCreateRequest>> request) {
        List<WorkAssignCreateRequest> list = MsgUtil.params(request);
        try {
            service.addAllAssign(list);
            return MsgUtil.ok("批量下达成功");
        } catch (RuntimeException ex) {
            return MsgUtil.fail(ex.getMessage());
        }
    }


    @PostMapping("/add_All_report")
    @Operation(summary = "新增所有工单报工表记录")
    public BaseResponse<String> addAllReport(@RequestBody @Valid BaseRequest<WorkAllReportCreateRequest> request) {
        WorkAllReportCreateRequest params = MsgUtil.params(request);
        service.addAllReport(params);
        return MsgUtil.ok();
    }

    /**
     * 激光切割查询工单信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/order_page_list")
    @Operation(summary = "激光切割查询工单表分页列表")
    public BaseResponse<List<WorkOrderPageResponse>> orderPageList(@RequestBody @Valid BaseRequest<WorkOrderRequest> request) {
        WorkOrderRequest params = MsgUtil.params(request);
        return service.orderPageInfo(request.getPage(), params);
    }
}

package cn.jb.boot.biz.work.controller;

import cn.jb.boot.biz.work.service.WorkReportService;
import cn.jb.boot.biz.work.vo.request.ReportUpdateStatusRequest;
import cn.jb.boot.biz.work.vo.request.WorkReportPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkReportPageResponse;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 工单报工
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@RestController
@RequestMapping("/api/work/work_report")
@Tag(name = "work-report-controller", description = "工单报工表接口")
public class WorkReportController {

    @Resource
    private WorkReportService service;


    /**
     * 报工审批列表
     * 此处查询涉及到5张表：t_work_report、t_work_order、t_group_info、mes_item_stock、t_order_dtl
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询工单报工表分页列表")
    public BaseResponse<List<WorkReportPageResponse>> pageList(@RequestBody @Valid BaseRequest<WorkReportPageRequest> request) {
        WorkReportPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }


    //    报工审批按钮
    @PostMapping("/update_status")
    @Operation(summary = "修改审核状态")
    public BaseResponse<String> updateStatus(@RequestBody @Valid BaseRequest<ReportUpdateStatusRequest> request) {
        service.updateStatus(MsgUtil.params(request));
        return MsgUtil.ok();
    }


//    验收通过/拒绝
    @PostMapping("/update_verify_status")
    @Operation(summary = "修改验收状态")
    public BaseResponse<String> updateVerifyStatus(@RequestBody @Valid BaseRequest<ReportUpdateStatusRequest> request) {
        service.updateVerifyStatus(MsgUtil.params(request));
        return MsgUtil.ok();
    }
}

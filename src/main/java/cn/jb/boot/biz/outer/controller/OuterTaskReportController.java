package cn.jb.boot.biz.outer.controller;

import cn.jb.boot.biz.outer.service.OuterTaskReportService;
import cn.jb.boot.biz.outer.vo.request.OuterTaskReportCreateRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskReportPageRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskReportUpdateRequest;
import cn.jb.boot.biz.outer.vo.response.OuterTaskReportInfoResponse;
import cn.jb.boot.biz.outer.vo.response.OuterTaskReportPageResponse;
import cn.jb.boot.biz.work.vo.request.ReportUpdateStatusRequest;
import cn.jb.boot.framework.com.entity.ComId;
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
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@RestController
@RequestMapping("/api/outer/outer_task_report")
@Tag(name = "outer-task-report-controller", description = "外协任务报工接口")
public class OuterTaskReportController {

    @Resource
    private OuterTaskReportService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增外协任务报工记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<OuterTaskReportCreateRequest> request) {
        OuterTaskReportCreateRequest params = MsgUtil.params(request);
        service.createInfo(params);
        return MsgUtil.ok();
    }


    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询外协任务报工分页列表")
    public BaseResponse<List<OuterTaskReportPageResponse>> pageList(@RequestBody @Valid BaseRequest<OuterTaskReportPageRequest> request) {
        OuterTaskReportPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }

    @PostMapping("/update_status")
    @Operation(summary = "更新任务状态")
    public BaseResponse<String> updateStatus(@RequestBody @Valid BaseRequest<ReportUpdateStatusRequest> request) {
        ReportUpdateStatusRequest params = MsgUtil.params(request);
        service.updateStatus(params);
        return MsgUtil.ok();
    }


}

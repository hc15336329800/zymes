package cn.jb.boot.biz.outer.controller;

import cn.jb.boot.biz.outer.service.OuterTaskService;
import cn.jb.boot.biz.outer.vo.request.OuterTaskCreateRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskPageRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskUpdateRequest;
import cn.jb.boot.biz.outer.vo.response.OuterTaskInfoResponse;
import cn.jb.boot.biz.outer.vo.response.OuterTaskPageResponse;
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
@RequestMapping("/api/outer/outer_task")
@Tag(name = "outer-task-controller", description = "外协任务接口")
public class OuterTaskController {

    @Resource
    private OuterTaskService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增外协任务记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<OuterTaskCreateRequest> request) {
        OuterTaskCreateRequest params = MsgUtil.params(request);
        service.createInfo(params);
        return MsgUtil.ok();
    }

    /**
     * 删除记录
     *
     * @param request 主键
     * @return 是否成功
     */
    @PostMapping("/delete")
    @Operation(summary = "根据主键删除外协任务记录")
    public BaseResponse<String> delete(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        service.removeById(params.getId());
        return MsgUtil.ok();
    }


    @PostMapping("/list_by_pid")
    @Operation(summary = "按分配表查询列表")
    public BaseResponse<List<OuterTaskInfoResponse>> listByPid(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        List<OuterTaskInfoResponse> list = service.listByPid(params.getId());
        return MsgUtil.ok(list);
    }


    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询外协任务分页列表")
    public BaseResponse<List<OuterTaskPageResponse>> pageList(@RequestBody @Valid BaseRequest<OuterTaskPageRequest> request) {
        OuterTaskPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }

    @PostMapping("/accept")
    @Operation(summary = "新增外协任务报工记录")
    public BaseResponse<String> accept(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        service.accept(params);
        return MsgUtil.ok();
    }

}

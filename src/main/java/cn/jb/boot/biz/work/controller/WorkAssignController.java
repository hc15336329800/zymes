package cn.jb.boot.biz.work.controller;

import cn.jb.boot.biz.work.service.WorkAssignService;
import cn.jb.boot.biz.work.vo.request.WorkAssignPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkAssignPageResponse;
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
 * @since 2024-01-09 14:06:38
 */
@RestController
@RequestMapping("/api/work/work_assign")
@Tag(name = "work-assign-controller", description = "工单下达表接口")
public class WorkAssignController {

    @Resource
    private WorkAssignService service;


    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询工单下达表分页列表")
    public BaseResponse<List<WorkAssignPageResponse>> pageList(@RequestBody @Valid BaseRequest<WorkAssignPageRequest> request) {
        WorkAssignPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }
}

package cn.jb.boot.system.controller;


import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.entity.TreeSelect;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.service.DeptInfoService;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.request.DeptInfoCreateRequest;
import cn.jb.boot.system.vo.request.DeptInfoUpdateRequest;
import cn.jb.boot.system.vo.request.DeptTreeIdRequest;
import cn.jb.boot.system.vo.response.DeptInfoDetailResponse;
import cn.jb.boot.system.vo.response.DeptTreeSelectResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
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
 * @author xieyubin
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-09-06 11:22:14
 */
@RestController
@RequestMapping("/api/system/dept")
@Tag(name = "dept-info-controller", description = "部门信息接口")
public class DeptInfoController {

    @Resource
    private DeptInfoService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增部门信息记录")
    public BaseResponse<String> addDept(@RequestBody @Valid BaseRequest<DeptInfoCreateRequest> request) {
        DeptInfoCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除部门信息记录")
    public BaseResponse<String> deleteDept(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        service.deleteDept(params.getId());
        return MsgUtil.ok();
    }


    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改部门信息记录")
    public BaseResponse<String> updateDept(@RequestBody @Valid BaseRequest<DeptInfoUpdateRequest> request) {
        DeptInfoUpdateRequest params = MsgUtil.params(request);
        service.updateInfo(params);
        return MsgUtil.ok();
    }

    @PostMapping("/detail")
    @Operation(summary = "部门信息详情")
    public BaseResponse<DeptInfoDetailResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        DeptInfoDetailResponse response = service.detail(MsgUtil.params(request));
        return MsgUtil.ok(response);
    }


    /**
     * 部门树
     *
     * @param request 查询参数
     * @return 部门树
     */
    @PostMapping("/tree")
    @Operation(summary = "部门树")
    public BaseResponse<List<DeptTreeSelectResponse>> deptTree(@RequestBody @Valid BaseRequest<DeptTreeIdRequest> request) {
        DeptTreeIdRequest params = MsgUtil.params(request);
        List<DeptTreeSelectResponse> tree = service.deptTree(params.getPaterId());
        return MsgUtil.ok(tree);
    }
}

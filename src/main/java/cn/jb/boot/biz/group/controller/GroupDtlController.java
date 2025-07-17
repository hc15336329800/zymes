package cn.jb.boot.biz.group.controller;

import cn.jb.boot.biz.group.service.GroupDtlService;
import cn.jb.boot.biz.group.vo.request.GroupDtlCreateRequest;
import cn.jb.boot.biz.group.vo.request.GroupDtlPageRequest;
import cn.jb.boot.biz.group.vo.request.GroupDtlUpdateRequest;
import cn.jb.boot.biz.group.vo.response.GroupDtlInfoResponse;
import cn.jb.boot.biz.group.vo.response.GroupDtlPageResponse;
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
 * @since 2024-01-06 20:38:27
 */
@RestController
@RequestMapping("/api/group/group_dtl")
@Tag(name = "group-dtl-controller", description = "分组明细接口")
public class GroupDtlController {

    @Resource
    private GroupDtlService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增分组明细记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<GroupDtlCreateRequest> request) {
        GroupDtlCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除分组明细记录")
    public BaseResponse<String> delete(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        service.removeById(params.getId());
        return MsgUtil.ok();
    }

    /**
     * 查询详情
     *
     * @param request 主键
     * @return 记录信息
     */
    @PostMapping("/detail")
    @Operation(summary = "查询分组明细记录")
    public BaseResponse<GroupDtlInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        GroupDtlInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改分组明细记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<GroupDtlUpdateRequest> request) {
        GroupDtlUpdateRequest params = MsgUtil.params(request);
        service.updateInfo(params);
        return MsgUtil.ok();
    }


    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询分组明细分页列表")
    public BaseResponse<List<GroupDtlPageResponse>> pageList(@RequestBody @Valid BaseRequest<GroupDtlPageRequest> request) {
        GroupDtlPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }
}

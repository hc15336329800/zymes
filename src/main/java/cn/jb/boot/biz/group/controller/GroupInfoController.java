package cn.jb.boot.biz.group.controller;

import cn.jb.boot.biz.group.service.GroupInfoService;
import cn.jb.boot.biz.group.vo.request.GroupInfoCreateRequest;
import cn.jb.boot.biz.group.vo.request.GroupInfoPageRequest;
import cn.jb.boot.biz.group.vo.request.GroupInfoUpdateRequest;
import cn.jb.boot.biz.group.vo.response.GroupInfoInfoResponse;
import cn.jb.boot.biz.group.vo.response.GroupInfoPageResponse;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
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
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@RestController
@RequestMapping("/api/group/group_info")
@Tag(name = "group-info-controller", description = "工人分组接口")
public class GroupInfoController {

    @Resource
    private GroupInfoService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增工人分组记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<GroupInfoCreateRequest> request) {
        GroupInfoCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除工人分组记录")
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
    @Operation(summary = "查询工人分组记录")
    public BaseResponse<GroupInfoInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        GroupInfoInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改工人分组记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<GroupInfoUpdateRequest> request) {
        GroupInfoUpdateRequest params = MsgUtil.params(request);
        service.updateInfo(params);
        return MsgUtil.ok();
    }

    @PostMapping("/selected")
    @Operation(summary = "修改工人分组记录")
    public BaseResponse<List<DictListResponse>> selected() {
        List<DictListResponse> resp = service.selected();
        return MsgUtil.ok(resp);
    }


    /**
     * 查询工人分组分页列表
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询工人分组分页列表")
    public BaseResponse<List<GroupInfoPageResponse>> pageList(@RequestBody @Valid BaseRequest<GroupInfoPageRequest> request) {
        GroupInfoPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }
}

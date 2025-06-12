package cn.jb.boot.biz.device.controller;

import cn.jb.boot.biz.device.service.CheckInfoService;
import cn.jb.boot.biz.device.vo.request.CheckInfoCreateRequest;
import cn.jb.boot.biz.device.vo.request.CheckInfoPageRequest;
import cn.jb.boot.biz.device.vo.request.CheckInfoUpdateRequest;
import cn.jb.boot.biz.device.vo.response.CheckInfoInfoResponse;
import cn.jb.boot.biz.device.vo.response.CheckInfoPageResponse;
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
 * @since 2024-01-01 20:04:08
 */
@RestController
@RequestMapping("/api/device/check_info")
@Tag(name = "check-info-controller", description = "点检信息接口")
public class CheckInfoController {

    @Resource
    private CheckInfoService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增点检信息记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<CheckInfoCreateRequest> request) {
        CheckInfoCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除点检信息记录")
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
    @Operation(summary = "查询点检信息记录")
    public BaseResponse<CheckInfoInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        CheckInfoInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改点检信息记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<CheckInfoUpdateRequest> request) {
        CheckInfoUpdateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "查询点检信息分页列表")
    public BaseResponse<List<CheckInfoPageResponse>> pageList(@RequestBody @Valid BaseRequest<CheckInfoPageRequest> request) {
        CheckInfoPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }
}

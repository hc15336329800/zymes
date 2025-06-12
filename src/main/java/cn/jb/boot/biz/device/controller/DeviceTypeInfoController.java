package cn.jb.boot.biz.device.controller;

import cn.jb.boot.biz.device.service.DeviceTypeInfoService;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoCreateRequest;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoPageRequest;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoUpdateRequest;
import cn.jb.boot.biz.device.vo.response.DeviceTypeInfoInfoResponse;
import cn.jb.boot.biz.device.vo.response.DeviceTypeInfoPageResponse;
import cn.jb.boot.biz.device.vo.response.DeviceTypeListResponse;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.vo.DictDataVo;
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
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 03:22:39
 */
@RestController
@RequestMapping("/api/device/device_type_info")
@Tag(name = "device-type-info-controller", description = "设备类型信息接口")
public class DeviceTypeInfoController {

    @Resource
    private DeviceTypeInfoService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增设备类型信息记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<DeviceTypeInfoCreateRequest> request) {
        DeviceTypeInfoCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除设备类型信息记录")
    public BaseResponse<String> delete(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        service.delete(params.getId());
        return MsgUtil.ok();
    }

    /**
     * 查询详情
     *
     * @param request 主键
     * @return 记录信息
     */
    @PostMapping("/detail")
    @Operation(summary = "查询设备类型信息记录")
    public BaseResponse<DeviceTypeInfoInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        DeviceTypeInfoInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改设备类型信息记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<DeviceTypeInfoUpdateRequest> request) {
        DeviceTypeInfoUpdateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "查询设备类型信息分页列表")
    public BaseResponse<List<DeviceTypeInfoPageResponse>> pageList(@RequestBody @Valid BaseRequest<DeviceTypeInfoPageRequest> request) {
        DeviceTypeInfoPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }

    @PostMapping("/list_all_type")
    @Operation(summary = "所有设备列表")
    public BaseResponse<List<DictListResponse>> listAllType() {
        List<DictListResponse> responses = service.listAllType();
        return MsgUtil.ok(responses);
    }
}

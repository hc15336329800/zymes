package cn.jb.boot.biz.device.controller;

import cn.jb.boot.biz.device.service.DeviceInfoService;
import cn.jb.boot.biz.device.vo.request.DeviceInfoCreateRequest;
import cn.jb.boot.biz.device.vo.request.DeviceInfoPageRequest;
import cn.jb.boot.biz.device.vo.request.DeviceInfoUpdateRequest;
import cn.jb.boot.biz.device.vo.response.DeviceInfoInfoResponse;
import cn.jb.boot.biz.device.vo.response.DeviceInfoPageResponse;
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
import java.util.Map;

/**
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 04:46:16
 */
@RestController
@RequestMapping("/api/device/device_info")
@Tag(name = "device-info-controller", description = "设备信息接口")
public class DeviceInfoController {

    @Resource
    private DeviceInfoService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增设备信息记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<DeviceInfoCreateRequest> request) {
        DeviceInfoCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除设备信息记录")
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
    @Operation(summary = "查询设备信息记录")
    public BaseResponse<DeviceInfoInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        DeviceInfoInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改设备信息记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<DeviceInfoUpdateRequest> request) {
        DeviceInfoUpdateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "查询设备信息分页列表")
    public BaseResponse<List<DeviceInfoPageResponse>> pageList(@RequestBody @Valid BaseRequest<DeviceInfoPageRequest> request) {
        DeviceInfoPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }

    @PostMapping("/device_select")
    @Operation(summary = "查询设备信息记录")
    public BaseResponse<List<DictListResponse>> deviceSelect() {
        List<DictListResponse> entity = service.deviceSelect();
        return MsgUtil.ok(entity);
    }

    @PostMapping("/deviceType_count")
    @Operation(summary = "根据类型查询设备数量")
    public Map<String,Object> deviceTypeCount(@RequestBody Map<String, Object> params) {
        return service.deviceTypeCount();
    }
}

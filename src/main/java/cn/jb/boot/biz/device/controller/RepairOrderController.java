package cn.jb.boot.biz.device.controller;

import cn.jb.boot.biz.device.service.RepairOrderService;
import cn.jb.boot.biz.device.vo.request.RepairOrderCreateRequest;
import cn.jb.boot.biz.device.vo.request.RepairOrderPageRequest;
import cn.jb.boot.biz.device.vo.request.RepairOrderUpdateRequest;
import cn.jb.boot.biz.device.vo.response.RepairOrderInfoResponse;
import cn.jb.boot.biz.device.vo.response.RepairOrderPageResponse;
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
 * @since 2024-01-01 16:15:25
 */
@RestController
@RequestMapping("/api/device/repair_order")
@Tag(name = "repair-order-controller", description = "维修单接口")
public class RepairOrderController {

    @Resource
    private RepairOrderService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增维修单记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<RepairOrderCreateRequest> request) {
        RepairOrderCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除维修单记录")
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
    @Operation(summary = "查询维修单记录")
    public BaseResponse<RepairOrderInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        RepairOrderInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改维修单记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<RepairOrderUpdateRequest> request) {
        RepairOrderUpdateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "查询维修单分页列表")
    public BaseResponse<List<RepairOrderPageResponse>> pageList(@RequestBody @Valid BaseRequest<RepairOrderPageRequest> request) {
        RepairOrderPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }
}

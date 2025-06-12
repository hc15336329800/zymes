package cn.jb.boot.biz.depository.controller;

import cn.jb.boot.biz.depository.service.OutStoreOrderService;
import cn.jb.boot.biz.depository.vo.request.OutStoreOrderCreateRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreOrderPageRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreOrderUpdateRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreStatusRequest;
import cn.jb.boot.biz.depository.vo.response.OutStoreOrderInfoResponse;
import cn.jb.boot.biz.depository.vo.response.OutStoreOrderPageResponse;
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
 * @since 2024-01-12 15:08:36
 */
@RestController
@RequestMapping("/api/depository/out_store_order")
@Tag(name = "out-store-order-controller", description = "出库单表接口")
public class OutStoreOrderController {

    @Resource
    private OutStoreOrderService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增出库单表记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<OutStoreOrderCreateRequest> request) {
        OutStoreOrderCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除出库单表记录")
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
    @Operation(summary = "查询出库单表记录")
    public BaseResponse<OutStoreOrderInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        OutStoreOrderInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改出库单表记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<OutStoreOrderUpdateRequest> request) {
        OutStoreOrderUpdateRequest params = MsgUtil.params(request);
        service.updateInfo(params);
        return MsgUtil.ok();
    }

    @PostMapping("/update_check_status")
    @Operation(summary = "更新审核状态")
    public BaseResponse<String> updateCheckStatus(@RequestBody @Valid BaseRequest<OutStoreStatusRequest> request) {
        service.updateCheckStatus(MsgUtil.params(request));
        return MsgUtil.ok();
    }


    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询出库单表分页列表")
    public BaseResponse<List<OutStoreOrderPageResponse>> pageList(@RequestBody @Valid BaseRequest<OutStoreOrderPageRequest> request) {
        OutStoreOrderPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }
}

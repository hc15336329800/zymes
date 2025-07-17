package cn.jb.boot.biz.sales.controller;

import cn.jb.boot.biz.sales.service.SaleOrderPlaceService;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.PlaceOrderPageReq;
import cn.jb.boot.biz.sales.vo.request.PlaceOrderReq;
import cn.jb.boot.biz.sales.vo.request.PlaceRefuseReq;
import cn.jb.boot.biz.sales.vo.request.SaleOrderIdsReq;
import cn.jb.boot.biz.sales.vo.response.PlaceOrderPageRep;
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
 * @since 2024-01-04 00:13:32
 */
@RestController
@RequestMapping("/api/sales/sale_order_place")
@Tag(name = "下单详情审批", description = "下单详情流水审批接口")
public class SaleOrderPlaceController {

    @Resource
    private SaleOrderPlaceService service;


    @PostMapping("/place_order")
    @Operation(summary = "下生产单")
    public BaseResponse<String> placeOrder(@RequestBody @Valid BaseRequest<PlaceOrderReq> request) {
        PlaceOrderReq params = MsgUtil.params(request);
        service.placeOrder(params);
        return MsgUtil.ok();
    }

    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询下生产单分页列表")
    public BaseResponse<List<PlaceOrderPageRep>> pageList(@RequestBody @Valid BaseRequest<PlaceOrderPageReq> request) {
        return service.pageList(request);
    }

    @PostMapping("/list_details")
    @Operation(summary = "查看详情")
    public BaseResponse<List<PlaceOrderPageRep>> listDetails(@RequestBody @Valid BaseRequest<ComIdsReq> request) {
        List<PlaceOrderPageRep> list = service.listDetails(MsgUtil.params(request));
        return MsgUtil.ok(list);
    }


    /**
     * 审批通过
     *
     * @param request 查询参数
     * @return 返回成功
     */
    @PostMapping("/approval")
    @Operation(summary = "审批通过")
    public BaseResponse<String> approval(@RequestBody @Valid BaseRequest<SaleOrderIdsReq> request) {
        SaleOrderIdsReq params = MsgUtil.params(request);
        service.approval(params.getList());
        return MsgUtil.ok();
    }

    /**
     * 审批拒绝
     *
     * @param request 查询参数
     * @return 返回成功
     */
    @PostMapping("/refuse")
    @Operation(summary = "审批拒绝")
    public BaseResponse<String> refuse(@RequestBody @Valid BaseRequest<PlaceRefuseReq> request) {
        PlaceRefuseReq params = MsgUtil.params(request);
        service.refuse(params);
        return MsgUtil.ok();
    }
}

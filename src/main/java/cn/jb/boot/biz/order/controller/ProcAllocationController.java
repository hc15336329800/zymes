package cn.jb.boot.biz.order.controller;

import cn.jb.boot.biz.order.service.ProcAllocationService;
import cn.jb.boot.biz.order.vo.request.BatchProcAllocReq;
import cn.jb.boot.biz.order.vo.request.DistListRequest;
import cn.jb.boot.biz.order.vo.request.DistOuterReq;
import cn.jb.boot.biz.order.vo.request.OuterPubPageRequest;
import cn.jb.boot.biz.order.vo.request.ProcAllocationPageRequest;
import cn.jb.boot.biz.order.vo.request.ProcAllocationUpdateRequest;
import cn.jb.boot.biz.order.vo.request.SingleProcAllocReq;
import cn.jb.boot.biz.order.vo.response.DistInfoResponse;
import cn.jb.boot.biz.order.vo.response.OuterDistInfoResponse;
import cn.jb.boot.biz.order.vo.response.ProcAllocationInfoResponse;
import cn.jb.boot.biz.order.vo.response.ProcAllocationPageResponse;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
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
 * 工序
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@RestController
@RequestMapping("/api/order/proc_allocation")
@Tag(name = "proc-allocation-controller", description = "工序分配表接口")
public class ProcAllocationController {

    @Resource
    private ProcAllocationService service;


    /**
     *  查询部件的工序列表
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询工序分配表分页列表")
    public BaseResponse<List<ProcAllocationPageResponse>> pageList(@RequestBody @Valid BaseRequest<ProcAllocationPageRequest> request) {
        ProcAllocationPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }

    @PostMapping("/list_by_id")
    @Operation(summary = "查询工序分配表分页列表")
    public BaseResponse<List<ProcAllocationInfoResponse>> listById(@RequestBody @Valid BaseRequest<ComIdsReq> request) {
        ComIdsReq params = MsgUtil.params(request);
        List<ProcAllocationInfoResponse> resp = service.getByIds(params.getIds());
        return MsgUtil.ok(resp);
    }

    @PostMapping("/update_status")
    @Operation(summary = "查询工序分配表分页列表")
    public BaseResponse<String> updateStatus(@RequestBody @Valid BaseRequest<ProcAllocationUpdateRequest> request) {
        service.updateStatus(MsgUtil.params(request));
        return MsgUtil.ok();
    }


//    工序分配列表
    @PostMapping("/dist_list")
    @Operation(summary = "工序分配(列表)")
    public BaseResponse<List<DistInfoResponse>> distList(@RequestBody @Valid BaseRequest<DistListRequest> request) {
        List<DistInfoResponse> distList = service.distList(MsgUtil.params(request));
        return MsgUtil.ok(distList);
    }

    @PostMapping("/outer_dist_list")
    @Operation(summary = "外协发布列表")
    public BaseResponse<List<OuterDistInfoResponse>> outerDistList(@RequestBody @Valid BaseRequest<ComIdsReq> request) {
        List<OuterDistInfoResponse> distList = service.outerDistList(MsgUtil.params(request));
        return MsgUtil.ok(distList);
    }



    // 点击保存工序后，进入工单管理页面
//    工序分配列表页面>>>保存按钮
    @PostMapping("/alloc_proc_v1")
    @Operation(summary = "工序分配")
    public BaseResponse<String> createWorkOrder(@RequestBody @Valid BaseRequest<BatchProcAllocReq> request) {
//        service.createWorkOrder(MsgUtil.params(request));
        service.createWorkOrderNew(MsgUtil.params(request));  // new 新的分开的

        return MsgUtil.ok();
    }

    @PostMapping("/distribution_outer")
    @Operation(summary = "外协分配")
    public BaseResponse<String> distributionOuter(@RequestBody @Valid BaseRequest<DistOuterReq> request) {
        service.distributionOuter(MsgUtil.params(request));
        return MsgUtil.ok();
    }

    @PostMapping("/delete_by_work_id")
    @Operation(summary = "删除工单")
    public BaseResponse<String> deleteByWorkId(@RequestBody @Valid BaseRequest<ComId> request) {
        service.deleteByWorkId(MsgUtil.params(request));
        return MsgUtil.ok();
    }

    @PostMapping("/outer_pub_list")
    @Operation(summary = "外协发布列表")
    public BaseResponse<List<OuterDistInfoResponse>> outerPubList(@RequestBody @Valid BaseRequest<OuterPubPageRequest> request) {
        OuterPubPageRequest params = MsgUtil.params(request);
        return service.outerPubList(request.getPage(), params);
    }


}

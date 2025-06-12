package cn.jb.boot.biz.sales.controller;

import cn.jb.boot.biz.sales.service.BomStoreService;
import cn.jb.boot.biz.sales.vo.request.BomStoreAddReq;
import cn.jb.boot.biz.sales.vo.request.BomStorePageReq;
import cn.jb.boot.biz.sales.vo.request.BomStoreStaReq;
import cn.jb.boot.biz.sales.vo.request.BomStoreUpdateReq;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.StaDetailReq;
import cn.jb.boot.biz.sales.vo.response.BomStoreDetailResp;
import cn.jb.boot.biz.sales.vo.response.BomStoreResp;
import cn.jb.boot.biz.sales.vo.response.BomStoreStaResp;
import cn.jb.boot.biz.sales.vo.response.StaDetailResp;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;

import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 前端控制器
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-12-18 14:27:31
 */
@RestController
@RequestMapping("/api/sales/bom_store")
@Tag(name = "bom-store-controller", description = "出入库流水接口")
public class BomStoreController {

    @Autowired
    private BomStoreService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增出入库流水记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<BomStoreAddReq> request) {
        service.add(MsgUtil.params(request));
        return MsgUtil.ok();
    }

    @PostMapping("/detail")
    @Operation(summary = "新增出入库流水记录")
    public BaseResponse<BomStoreDetailResp> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        BomStoreDetailResp resp = service.detail(MsgUtil.params(request));
        return MsgUtil.ok(resp);
    }


    /**
     * 删除记录
     *
     * @param request 主键
     * @return 是否成功
     */
    @PostMapping("/delete")
    @Operation(summary = "根据主键删除出入库流水记录")
    public BaseResponse<String> delete(@RequestBody @Valid BaseRequest<ComId> request) {
        service.delete(MsgUtil.params(request).getId());
        return MsgUtil.ok();
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改出入库流水记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<BomStoreUpdateReq> request) {
        service.updateStore(MsgUtil.params(request));
        return MsgUtil.ok();
    }


    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询出入库流水分页列表")
    public BaseResponse<List<BomStoreResp>> pageList(@RequestBody @Valid BaseRequest<BomStorePageReq> request) {
        return this.service.pageList(request);
    }

    @PostMapping("/confirm")
    @Operation(summary = "批量确认")
    public BaseResponse<String> confirm(@RequestBody @Valid BaseRequest<ComIdsReq> request) {
        this.service.confirm(request);
        return MsgUtil.ok();
    }

    @PostMapping("/sta")
    @Operation(summary = "出入库统计")
    public BaseResponse<List<BomStoreStaResp>> sta(@RequestBody @Valid BaseRequest<BomStoreStaReq> request) {
        List<BomStoreStaResp> resps = service.sta(MsgUtil.params(request));
        return MsgUtil.ok(resps);
    }

    @PostMapping("/sta_detail")
    @Operation(summary = "出入库统计")
    public BaseResponse<List<StaDetailResp>> staDetail(@RequestBody @Valid BaseRequest<StaDetailReq> request) {
        return service.staDetail(request);
    }

}

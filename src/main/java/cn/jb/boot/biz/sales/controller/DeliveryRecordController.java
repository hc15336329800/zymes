package cn.jb.boot.biz.sales.controller;

import cn.jb.boot.biz.sales.service.DeliveryRecordService;
import cn.jb.boot.biz.sales.vo.request.DeliveryRecordPageReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryRecordStaReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryRecordPageRep;
import cn.jb.boot.biz.sales.vo.response.DeliveryRecordStaRep;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 前端控制器
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-01 12:29:38
 */
@RestController
@RequestMapping("/api/sales/delivery_record")
@Tag(name = "发货统计", description = "发送统计接口")
public class DeliveryRecordController {

    @Autowired
    private DeliveryRecordService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/sta")
    @Operation(summary = "发送统计")
    public BaseResponse<DeliveryRecordStaRep> sta(@RequestBody @Valid BaseRequest<DeliveryRecordStaReq> request) {
        DeliveryRecordStaReq params = MsgUtil.params(request);
        DeliveryRecordStaRep rep = service.sta(params);
        return MsgUtil.ok(rep);
    }

    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "发货明细导出分页列表")
    public BaseResponse<List<DeliveryRecordPageRep>> pageList(@RequestBody @Valid BaseRequest<DeliveryRecordPageReq> request) {
        return service.pageList(request);
    }

    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/export")
    @Operation(summary = "发货明细导出")
    public void export(@RequestBody @Valid BaseRequest<DeliveryRecordPageReq> request, HttpServletResponse response) {
        DeliveryRecordPageReq params = MsgUtil.params(request);
        service.export(params, response);
    }


}

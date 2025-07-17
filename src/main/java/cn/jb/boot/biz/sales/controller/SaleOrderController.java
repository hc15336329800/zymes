package cn.jb.boot.biz.sales.controller;

import cn.jb.boot.biz.sales.service.SaleOrderService;
import cn.jb.boot.biz.sales.vo.request.SaleOrderBatchAddReq;
import cn.jb.boot.biz.sales.vo.request.SaleOrderIdsReq;
import cn.jb.boot.biz.sales.vo.request.SaleOrderPageReq;
import cn.jb.boot.biz.sales.vo.request.SaleOrderUpdateReq;
import cn.jb.boot.biz.sales.vo.response.SaleOrderInfoResponse;
import cn.jb.boot.biz.sales.vo.response.SaleOrderPageRep;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/api/sales/sale_order")
@Tag(name = "销售单接口", description = "销售单接口")
public class SaleOrderController {

    @Resource
    private SaleOrderService service;


    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/batch_add")
    @Operation(summary = "新增销售单记录")
    public BaseResponse<String> batchAdd(@RequestBody @Valid BaseRequest<SaleOrderBatchAddReq> request) {
        SaleOrderBatchAddReq params = MsgUtil.params(request);
        service.batchAdd(params);
        return MsgUtil.ok();
    }

    /**
     * 删除记录
     *
     * @param request 主键
     * @return 是否成功
     */
    @PostMapping("/delete")
    @Operation(summary = "根据主键删除销售单记录 可以批量")
    public BaseResponse<String> delete(@RequestBody @Valid BaseRequest<SaleOrderIdsReq> request) {
        SaleOrderIdsReq params = MsgUtil.params(request);
        service.delete(params.getList());
        return MsgUtil.ok();
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改销售单记录")
    public BaseResponse<SaleOrderPageRep> update(@RequestBody @Valid BaseRequest<SaleOrderUpdateReq> request) {
        SaleOrderUpdateReq params = MsgUtil.params(request);
        SaleOrderPageRep resp = service.update(params);
        return MsgUtil.ok(resp);
    }

    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询销售单分页列表")
    public BaseResponse<List<SaleOrderPageRep>> pageList(@RequestBody @Valid BaseRequest<SaleOrderPageReq> request) {
        return service.pageList(request);
    }

    /**
     * 导入
     *
     * @param multipartFile 查询参数
     * @return 分页记录
     */
    @PostMapping("/import_order")
    @Operation(summary = "导入销售单", description = "form里文件的name为 file")
    public BaseResponse<String> importOrder(@RequestParam("file") MultipartFile multipartFile) {
        service.importOrder(multipartFile);
        return MsgUtil.ok();
    }


    /**
     * 下载导入模板
     *
     * @param response response
     * @return 分页记录
     */
    @PostMapping("/down_temp")
    @Operation(summary = "下载导入模板")
    public void downTemp(HttpServletResponse response) {
        service.downTemp(response);
    }

    /**
     * 下载导入模板
     *
     * @param response response
     * @return 分页记录
     */
    @PostMapping("/export_order")
    @Operation(summary = "导出销售单")
    public void exportOrder(@RequestBody @Valid BaseRequest<SaleOrderIdsReq> request, HttpServletResponse response) {
        SaleOrderIdsReq params = MsgUtil.params(request);
        service.exportOrder(params.getList(), response);
    }

    @PostMapping("/list_sales_order")
    @Operation(summary = "按Id列出销售单")
    public BaseResponse<List<SaleOrderPageRep>> listSalesOrder(@RequestBody @Valid BaseRequest<SaleOrderIdsReq> request) {
        SaleOrderIdsReq params = MsgUtil.params(request);
        List<SaleOrderPageRep> list = service.listSalesOrder(params.getList());
        return MsgUtil.ok(list);
    }

    @PostMapping("/detail")
    @Operation(summary = "详情")
    public BaseResponse<SaleOrderInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        SaleOrderInfoResponse detail = service.detail(MsgUtil.params(request));
        return MsgUtil.ok(detail);
    }
}

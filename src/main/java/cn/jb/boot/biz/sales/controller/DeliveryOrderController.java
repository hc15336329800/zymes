package cn.jb.boot.biz.sales.controller;

import cn.jb.boot.biz.sales.service.DeliveryOrderService;
import cn.jb.boot.biz.sales.vo.request.BatchUpdateReq;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryBatchAddReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryMainPageReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryMainPageResp;
import cn.jb.boot.biz.sales.vo.response.DeliveryMainResp;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/api/sales/delivery_order")
@Tag(name = "发运单", description = "发货申请表接口")
public class DeliveryOrderController {

    @Autowired
    private DeliveryOrderService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/batch_add")
    @Operation(summary = "批量新增发货申请表记录")
    public BaseResponse<Boolean> batchAdd(@RequestBody @Valid BaseRequest<DeliveryBatchAddReq> request) {
        service.batchAdd(MsgUtil.params(request));
        return MsgUtil.ok(true);
    }

    /**
     * 删除记录
     *
     * @param request 主键
     * @return 是否成功
     */
    @PostMapping("/delete")
    @Operation(summary = "根据主键删除发货申请表记录")
    public BaseResponse<Boolean> delete(@RequestBody @Valid BaseRequest<ComId> request) {
        service.delete(MsgUtil.params(request).getId());
        return MsgUtil.ok(true);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改发货申请表记录")
    public BaseResponse<Boolean> update(@RequestBody @Valid BaseRequest<BatchUpdateReq> request) {
        service.batchUpdate(MsgUtil.params(request));
        return MsgUtil.ok(true);
    }


    @PostMapping("/update_count")
    @Operation(summary = "修改发货数量")
    public BaseResponse<Boolean> updateCount(@RequestBody @Valid BaseRequest<BatchUpdateReq> request) {
        service.updateCount(MsgUtil.params(request));
        return MsgUtil.ok(true);
    }

    @PostMapping("/commit")
    @Operation(summary = "提交仓库")
    public BaseResponse<Boolean> commitOrder(@RequestBody @Valid BaseRequest<ComId> request) {
        service.commitOrder(MsgUtil.params(request));
        return MsgUtil.ok(true);
    }

    /**
     * 查询详情
     *
     * @param request 主键
     * @return 记录信息
     */
    @PostMapping("/detail")
    @Operation(summary = "查询发货申请表记录")
    public BaseResponse<DeliveryMainResp> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        return service.detail(MsgUtil.params(request));
    }

    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询发货申请表分页列表")
    public BaseResponse<List<DeliveryMainPageResp>> pageList(@RequestBody @Valid BaseRequest<DeliveryMainPageReq> request) {
        return service.pageList(request);
    }

    /**
     * 查询详情
     *
     * @param request 主键
     * @return 记录信息
     */
    @PostMapping("/confirm")
    @Operation(summary = "批量确认")
    public BaseResponse<Boolean> confirm(@RequestBody @Valid BaseRequest<ComIdsReq> request) {
        service.confirm(MsgUtil.params(request));
        return MsgUtil.ok(true);
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

    @PostMapping("/import_delivery")
    @Operation(summary = "导入发货申请", description = "form里文件的name为 file")
    public BaseResponse<String> importDelivery(@RequestParam("file") MultipartFile multipartFile) {
        service.importDelivery(multipartFile);
        return MsgUtil.ok();
    }


    @PostMapping("/export_order")
    @Operation(summary = "导出发运单")
    public void exportOrder(@RequestBody @Valid BaseRequest<ComIdsReq> request, HttpServletResponse response) {
        service.exportOrder(MsgUtil.params(request), response);
    }


}

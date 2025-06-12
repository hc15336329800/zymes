package cn.jb.boot.biz.item.controller;

import cn.jb.boot.biz.item.service.impl.MesItemStockServiceImpl;
import cn.jb.boot.biz.item.vo.request.BomNoSelectedRequest;
import cn.jb.boot.biz.item.vo.request.BomPageRequest;
import cn.jb.boot.biz.item.vo.request.ItemNoSelectedRequest;
import cn.jb.boot.biz.item.vo.response.ItemSelectedResponse;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.vo.request.MesItemStockCreateRequest;
import cn.jb.boot.biz.item.vo.request.MesItemStockPageRequest;
import cn.jb.boot.biz.item.vo.request.MesItemStockUpdateRequest;
import cn.jb.boot.biz.item.vo.response.MesItemStockInfoResponse;
import cn.jb.boot.biz.item.vo.response.MesItemStockPageResponse;
import cn.jb.boot.framework.common.core.domain.AjaxResult;
import cn.jb.boot.framework.common.exception.ServiceException;
import cn.jb.boot.util.EasyExcelUtil;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;


/**
 * 前端控制器
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-08-03 13:24:13
 */
@RestController
@RequestMapping("/api/item/mes_item_stock")
@Tag(name = "产品库存表接口mes-item-stock-controller", description = "产品库存表接口")
public class MesItemStockController {

    @Resource
    private MesItemStockService service;

////////////////////////////////////////////////////////////////////////////新街口//////////////////////////////////////////////////////////////////////////////////

    @Resource
    private MesItemStockServiceImpl stockService;
    /**
     * 新接口：导入物料 + 同步更新库存
     */
    @PostMapping("/uploadNew")
    public AjaxResult uploadNew(HttpServletRequest request) {

        try {
            stockService.uploadNew(request);
            return AjaxResult.success("物料导入并初始化库存成功");
        } catch (ServiceException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
//            log.error("物料导入失败", e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增产品库存表记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<MesItemStockCreateRequest> request) {
        MesItemStockCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除产品库存表记录")
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
    @Operation(summary = "查询产品库存表记录")
    public BaseResponse<MesItemStockInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        MesItemStockInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改产品库存表记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<MesItemStockUpdateRequest> request) {
        MesItemStockUpdateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "查询产品库存表分页列表")
    public BaseResponse<List<MesItemStockPageResponse>> pageList(@RequestBody @Valid BaseRequest<MesItemStockPageRequest> request) {
        MesItemStockPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }

    @PostMapping("/down_temp")
    @Operation(summary = "下载产品模板")
    public void downUseTemp(HttpServletResponse response) {
        String fileName = "mes_item_stock.xlsx";
        EasyExcelUtil.downloadExcelTemp(response, fileName);
    }


//    导入物料
    @PostMapping("/upload")
    @Operation(summary = "导入产品信息")
    public BaseResponse<String> upload(HttpServletRequest request) {
        service.upload(request);
        return MsgUtil.ok();
    }

    @PostMapping("/item_no_selected")
    @Operation(summary = "产品编码下拉框")
    public BaseResponse<List<ItemSelectedResponse>> itemNoSelected(@RequestBody @Valid BaseRequest<ItemNoSelectedRequest> request) {
        List<ItemSelectedResponse> entity = service.itemNoSelected(MsgUtil.params(request));
        return MsgUtil.ok(entity);
    }

    @PostMapping("/list_bom_by_no")
    @Operation(summary = "bom列表")
    public BaseResponse<List<ItemSelectedResponse>> listBomByNo(@RequestBody @Valid BaseRequest<BomNoSelectedRequest> request) {
        List<ItemSelectedResponse> list = service.listBomByNo(MsgUtil.params(request));
        return MsgUtil.ok(list);
    }

    @PostMapping("/bom_page_list")
    @Operation(summary = "查询产品库存表分页列表")
    public BaseResponse<List<MesItemStockPageResponse>> bomPageList(@RequestBody @Valid BaseRequest<BomPageRequest> request) {
        BomPageRequest params = MsgUtil.params(request);
        return service.bomPageList(request.getPage(), params);
    }


}

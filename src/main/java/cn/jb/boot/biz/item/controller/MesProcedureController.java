package cn.jb.boot.biz.item.controller;

import cn.jb.boot.biz.item.dto.MesProcedureImportResult;
import cn.jb.boot.biz.item.vo.request.ItemProcedureRequest;
import cn.jb.boot.biz.item.vo.request.MesProcedureCreateRequest;
import cn.jb.boot.biz.item.vo.request.MesProcedurePageRequest;
import cn.jb.boot.biz.item.vo.request.MesProcedureUpdateRequest;
import cn.jb.boot.biz.item.vo.request.ShortCodeReq;
import cn.jb.boot.biz.item.vo.response.ItemProcedureResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedureHeaderResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedurePageResponse;
import cn.jb.boot.biz.item.vo.response.ProcListResp;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.biz.item.service.MesProcedureService;
import cn.jb.boot.biz.item.vo.req.ItemIdReq;
import cn.jb.boot.biz.item.vo.request.ItemNoRequest;
import cn.jb.boot.biz.item.vo.response.MesProcedureInfoResponse;
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

/**
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
@RestController
@RequestMapping("/api/item/mes_procedure")
@Tag(name = "mes-procedure-controller", description = "工序表接口")
public class MesProcedureController {

    @Resource
    private MesProcedureService service;


    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增工序表记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<MesProcedureCreateRequest> request) {
        MesProcedureCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除工序表记录")
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
    @Operation(summary = "查询工序表记录")
    public BaseResponse<MesProcedureInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        MesProcedureInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改工序表记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<MesProcedureUpdateRequest> request) {
        MesProcedureUpdateRequest params = MsgUtil.params(request);
        service.updateInfo(params);
        return MsgUtil.ok();
    }

    @PostMapping("/procedure_header")
    @Operation(summary = "工序详情头部信息")
    public BaseResponse<MesProcedureHeaderResponse> headerInfo(@RequestBody @Valid BaseRequest<ItemNoRequest> request) {
        MesProcedureHeaderResponse resp = service.headerInfo(MsgUtil.params(request));
        return MsgUtil.ok(resp);
    }


    @PostMapping("/list_by_item")
    @Operation(summary = "BOM详情页查询工序信息")
    public BaseResponse<List<MesProcedureInfoResponse>> listByItem(@RequestBody @Valid BaseRequest<ItemNoRequest> request) {
        List<MesProcedureInfoResponse> resp = service.listByItem(MsgUtil.params(request));
        return MsgUtil.ok(resp);
    }

    @PostMapping("/down_proc_temp")
    @Operation(summary = "下载工序导入模板")
    public void downProcTemp(HttpServletResponse response) {
        String fileName = "mes_procedure.xlsx";
        EasyExcelUtil.downloadExcelTemp(response, fileName);
    }

    @PostMapping("/export_bom_proc")
    @Operation(summary = "bom工序下载")
    public void exportBomProc(HttpServletResponse response, @RequestBody @Valid BaseRequest<ItemIdReq> request) {
        service.export(response, MsgUtil.params(request).getId());
    }

//    @PostMapping("/upload")
//    @Operation(summary = "工序导入")
//    public BaseResponse<String> upload(HttpServletRequest request) {
//        service.upload(request);
//        return MsgUtil.ok();
//    }


    @PostMapping("/upload")
    @Operation(summary = "工序导入")
    public BaseResponse<MesProcedureImportResult> upload(HttpServletRequest request) {
        MesProcedureImportResult result = service.upload(request);
        return BaseResponse.ok(result);
    }


    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询工序表分页列表")
    public BaseResponse<List<MesProcedurePageResponse>> pageList(@RequestBody @Valid BaseRequest<MesProcedurePageRequest> request) {
        MesProcedurePageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }

    @PostMapping("/list_name_by_short")
    @Operation(summary = "工序简码查询工序")
    public BaseResponse<List<ProcListResp>> listNameByShortCode(@RequestBody @Valid BaseRequest<ShortCodeReq> request) {
        List<ProcListResp> list = service.listNameByShortCode(MsgUtil.params(request));
        return MsgUtil.ok(list);
    }

    @PostMapping("/list_procedure_by_item")
    @Operation(summary = "通过产品编码查询工序")
    public BaseResponse<List<ItemProcedureResponse>> listProcedureByItem(@RequestBody @Valid BaseRequest<ItemProcedureRequest> request) {
        List<ItemProcedureResponse> list = service.listProcedureByItem(MsgUtil.params(request));
        return MsgUtil.ok(list);
    }

}

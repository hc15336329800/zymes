package cn.jb.boot.biz.item.controller;

import cn.jb.boot.biz.item.mapper.BomUsedMapper;
import cn.jb.boot.biz.item.service.impl.BomTreeServiceImpl;
import cn.jb.boot.biz.item.service.impl.ItemUseUploadServiceImpl;
import cn.jb.boot.biz.item.task.GetErpDataJob;
import cn.jb.boot.biz.item.task.GetMesDataJob;
import cn.jb.boot.biz.item.vo.request.MesItemUseUpdateRequest;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.biz.item.service.MesItemUseService;
import cn.jb.boot.biz.item.vo.request.ItemNoRequest;
import cn.jb.boot.biz.item.vo.request.MesItemUseCreateRequest;
import cn.jb.boot.biz.item.vo.response.ItemResp;
import cn.jb.boot.biz.item.vo.response.UseItemTreeResp;
import cn.jb.boot.biz.item.vo.response.MesItemUseInfoResponse;
import cn.jb.boot.framework.common.core.domain.AjaxResult;
import cn.jb.boot.framework.common.exception.ServiceException;
import cn.jb.boot.util.EasyExcelUtil;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
@RestController
@RequestMapping("/api/item/mes_item_use")
@Tag(name = "mes-item-use-controller", description = "产品用料表接口")
public class MesItemUseController {

    @Resource
    private MesItemUseService service;

    @Resource
    private GetMesDataJob getMesDataJob; // 注入内部同步任务类

    @Resource
    private GetErpDataJob getErpDataJob; // 注入外部同步任务类


    ////////////////////////////////////////////更新测试  手动同步接口   先注释  后面要用/////////////////////////////////////////////


//    /**
//     *  内同步接口  || 只测试内同步（bom和工序）（按ITEM）
//     */
//    @PostMapping("/inner_sync_bom_item")
//    @Operation(summary = "内部同步BOM树")
//    public AjaxResult innerSyncBomItem (@RequestBody(required = false) Map<String, Object> params) {
//        try {
//            String ItemNo = Optional.ofNullable(params)
//                    .map(p -> (String)p.get("ItemNo"))
//                    .orElse(null);
//
//            String bomNo = Optional.ofNullable(params)
//                    .map(p -> (String)p.get("bomNo"))
//                    .orElse(null);
//
//            getErpDataJob.syncErpToMesBomItemNo(ItemNo,bomNo);
//
//            return AjaxResult.success("外部同步ERP数据到MES完成");
//        } catch (Exception e) {
//            return AjaxResult.error("外部同步ERP数据到MES失败: " + e.getMessage());
//        }
//    }
//
    /**
     *  内同步接口  || 只测试内同步（bom和工序）（按时间） 全量 不再按时间
     */
    @PostMapping("/inner_sync_bom")
    @Operation(summary = "内部同步BOM树")
    public AjaxResult innerSyncBom (@RequestBody(required = false) Map<String, Object> params) {
        try {
            String syncTime = Optional.ofNullable(params)
                    .map(p -> (String)p.get("syncTime"))
                    .orElse(null);

            getErpDataJob.syncErpToMesBom();
            return AjaxResult.success("外部同步ERP数据到MES完成");
        } catch (Exception e) {
            return AjaxResult.error("外部同步ERP数据到MES失败: " + e.getMessage());
        }
    }
//
//    /**
//     * 外同步接口  || 只测试外同步（  原料物料+bom物料、 bom临时依赖、  工序表）
//     */
//    @PostMapping("/sync_erp_to_mes")
//    @Operation(summary = "外部同步ERP数据到MES")
//    public AjaxResult syncErpToMes(@RequestBody(required = false) Map<String, Object> params) {
//        try {
//            String syncTime = Optional.ofNullable(params)
//                    .map(p -> (String)p.get("syncTime"))
//                    .orElse(null);
//
//            getErpDataJob.syncErpToMes(syncTime);
//            return AjaxResult.success("外部同步ERP数据到MES完成");
//        } catch (Exception e) {
//            return AjaxResult.error("外部同步ERP数据到MES失败: " + e.getMessage());
//        }
//    }



    ////////////////////////////////////////////重写上传逻辑/////////////////////////////////////////////
    @Resource   //字段注入
    private   ItemUseUploadServiceImpl uploadService;
    @Resource  //字段注入
    private   BomTreeServiceImpl bomTreeService;



    /**
     * 上传用料表
     * 一步到位：校验→写 mes_item_use →写 t_bom_used
     * */
    @PostMapping("/uploadNew")
    public AjaxResult uploadNew(@RequestPart("file") MultipartFile file) {
        try {
            uploadService.uploadAndRebuild(file);
            return AjaxResult.success("导入并生成 BOM 树成功");
        } catch (ServiceException e) {
            // 业务校验失败，友好提示
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            // 系统异常，记录日志后返回通用提示
//            log.error("用料导入失败", e);
            return AjaxResult.error("系统繁忙，请稍后再试");
        }
    }


//    bom树构造器  （  将 List<BomUsed> list  + String root   构建为前端树结构）
    @PostMapping("/item_use_tree_new")
    @Operation(summary = "查看用料树（优化版）")
    public BaseResponse<UseItemTreeResp> itemUseTreeNew(@RequestBody @Valid BaseRequest<ItemNoRequest> request) {
        UseItemTreeResp tree = service.itemUseTreeNew(MsgUtil.params(request));
        return MsgUtil.ok(tree);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////


//原始
    @PostMapping("/item_use_tree")
    @Operation(summary = "查看用料树")
    public BaseResponse<UseItemTreeResp> itemUseTree(@RequestBody @Valid BaseRequest<ItemNoRequest> request) {
        UseItemTreeResp entity = service.itemUseTree(MsgUtil.params(request));
        return MsgUtil.ok(entity);
    }



    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增产品用料表记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<MesItemUseCreateRequest> request) {
        MesItemUseCreateRequest params = MsgUtil.params(request);
        service.saveOrUpdateItem(params);
        return MsgUtil.ok();
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改产品用料表记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<MesItemUseUpdateRequest> request) {
        MesItemUseUpdateRequest params = MsgUtil.params(request);
        service.saveOrUpdateItem(params);
        return MsgUtil.ok();
    }

    /**
     * 删除记录
     *
     * @param request 主键
     * @return 是否成功
     */
    @PostMapping("/delete")
    @Operation(summary = "根据主键删除产品用料表记录")
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
    @Operation(summary = "查询产品用料表记录")
    public BaseResponse<MesItemUseInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        MesItemUseInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    @PostMapping("/down_use_temp")
    @Operation(summary = "下载用料导入模板")
    public void downUseTemp(HttpServletResponse response) {
        String fileName = "item_use.xlsx";
        EasyExcelUtil.downloadExcelTemp(response, fileName);
    }

    @PostMapping("/export_bom_use")
    @Operation(summary = "bom用料导出")
    public void export(HttpServletResponse response, @RequestBody @Valid BaseRequest<ComId> request) {
        service.export(response, MsgUtil.params(request).getId());
    }


    //    mes_item_stock  仅被 SELECT 用来校验图纸号和物料编码。
    //
    //    mes_item_use 先整表级别 Delete（限于导入涉及的成品），再批量 Insert 新用料明细。
    //
    //    t_bom_used 在事务提交后异步刷新：对受影响的每一个编码先 Delete 再 Insert，重建整棵 BOM 依赖树。
    @PostMapping("/upload")
    @Operation(summary = "工序用料导入")
    public BaseResponse<String> upload(HttpServletRequest request) {
        service.upload(request);
        return MsgUtil.ok();
    }


    @PostMapping("/item_list")
    @Operation(summary = "查询产品列表")
    public BaseResponse<List<ItemResp>> itemList(@RequestBody @Valid BaseRequest<ItemNoRequest> request) {
        List<ItemResp> list = service.itemList(MsgUtil.params(request));
        return MsgUtil.ok(list);
    }


}

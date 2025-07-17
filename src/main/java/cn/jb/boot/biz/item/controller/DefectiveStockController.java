package cn.jb.boot.biz.item.controller;

import cn.jb.boot.biz.item.service.DefectiveStockService;
import cn.jb.boot.biz.item.vo.request.DefectiveStockCreateRequest;
import cn.jb.boot.biz.item.vo.request.DefectiveStockPageRequest;
import cn.jb.boot.biz.item.vo.request.DefectiveStockUpdateRequest;
import cn.jb.boot.biz.item.vo.response.DefectiveStockInfoResponse;
import cn.jb.boot.biz.item.vo.response.DefectiveStockPageResponse;

import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 22:01:35
 */
@RestController
@RequestMapping("/api/item/defective_stock")
@Tag(name = "defective-stock-controller", description = "不良品库存接口")
public class DefectiveStockController {

    @Resource
    private DefectiveStockService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增不良品库存记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<DefectiveStockCreateRequest> request) {
        DefectiveStockCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除不良品库存记录")
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
    @Operation(summary = "查询不良品库存记录")
    public BaseResponse<DefectiveStockInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        DefectiveStockInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改不良品库存记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<DefectiveStockUpdateRequest> request) {
        DefectiveStockUpdateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "查询不良品库存分页列表")
    public BaseResponse<List<DefectiveStockPageResponse>> pageList(@RequestBody @Valid BaseRequest<DefectiveStockPageRequest> request) {
        DefectiveStockPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }
}

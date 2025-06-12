package cn.jb.boot.biz.item.controller;

import cn.jb.boot.biz.item.service.WarehouseInfoService;
import cn.jb.boot.biz.item.vo.request.WarehouseInfoCreateRequest;
import cn.jb.boot.biz.item.vo.request.WarehouseInfoPageRequest;
import cn.jb.boot.biz.item.vo.request.WarehouseInfoUpdateRequest;
import cn.jb.boot.biz.item.vo.response.WarehouseInfoInfoResponse;
import cn.jb.boot.biz.item.vo.response.WarehouseInfoPageResponse;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
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
 * @since 2024-01-02 00:44:38
 */
@RestController
@RequestMapping("/api/item/warehouse_info")
@Tag(name = "warehouse-info-controller", description = "库位信息接口")
public class WarehouseInfoController {

    @Resource
    private WarehouseInfoService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增库位信息记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<WarehouseInfoCreateRequest> request) {
        WarehouseInfoCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除库位信息记录")
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
    @Operation(summary = "查询库位信息记录")
    public BaseResponse<WarehouseInfoInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        WarehouseInfoInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改库位信息记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<WarehouseInfoUpdateRequest> request) {
        WarehouseInfoUpdateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "查询库位信息分页列表")
    public BaseResponse<List<WarehouseInfoPageResponse>> pageList(@RequestBody @Valid BaseRequest<WarehouseInfoPageRequest> request) {
        WarehouseInfoPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }

    @PostMapping("/selected")
    @Operation(summary = "查询库位信息下拉列表")
    public BaseResponse<List<DictListResponse>> selected() {
        List<DictListResponse> entity = service.selected();
        return MsgUtil.ok(entity);
    }
}

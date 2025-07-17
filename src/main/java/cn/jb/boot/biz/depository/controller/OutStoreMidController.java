package cn.jb.boot.biz.depository.controller;

import cn.jb.boot.biz.depository.service.OutStoreMidService;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidCreateRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidPageRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidUpdateRequest;
import cn.jb.boot.biz.depository.vo.response.OutStoreMidInfoResponse;
import cn.jb.boot.biz.depository.vo.response.OutStoreMidPageResponse;
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
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@RestController
@RequestMapping("/api/depository/out_store_mid")
@Tag(name = "out-store-mid-controller", description = "中间件使用表接口")
public class OutStoreMidController {

    @Resource
    private OutStoreMidService service;

    /**
     * 新增记录
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @PostMapping("/add")
    @Operation(summary = "新增中间件使用表记录")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<OutStoreMidCreateRequest> request) {
        OutStoreMidCreateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "根据主键删除中间件使用表记录")
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
    @Operation(summary = "查询中间件使用表记录")
    public BaseResponse<OutStoreMidInfoResponse> detail(@RequestBody @Valid BaseRequest<ComId> request) {
        ComId params = MsgUtil.params(request);
        OutStoreMidInfoResponse entity = service.getInfoById(params.getId());
        return MsgUtil.ok(entity);
    }

    /**
     * 更新记录
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改中间件使用表记录")
    public BaseResponse<String> update(@RequestBody @Valid BaseRequest<OutStoreMidUpdateRequest> request) {
        OutStoreMidUpdateRequest params = MsgUtil.params(request);
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
    @Operation(summary = "查询中间件使用表分页列表")
    public BaseResponse<List<OutStoreMidPageResponse>> pageList(@RequestBody @Valid BaseRequest<OutStoreMidPageRequest> request) {
        OutStoreMidPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }
}

package cn.jb.boot.biz.item.controller;

import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.biz.item.vo.request.MidItemStockPageRequest;
import cn.jb.boot.biz.item.vo.response.MidItemStockPageResponse;
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
 * @since 2024-01-03 17:28:52
 */
@RestController
@RequestMapping("/api/item/mid_item_stock")
@Tag(name = "mid-item-stock-controller", description = "mes中间件库存表接口")
public class MidItemStockController {

    @Resource
    private MidItemStockService service;


    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询mes中间件库存表分页列表")
    public BaseResponse<List<MidItemStockPageResponse>> pageList(@RequestBody @Valid BaseRequest<MidItemStockPageRequest> request) {
        MidItemStockPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }
}

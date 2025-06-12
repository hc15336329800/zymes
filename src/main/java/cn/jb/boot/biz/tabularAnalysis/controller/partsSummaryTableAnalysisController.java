package cn.jb.boot.biz.tabularAnalysis.controller;

import cn.jb.boot.biz.depository.vo.response.OutStoreMidPageResponse;
import cn.jb.boot.biz.item.vo.request.MesItemStockPageRequest;
import cn.jb.boot.biz.item.vo.response.ItemSelectedResponse;
import cn.jb.boot.biz.tabularAnalysis.entity.Part;
import cn.jb.boot.biz.tabularAnalysis.service.PartsSummaryTableAnalysisService;
import cn.jb.boot.biz.tabularAnalysis.vo.request.PartRequest;
import cn.jb.boot.biz.tabularAnalysis.vo.response.PartResponse;
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
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 前端控制器
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-11 20:04:08
 */
@RestController
@RequestMapping("/api/tabularAnalysis/parts-summary")
@Tag(name = "parts-summary-controller", description = "托盘信息")
public class partsSummaryTableAnalysisController {

    @Resource
    PartsSummaryTableAnalysisService sevice;

    @PostMapping("/upload")
    @Operation(summary = "导入产品信息")
    public BaseResponse<String> upload(HttpServletRequest request) {
        sevice.upload(request);
        return MsgUtil.ok();
    }

    /**
     * 查看历史记录
     * @param request
     * @return
     */
    @PostMapping("/page_list")
    public BaseResponse<List<PartResponse>> getListPage(@RequestBody @Valid BaseRequest<PartRequest> request){

        PartRequest params = MsgUtil.params(request);
        return sevice.getListPage(request.getPage(), params);

//        return sevice.getListPage(params);
    }
    @PostMapping("/get_list")
    public BaseResponse<List<Part>> getList(){
        List<Part> list = sevice.getList();
        return MsgUtil.ok(list);

    }

    @PostMapping("/get_map")
    public BaseResponse<Map<String, Object>> getMap(){
        Map<String, Object>  list = sevice.getMap();
        return MsgUtil.ok(list);

    }


    @PostMapping("/clean_list")
    public void cleanList(){
        sevice.cleanList();
    }


    /**
     * 添加到缓存
     * @param parts
     * @return
     */
    @PostMapping("/add_in_cache")
    public BaseResponse<String> addInCache(@RequestBody @Valid List<Part> parts){
//        sevice.addInCache(parts);
        return MsgUtil.ok();

    }

    @PostMapping("/add_in_bulk")
    public BaseResponse<String> installs(@RequestBody @Valid List<Part> parts){
        sevice.install(parts);
        return MsgUtil.ok();

    }

    /**
     * 数据筛选（工件总数  当天总数）
     */
    @PostMapping("/statistics")
    public BaseResponse<Map<String, Object>> statistics(){
        return MsgUtil.ok(sevice.statistics());
    }

    /**
     * 切割列表
     */
    @PostMapping("/DataDevicePageList")
    public BaseResponse<List<Map<String, Object>>> DataDevicePageList(){
        return MsgUtil.ok(sevice.getProcTodayDatas());
    }

}

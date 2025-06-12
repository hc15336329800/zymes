package cn.jb.boot.biz.tabularAnalysis.service;

import cn.jb.boot.biz.item.vo.request.DefectiveStockPageRequest;
import cn.jb.boot.biz.item.vo.response.DefectiveStockPageResponse;
import cn.jb.boot.biz.tabularAnalysis.entity.Part;
import cn.jb.boot.biz.tabularAnalysis.vo.request.PartRequest;
import cn.jb.boot.biz.tabularAnalysis.vo.response.PartResponse;
import cn.jb.boot.biz.tray.entity.TrayManageInfo;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 点检信息 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
public interface PartsSummaryTableAnalysisService extends IService<Part> {

    void upload(HttpServletRequest request);
    BaseResponse<List<PartResponse>> getListPage(Paging page, PartRequest params);

    List<Part> getList();
    Map<String, Object> getMap();

    void cleanList();

    Map<String, Object> statistics();

    int install(List<Part> parts);
//    List<Part> addInCache(List<Part> parts);

    List<Map<String, Object>> getProcTodayDatas();
}

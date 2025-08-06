package cn.jb.boot.biz.work.service;

import cn.hutool.poi.excel.ExcelWriter;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryMainPageReq;
import cn.jb.boot.biz.work.dto.WorkerReportSalarySummaryDTO;
import cn.jb.boot.biz.work.entity.WorkerReportDtl;
import cn.jb.boot.biz.work.vo.request.WorkerReportDetailPageRequest;
import cn.jb.boot.biz.work.vo.request.WorkerReportDtlPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkerReportDtlPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 工人报工明细 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-11 15:32:29
 */
public interface WorkerReportDtlService extends IService<WorkerReportDtl> {



  /** 导出工人工资汇总（带时间范围参数） */
  void downloadSalarySummary(WorkerReportDetailPageRequest params, HttpServletResponse response);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<WorkerReportDtlPageResponse>> pageInfo(Paging page, WorkerReportDtlPageRequest params);

    BaseResponse<List<WorkerReportDtlPageResponse>> detailPageList(Paging page, WorkerReportDetailPageRequest params);



    void exportOrder(WorkerReportDtlPageRequest params, HttpServletResponse response);
}

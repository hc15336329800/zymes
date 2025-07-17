package cn.jb.boot.biz.work.service;

import cn.jb.boot.biz.work.entity.WorkReport;
import cn.jb.boot.biz.work.vo.request.ReportUpdateStatusRequest;
import cn.jb.boot.biz.work.vo.request.WorkReportPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkReportPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 工单报工表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
public interface WorkReportService extends IService<WorkReport> {


    /**
     * 报工审批列表
     *此处查询涉及到5张表：t_work_report、t_work_order、t_group_info、mes_item_stock、t_order_dtl
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<WorkReportPageResponse>> pageInfo(Paging page, WorkReportPageRequest params);

    void updateStatus(ReportUpdateStatusRequest params);

    void updateVerifyStatus(ReportUpdateStatusRequest params);
}

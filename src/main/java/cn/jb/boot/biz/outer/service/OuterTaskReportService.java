package cn.jb.boot.biz.outer.service;

import cn.jb.boot.biz.outer.entity.OuterTaskReport;
import cn.jb.boot.biz.outer.vo.request.OuterTaskReportCreateRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskReportPageRequest;
import cn.jb.boot.biz.outer.vo.request.OuterTaskReportUpdateRequest;
import cn.jb.boot.biz.outer.vo.response.OuterTaskReportInfoResponse;
import cn.jb.boot.biz.outer.vo.response.OuterTaskReportPageResponse;
import cn.jb.boot.biz.work.vo.request.ReportUpdateStatusRequest;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 外协任务报工 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
public interface OuterTaskReportService extends IService<OuterTaskReport> {

    /**
     * 新增外协任务报工
     *
     * @param params 外协任务报工
     */
    void createInfo(OuterTaskReportCreateRequest params);


    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<OuterTaskReportPageResponse>> pageInfo(Paging page, OuterTaskReportPageRequest params);

    void updateStatus(ReportUpdateStatusRequest params);

}

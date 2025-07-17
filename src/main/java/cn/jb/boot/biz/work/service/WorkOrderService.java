package cn.jb.boot.biz.work.service;

import cn.jb.boot.biz.work.entity.WorkOrder;
import cn.jb.boot.biz.work.vo.request.*;
import cn.jb.boot.biz.work.vo.response.WorkOrderPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.common.core.domain.AjaxResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 工单表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 21:52:39
 */
public interface WorkOrderService extends IService<WorkOrder> {


    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<WorkOrderPageResponse>> pageInfo(Paging page, WorkOrderPageRequest params);

    /**
     * 工单下达
     *
     * @param params
     */
    void addAssign(WorkAssignCreateRequest params);

    /**
     * 所有工单下达
     *
     */
//    void addAllAssign(List<String> ids);



//   批量下达
    void addAllAssign(List<WorkAssignCreateRequest> list);


    /**
     * 工单报工
     *
     * @param params
     */
    void addReport(WorkReportCreateRequest params);


    /**
     * 批量报工处理
     * @param requestList 报工请求列表
     * @return AjaxResult 包含成功数量和失败工单号
     */
    AjaxResult addReportAll(List<WorkReportCreateRequest> requestList);

    /**
     * 工单报工
     *
     * @param params
     */
    void addAllReport(WorkAllReportCreateRequest params);

    /**
     * 新增确认完成的物料与件数
     *
     * @param params
     */
    void saveItemand(Map<String, Object> params);

    /**
     * 根据设备和物料获取加工件数
     *
     * @param params
     */
    String getRealnumber(Map<String, Object> params);
    /**
     * 查询激光切割机数据
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<WorkOrderPageResponse>> orderPageInfo(Paging page, WorkOrderRequest params);
}

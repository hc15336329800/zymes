package cn.jb.boot.biz.order.service;

import cn.jb.boot.biz.order.entity.ProcAllocation;
import cn.jb.boot.biz.order.vo.request.BatchProcAllocReq;
import cn.jb.boot.biz.order.vo.request.DistListRequest;
import cn.jb.boot.biz.order.vo.request.DistOuterReq;
import cn.jb.boot.biz.order.vo.request.OuterPubPageRequest;
import cn.jb.boot.biz.order.vo.request.ProcAllocationPageRequest;
import cn.jb.boot.biz.order.vo.request.ProcAllocationUpdateRequest;
import cn.jb.boot.biz.order.vo.request.SingleProcAllocReq;
import cn.jb.boot.biz.order.vo.response.DistInfoResponse;
import cn.jb.boot.biz.order.vo.response.OuterDistInfoResponse;
import cn.jb.boot.biz.order.vo.response.ProcAllocationInfoResponse;
import cn.jb.boot.biz.order.vo.response.ProcAllocationPageResponse;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 工序分配表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
public interface ProcAllocationService extends IService<ProcAllocation> {


    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<ProcAllocationPageResponse>> pageInfo(Paging page, ProcAllocationPageRequest params);

    List<ProcAllocationInfoResponse> getByIds(List<String> ids);

    void updateStatus(ProcAllocationUpdateRequest params);

    List<DistInfoResponse> distList(DistListRequest params);

    void createWorkOrder(BatchProcAllocReq params);

    void deleteByWorkId(ComId params);

    void distributionOuter(DistOuterReq params);

    List<OuterDistInfoResponse> outerDistList(ComIdsReq params);


    BaseResponse<List<OuterDistInfoResponse>> outerPubList(Paging page, OuterPubPageRequest params);
}

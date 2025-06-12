package cn.jb.boot.biz.order.service;

import cn.jb.boot.biz.order.entity.OrderDtl;
import cn.jb.boot.biz.order.vo.request.OrderAllDtlUpdateStatusRequest;
import cn.jb.boot.biz.order.vo.request.OrderDtlPageRequest;
import cn.jb.boot.biz.order.vo.request.OrderDtlUpdateStatusRequest;
import cn.jb.boot.biz.order.vo.response.OrderDtlPageResponse;
import cn.jb.boot.biz.order.vo.response.WarningResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 订单明细表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
public interface OrderDtlService extends IService<OrderDtl> {


    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<OrderDtlPageResponse>> pageInfo(Paging page, OrderDtlPageRequest params);

    List<OrderDtl> selectCurrentMonth(List<String> bomItemNos);

    void updateStatus(OrderDtlUpdateStatusRequest params);

    void updateAllStatus(OrderAllDtlUpdateStatusRequest params);

    String getProcTodayDatas(Map<String, Object> params);

    BaseResponse<List<WarningResponse>> getWarning();
}

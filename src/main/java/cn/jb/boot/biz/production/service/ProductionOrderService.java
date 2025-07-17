package cn.jb.boot.biz.production.service;

import cn.jb.boot.biz.order.entity.OrderDtl;
import cn.jb.boot.biz.order.entity.ProcAllocation;
import cn.jb.boot.biz.production.entity.ProductionOrder;
import cn.jb.boot.biz.production.vo.req.ProductionOrderPageRequest;
import cn.jb.boot.biz.production.vo.resp.ProductionOrderPageResponse;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 生产任务单 服务类
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-01-15 21:08:58
 */
public interface ProductionOrderService extends IService<ProductionOrder> {


    void delete(List<String> ids);

    void startSchedule(List<String> ids);

    void startSchedule02(String productionOrderId);

    /**
     * BOM拆解下达明细（工序版） 测试1·
       */
    void startSchedule01(List<String> ids);



    BaseResponse<List<ProductionOrderPageResponse>> pageList(BaseRequest<ProductionOrderPageRequest> request);

    void saveProcAndDtl(Map<String, OrderDtl> dtlMap, Map<String, List<ProcAllocation>> paMap,
                        List<ProductionOrder> rollOrders);



}

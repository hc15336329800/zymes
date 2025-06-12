package cn.jb.boot.biz.depository.service;

import cn.jb.boot.biz.depository.entity.OutStoreOrder;
import cn.jb.boot.biz.depository.vo.request.OutStoreOrderCreateRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreOrderPageRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreOrderUpdateRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreStatusRequest;
import cn.jb.boot.biz.depository.vo.response.OutStoreOrderInfoResponse;
import cn.jb.boot.biz.depository.vo.response.OutStoreOrderPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 出库单表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
public interface OutStoreOrderService extends IService<OutStoreOrder> {

    /**
     * 新增出库单表
     *
     * @param params 出库单表
     */
    void createInfo(OutStoreOrderCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    OutStoreOrderInfoResponse getInfoById(String id);

    /**
     * 修改出库单表
     *
     * @param params 出库单表
     */
    void updateInfo(OutStoreOrderUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<OutStoreOrderPageResponse>> pageInfo(Paging page, OutStoreOrderPageRequest params);

    void updateCheckStatus(OutStoreStatusRequest params);
}

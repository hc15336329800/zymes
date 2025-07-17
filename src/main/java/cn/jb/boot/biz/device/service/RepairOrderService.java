package cn.jb.boot.biz.device.service;

import cn.jb.boot.biz.device.entity.RepairOrder;
import cn.jb.boot.biz.device.vo.request.RepairOrderCreateRequest;
import cn.jb.boot.biz.device.vo.request.RepairOrderPageRequest;
import cn.jb.boot.biz.device.vo.request.RepairOrderUpdateRequest;
import cn.jb.boot.biz.device.vo.response.RepairOrderInfoResponse;
import cn.jb.boot.biz.device.vo.response.RepairOrderPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 维修单 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 16:15:25
 */
public interface RepairOrderService extends IService<RepairOrder> {

    /**
     * 新增维修单
     *
     * @param params 维修单
     */
    void createInfo(RepairOrderCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    RepairOrderInfoResponse getInfoById(String id);

    /**
     * 修改维修单
     *
     * @param params 维修单
     */
    void updateInfo(RepairOrderUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<RepairOrderPageResponse>> pageInfo(Paging page, RepairOrderPageRequest params);
}

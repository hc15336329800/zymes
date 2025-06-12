package cn.jb.boot.biz.item.service;

import cn.jb.boot.biz.item.entity.WarehouseInfo;
import cn.jb.boot.biz.item.vo.request.WarehouseInfoCreateRequest;
import cn.jb.boot.biz.item.vo.request.WarehouseInfoPageRequest;
import cn.jb.boot.biz.item.vo.request.WarehouseInfoUpdateRequest;
import cn.jb.boot.biz.item.vo.response.WarehouseInfoInfoResponse;
import cn.jb.boot.biz.item.vo.response.WarehouseInfoPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 库位信息 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-02 00:44:38
 */
public interface WarehouseInfoService extends IService<WarehouseInfo> {

    /**
     * 新增库位信息
     *
     * @param params 库位信息
     */
    void createInfo(WarehouseInfoCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    WarehouseInfoInfoResponse getInfoById(String id);

    /**
     * 修改库位信息
     *
     * @param params 库位信息
     */
    void updateInfo(WarehouseInfoUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<WarehouseInfoPageResponse>> pageInfo(Paging page, WarehouseInfoPageRequest params);

    List<DictListResponse> selected();

}

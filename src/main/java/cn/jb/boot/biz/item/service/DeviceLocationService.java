package cn.jb.boot.biz.item.service;

import cn.jb.boot.biz.item.entity.DeviceLocationInfo;
import cn.jb.boot.biz.item.vo.request.DeviceLocationCreateRequest;
import cn.jb.boot.biz.item.vo.response.DeviceLocationPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 库位信息 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-02 00:44:38
 */
public interface DeviceLocationService extends IService<DeviceLocationInfo> {

    /**
     * 新增设备点位信息
     *
     * @param params 库位信息
     */
    void createInfo(DeviceLocationCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    DeviceLocationPageResponse getInfoById(String id);

    /**
     * 修改设备工位信息
     *
     * @param params 库位信息
     */
    void updateInfo(DeviceLocationCreateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<DeviceLocationPageResponse>> pageInfo(Paging page, DeviceLocationCreateRequest params);

    /**
     * 根据工位库位信息查询AGV点位信息
     *
     * @return AGV点位信息
     */
    Map<String, Object> selectAGVLocation(Map<String, Object> params);

}

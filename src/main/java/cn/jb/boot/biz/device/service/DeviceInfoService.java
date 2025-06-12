package cn.jb.boot.biz.device.service;

import cn.jb.boot.biz.device.entity.DeviceInfo;
import cn.jb.boot.biz.device.vo.request.DeviceInfoCreateRequest;
import cn.jb.boot.biz.device.vo.request.DeviceInfoPageRequest;
import cn.jb.boot.biz.device.vo.request.DeviceInfoUpdateRequest;
import cn.jb.boot.biz.device.vo.response.DeviceInfoInfoResponse;
import cn.jb.boot.biz.device.vo.response.DeviceInfoPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 设备信息 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 04:46:16
 */
public interface DeviceInfoService extends IService<DeviceInfo> {

    /**
     * 新增设备信息
     *
     * @param params 设备信息
     */
    void createInfo(DeviceInfoCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    DeviceInfoInfoResponse getInfoById(String id);

    /**
     * 修改设备信息
     *
     * @param params 设备信息
     */
    void updateInfo(DeviceInfoUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<DeviceInfoPageResponse>> pageInfo(Paging page, DeviceInfoPageRequest params);

    void delete(String id);

    List<DictListResponse> deviceSelect();

    Map<String,Object> deviceTypeCount();
}

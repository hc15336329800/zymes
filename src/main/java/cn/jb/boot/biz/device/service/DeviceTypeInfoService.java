package cn.jb.boot.biz.device.service;

import cn.jb.boot.biz.device.entity.DeviceTypeInfo;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoCreateRequest;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoPageRequest;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoUpdateRequest;
import cn.jb.boot.biz.device.vo.response.DeviceTypeInfoInfoResponse;
import cn.jb.boot.biz.device.vo.response.DeviceTypeInfoPageResponse;
import cn.jb.boot.biz.device.vo.response.DeviceTypeListResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 设备类型信息 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 03:22:39
 */
public interface DeviceTypeInfoService extends IService<DeviceTypeInfo> {

    /**
     * 新增设备类型信息
     *
     * @param params 设备类型信息
     */
    void createInfo(DeviceTypeInfoCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    DeviceTypeInfoInfoResponse getInfoById(String id);

    /**
     * 修改设备类型信息
     *
     * @param params 设备类型信息
     */
    void updateInfo(DeviceTypeInfoUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<DeviceTypeInfoPageResponse>> pageInfo(Paging page, DeviceTypeInfoPageRequest params);

    List<DictListResponse> listAllType();

    void delete(String id);
}

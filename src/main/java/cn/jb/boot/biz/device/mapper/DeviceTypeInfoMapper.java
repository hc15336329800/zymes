package cn.jb.boot.biz.device.mapper;

import cn.jb.boot.biz.device.entity.DeviceTypeInfo;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoPageRequest;
import cn.jb.boot.biz.device.vo.response.DeviceTypeInfoPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 设备类型信息 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 03:22:39
 */
public interface DeviceTypeInfoMapper extends BaseMapper<DeviceTypeInfo> {

    /**
     * 根据条件分页查询设备类型信息列表
     *
     * @param params 设备类型信息信息
     * @return 设备类型信息信息集合信息
     */
    IPage<DeviceTypeInfoPageResponse> pageInfo(Page<DeviceTypeInfoPageResponse> page, @Param("p") DeviceTypeInfoPageRequest params);
}

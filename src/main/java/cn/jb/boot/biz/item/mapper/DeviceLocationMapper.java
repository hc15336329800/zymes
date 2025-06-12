package cn.jb.boot.biz.item.mapper;

import cn.jb.boot.biz.item.entity.DeviceLocationInfo;
import cn.jb.boot.biz.item.vo.request.DeviceLocationCreateRequest;
import cn.jb.boot.biz.item.vo.response.DeviceLocationPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 库位信息 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-02 00:44:38
 */
public interface DeviceLocationMapper extends BaseMapper<DeviceLocationInfo> {

    /**
     * 根据条件分页查询设备位置信息列表
     *
     * @param params 设备位置信息
     * @return 设备位置集合信息
     */
    IPage<DeviceLocationPageResponse> pageInfo(Page<DeviceLocationPageResponse> page, @Param("p") DeviceLocationCreateRequest params);

    /**
     * 根据工位库位信息查询AGV点位信息
     *
     * @param params 工位库位
     * @return AGV点位信息
     */
    Map<String, Object> selectAGVLocation(Map<String, Object> params);

    List<Map<String, Object>> getEmportLocation1(List<String> locationList);

    void updateEmportLocation(Map<String, Object> params);

    List<Map<String, Object>> getEmportLocation2(List<String> locationList);

    List<Map<String, Object>> selectLocationStatus();
}

package cn.jb.boot.biz.device.mapper;

import cn.jb.boot.biz.device.entity.DeviceInfo;
import cn.jb.boot.biz.device.vo.request.DeviceInfoPageRequest;
import cn.jb.boot.biz.device.vo.response.DeviceInfoPageResponse;
import cn.jb.boot.system.vo.DictDataVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 设备信息 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 04:46:16
 */
public interface DeviceInfoMapper extends BaseMapper<DeviceInfo> {

    /**
     * 根据条件分页查询设备信息列表
     *
     * @param params 设备信息信息
     * @return 设备信息信息集合信息
     */
    IPage<DeviceInfoPageResponse> pageInfo(Page<DeviceInfoPageResponse> page, @Param("p") DeviceInfoPageRequest params);

    List<DictDataVo> selected();

    List<Map<String,Object>> deviceTypeCount();
}

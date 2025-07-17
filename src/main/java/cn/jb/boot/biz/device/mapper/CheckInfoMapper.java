package cn.jb.boot.biz.device.mapper;

import cn.jb.boot.biz.device.entity.CheckInfo;
import cn.jb.boot.biz.device.vo.request.CheckInfoPageRequest;
import cn.jb.boot.biz.device.vo.response.CheckInfoPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 点检信息 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
public interface CheckInfoMapper extends BaseMapper<CheckInfo> {

    /**
     * 根据条件分页查询点检信息列表
     *
     * @param params 点检信息信息
     * @return 点检信息信息集合信息
     */
    IPage<CheckInfoPageResponse> pageInfo(Page<CheckInfoPageResponse> page, @Param("p") CheckInfoPageRequest params);
}

package cn.jb.boot.biz.device.mapper;

import cn.jb.boot.biz.device.entity.CheckItem;
import cn.jb.boot.biz.device.vo.request.CheckItemPageRequest;
import cn.jb.boot.biz.device.vo.response.CheckItemPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 质检项目 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
public interface CheckItemMapper extends BaseMapper<CheckItem> {

    /**
     * 根据条件分页查询质检项目列表
     *
     * @param params 质检项目信息
     * @return 质检项目信息集合信息
     */
    IPage<CheckItemPageResponse> pageInfo(Page<CheckItemPageResponse> page, @Param("p") CheckItemPageRequest params);
}

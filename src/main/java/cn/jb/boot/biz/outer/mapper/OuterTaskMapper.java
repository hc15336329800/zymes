package cn.jb.boot.biz.outer.mapper;

import cn.jb.boot.biz.outer.entity.OuterTask;
import cn.jb.boot.biz.outer.vo.request.OuterTaskPageRequest;
import cn.jb.boot.biz.outer.vo.response.OuterTaskPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 外协任务 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
public interface OuterTaskMapper extends BaseMapper<OuterTask> {

    /**
     * 根据条件分页查询外协任务列表
     *
     * @param params 外协任务信息
     * @return 外协任务信息集合信息
     */
    IPage<OuterTaskPageResponse> pageInfo(Page<OuterTaskPageResponse> page, @Param("p") OuterTaskPageRequest params);
}

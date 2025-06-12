package cn.jb.boot.biz.work.mapper;

import cn.jb.boot.biz.work.entity.WorkAssign;
import cn.jb.boot.biz.work.vo.request.WorkAssignPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkAssignPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 工单下达表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
public interface WorkAssignMapper extends BaseMapper<WorkAssign> {

    /**
     * 根据条件分页查询工单下达表列表
     *
     * @param params 工单下达表信息
     * @return 工单下达表信息集合信息
     */
    IPage<WorkAssignPageResponse> pageInfo(Page<WorkAssignPageResponse> page, @Param("p") WorkAssignPageRequest params);
}

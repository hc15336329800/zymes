package cn.jb.boot.biz.outer.mapper;

import cn.jb.boot.biz.outer.entity.OuterTaskReport;
import cn.jb.boot.biz.outer.vo.request.OuterTaskReportPageRequest;
import cn.jb.boot.biz.outer.vo.response.OuterTaskReportPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 外协任务报工 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
public interface OuterTaskReportMapper extends BaseMapper<OuterTaskReport> {

    /**
     * 根据条件分页查询外协任务报工列表
     *
     * @param params 外协任务报工信息
     * @return 外协任务报工信息集合信息
     */
    IPage<OuterTaskReportPageResponse> pageInfo(Page<OuterTaskReportPageResponse> page, @Param("p") OuterTaskReportPageRequest params);
}

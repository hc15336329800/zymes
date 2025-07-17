package cn.jb.boot.biz.work.mapper;

import cn.jb.boot.biz.work.entity.WorkReport;
import cn.jb.boot.biz.work.vo.request.WorkReportPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkReportPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 工单报工表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
public interface WorkReportMapper extends BaseMapper<WorkReport> {

    /**
     * 根据条件分页查询工单报工表列表
     * 此处查询涉及到5张表：t_work_report、t_work_order、t_group_info、mes_item_stock、t_order_dtl
     *
     * @param params 工单报工表信息
     * @return 工单报工表信息集合信息
     */
    IPage<WorkReportPageResponse> pageInfo(Page<WorkReportPageResponse> page, @Param("p") WorkReportPageRequest params);
}

package cn.jb.boot.biz.work.mapper;

import cn.jb.boot.biz.work.dto.WorkerReportSalaryExportDTO;
import cn.jb.boot.biz.work.entity.WorkerReportDtl;
import cn.jb.boot.biz.work.vo.request.WorkerReportDetailPageRequest;
import cn.jb.boot.biz.work.vo.request.WorkerReportDtlPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkerReportDtlPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工人报工明细 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-11 15:32:29
 */
public interface WorkerReportDtlMapper extends BaseMapper<WorkerReportDtl> {



//    导出工人报工工资明细（按条件查询并导出Excel）

    List<WorkerReportSalaryExportDTO> detailPageListAll(WorkerReportDetailPageRequest params);


    /**
     * 根据条件分页查询工人报工明细列表
     *
     * @param params 工人报工明细信息
     * @return 工人报工明细信息集合信息
     */
    IPage<WorkerReportDtlPageResponse> pageInfo(Page<WorkerReportDtlPageResponse> page, @Param("p") WorkerReportDtlPageRequest params);

    IPage<WorkerReportDtlPageResponse> detailPageList(Page<WorkerReportDtlPageResponse> page, @Param("p") WorkerReportDetailPageRequest params);
}

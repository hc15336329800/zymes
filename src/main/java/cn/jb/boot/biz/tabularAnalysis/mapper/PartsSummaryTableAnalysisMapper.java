package cn.jb.boot.biz.tabularAnalysis.mapper;

import cn.jb.boot.biz.depository.vo.request.OutStoreMidPageRequest;
import cn.jb.boot.biz.depository.vo.response.OutStoreMidPageResponse;
import cn.jb.boot.biz.tabularAnalysis.entity.Part;
import cn.jb.boot.biz.tabularAnalysis.vo.request.PartRequest;
import cn.jb.boot.biz.tabularAnalysis.vo.response.PartResponse;
import cn.jb.boot.biz.tray.vo.request.TrayManageInfoRequest;
import cn.jb.boot.biz.tray.vo.response.TrayManageInfoResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 托盘 Mapper 接口
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-11 20:04:08
 */
public interface PartsSummaryTableAnalysisMapper extends BaseMapper<Part> {

    /**
     * 根据条件分页查询中间件使用表列表
     *
     * @param params 中间件使用表信息
     * @return 中间件使用表信息集合信息
     */
    IPage<PartResponse> pageInfo(Page<PartResponse> page, @Param("p") PartRequest params);

    Map<String, Object> statistics();

    List<Map<String, Object>> getProcTodayDatas(@Param("nowDate") String nowDate);
}

package cn.jb.boot.biz.work.mapper;

import cn.jb.boot.biz.work.entity.WorkOrder;
import cn.jb.boot.biz.work.vo.request.WorkOrderPageRequest;
import cn.jb.boot.biz.work.vo.request.WorkOrderRequest;
import cn.jb.boot.biz.work.vo.response.WorkOrderPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 工单表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 21:52:39
 */
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    /**
     * 根据条件分页查询工单表列表
     *
     * @param params 工单表信息
     * @return 工单表信息集合信息
     */
    IPage<WorkOrderPageResponse> pageInfo(Page<WorkOrderPageResponse> page, @Param("p") WorkOrderPageRequest params);

    Long maxOrderNo();

    void saveItemand(Map<String, Object> params);

    Map<String, Object> getItemByItemNo(Map<String, Object> params);

    void updateItemand(Map<String, Object> params);

    void updateItemandByBom(Map<String, Object> params);

    void updateItemandByItemName(Map<String, Object> params);

    Map<String, Object> getItemNumber(Map<String, Object> params);

    IPage<WorkOrderPageResponse> orderPageInfo(Page<WorkOrderPageResponse> page, @Param("p") WorkOrderRequest params);

}

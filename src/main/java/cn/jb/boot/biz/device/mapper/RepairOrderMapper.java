package cn.jb.boot.biz.device.mapper;

import cn.jb.boot.biz.device.entity.RepairOrder;
import cn.jb.boot.biz.device.vo.request.RepairOrderPageRequest;
import cn.jb.boot.biz.device.vo.response.RepairOrderPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 维修单 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 16:15:25
 */
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {

    /**
     * 根据条件分页查询维修单列表
     *
     * @param params 维修单信息
     * @return 维修单信息集合信息
     */
    IPage<RepairOrderPageResponse> pageInfo(Page<RepairOrderPageResponse> page, @Param("p") RepairOrderPageRequest params);
}

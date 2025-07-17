package cn.jb.boot.biz.item.mapper;

import cn.jb.boot.biz.item.entity.DefectiveStock;
import cn.jb.boot.biz.item.vo.request.DefectiveStockPageRequest;
import cn.jb.boot.biz.item.vo.response.DefectiveStockPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 不良品库存 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 22:01:35
 */
public interface DefectiveStockMapper extends BaseMapper<DefectiveStock> {

    /**
     * 根据条件分页查询不良品库存列表
     *
     * @param params 不良品库存信息
     * @return 不良品库存信息集合信息
     */
    IPage<DefectiveStockPageResponse> pageInfo(Page<DefectiveStockPageResponse> page, @Param("p") DefectiveStockPageRequest params);
}

package cn.jb.boot.biz.item.mapper;

import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.vo.request.MidItemStockPageRequest;
import cn.jb.boot.biz.item.vo.response.MidItemStockPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mes中间件库存表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 17:28:52
 */
public interface MidItemStockMapper extends BaseMapper<MidItemStock> {

    /**
     * 根据条件分页查询mes中间件库存表列表
     *
     * @param params mes中间件库存表信息
     * @return mes中间件库存表信息集合信息
     */
    IPage<MidItemStockPageResponse> pageInfo(Page<MidItemStockPageResponse> page, @Param("p") MidItemStockPageRequest params);

    List<MesProcedure> getMissingMid();

    List<MidItemStock> selectStock(@Param("itemNos") List<String> itemNos);

}

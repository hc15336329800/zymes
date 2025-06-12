package cn.jb.boot.biz.production.mapper;


import cn.jb.boot.biz.production.entity.ProductionOrder;
import cn.jb.boot.biz.production.vo.req.ProductionOrderPageRequest;
import cn.jb.boot.biz.production.vo.resp.ProductionOrderPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 生产任务单 Mapper 接口
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-01-15 21:08:58
 */
public interface ProductionOrderMapper extends BaseMapper<ProductionOrder> {

    IPage<ProductionOrderPageResponse> pageList(Page<ProductionOrderPageResponse> page, @Param("p") ProductionOrderPageRequest q);

}

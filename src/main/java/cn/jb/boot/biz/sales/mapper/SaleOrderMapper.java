package cn.jb.boot.biz.sales.mapper;

import cn.jb.boot.biz.sales.entity.SaleOrder;
import cn.jb.boot.biz.sales.vo.request.SaleOrderPageReq;
import cn.jb.boot.biz.sales.vo.response.SaleOrderPageRep;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售单 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 00:13:32
 */
public interface SaleOrderMapper extends BaseMapper<SaleOrder> {

    String selectMaxOrderNo();

    IPage<SaleOrderPageRep> pageList(Page page, @Param("p") SaleOrderPageReq params);

    List<SaleOrderPageRep> listSalesOrder(@Param("p") SaleOrderPageReq params);

    void updateProdCount(@Param("id") String id, @Param("prodCnt") BigDecimal prodCnt);
}

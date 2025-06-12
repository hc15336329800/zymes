package cn.jb.boot.biz.sales.mapper;

import cn.jb.boot.biz.order.dto.OrderNoPageDto;
import cn.jb.boot.biz.order.dto.SaleOrderFullInfoResp;
import cn.jb.boot.biz.sales.entity.SaleOrderPlace;
import cn.jb.boot.biz.sales.model.PlaceCheck;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.PlaceOrderPageReq;
import cn.jb.boot.biz.sales.vo.response.PlaceOrderPageRep;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 下单详情流水审批 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 00:13:32
 */
public interface SaleOrderPlaceMapper extends BaseMapper<SaleOrderPlace> {

    List<PlaceCheck> placeCheckedInfo(List<String> list);

    IPage<PlaceOrderPageRep> pageList(Page page, @Param("p") PlaceOrderPageReq params);

    List<PlaceOrderPageRep> listDetails(@Param("p") ComIdsReq params);


    IPage<SaleOrderFullInfoResp> selectPlacePage(Page<?> page, @Param("dto") OrderNoPageDto dto);


}

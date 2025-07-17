package cn.jb.boot.biz.sales.service;

import cn.jb.boot.biz.sales.entity.SaleOrderPlace;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.PlaceOrderPageReq;
import cn.jb.boot.biz.sales.vo.request.PlaceOrderReq;
import cn.jb.boot.biz.sales.vo.request.PlaceRefuseReq;
import cn.jb.boot.biz.sales.vo.response.PlaceOrderPageRep;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 下单详情流水审批 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 00:13:32
 */
public interface SaleOrderPlaceService extends IService<SaleOrderPlace> {

    void placeOrder(PlaceOrderReq params);

    BaseResponse<List<PlaceOrderPageRep>> pageList(@RequestBody @Valid BaseRequest<PlaceOrderPageReq> request);

    void approval(List<String> ids);

    void refuse(PlaceRefuseReq params);

    List<PlaceOrderPageRep> listDetails(ComIdsReq params);
}

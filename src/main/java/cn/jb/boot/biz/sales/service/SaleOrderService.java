package cn.jb.boot.biz.sales.service;

import cn.jb.boot.biz.sales.entity.SaleOrder;
import cn.jb.boot.biz.sales.vo.request.SaleOrderBatchAddReq;
import cn.jb.boot.biz.sales.vo.request.SaleOrderPageReq;
import cn.jb.boot.biz.sales.vo.request.SaleOrderUpdateReq;
import cn.jb.boot.biz.sales.vo.response.SaleOrderInfoResponse;
import cn.jb.boot.biz.sales.vo.response.SaleOrderPageRep;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 销售单 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 00:13:32
 */
public interface SaleOrderService extends IService<SaleOrder> {

    void batchAdd(SaleOrderBatchAddReq params);

    void delete(List<String> ids);

    SaleOrderPageRep update(SaleOrderUpdateReq params);

    BaseResponse<List<SaleOrderPageRep>> pageList(BaseRequest<SaleOrderPageReq> request);

    void importOrder(MultipartFile file);

    void downTemp(HttpServletResponse response);

    void exportOrder(List<String> ids, HttpServletResponse response);

    List<SaleOrderPageRep> listSalesOrder(List<String> list);

    SaleOrderInfoResponse detail(ComId params);
}

package cn.jb.boot.biz.sales.service;


import cn.jb.boot.biz.sales.entity.DeliveryOrder;
import cn.jb.boot.biz.sales.vo.request.BatchUpdateReq;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryBatchAddReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryMainPageReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryMainPageResp;
import cn.jb.boot.biz.sales.vo.response.DeliveryMainResp;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 发货申请表 服务类
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-12-18 14:27:31
 */
public interface DeliveryOrderService extends IService<DeliveryOrder> {
    /**
     * 批量新增发货申请
     *
     * @param params
     * @return
     */

    void batchAdd(DeliveryBatchAddReq params);

    BaseResponse<List<DeliveryMainPageResp>> pageList(BaseRequest<DeliveryMainPageReq> request);

    BaseResponse<DeliveryMainResp> detail(ComId params);

    void delete(String id);

    void batchUpdate(BatchUpdateReq params);

    void confirm(ComIdsReq params);

    void downTemp(HttpServletResponse response);

    void importDelivery(MultipartFile multipartFile);

    void exportOrder(ComIdsReq params, HttpServletResponse response);

    void updateCount(BatchUpdateReq params);

    void commitOrder(ComId params);
}

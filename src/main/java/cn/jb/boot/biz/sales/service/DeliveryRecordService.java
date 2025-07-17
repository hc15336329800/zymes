package cn.jb.boot.biz.sales.service;

import cn.jb.boot.biz.sales.entity.DeliveryRecord;
import cn.jb.boot.biz.sales.vo.request.DeliveryRecordPageReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryRecordStaReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryRecordPageRep;
import cn.jb.boot.biz.sales.vo.response.DeliveryRecordStaRep;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 发货单记录 服务类
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-01 12:29:38
 */
public interface DeliveryRecordService extends IService<DeliveryRecord> {

    DeliveryRecordStaRep sta(DeliveryRecordStaReq params);

    BaseResponse<List<DeliveryRecordPageRep>> pageList(BaseRequest<DeliveryRecordPageReq> request);

    void export(DeliveryRecordPageReq params, HttpServletResponse response);

}

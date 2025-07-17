package cn.jb.boot.biz.sales.mapper;


import cn.jb.boot.biz.sales.entity.DeliveryRecord;
import cn.jb.boot.biz.sales.model.DeliveryRecordSta;
import cn.jb.boot.biz.sales.vo.request.DeliveryRecordPageReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryRecordPageRep;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发货单记录 Mapper 接口
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-01 12:29:38
 */
public interface DeliveryRecordMapper extends BaseMapper<DeliveryRecord> {

    List<DeliveryRecordSta> sta(String ym);

    IPage<DeliveryRecordPageRep> deliveryRecordPage(Page page, @Param("p") DeliveryRecordPageReq params);

    List<DeliveryRecordPageRep> deliveryRecordList(@Param("p") DeliveryRecordPageReq params);
}

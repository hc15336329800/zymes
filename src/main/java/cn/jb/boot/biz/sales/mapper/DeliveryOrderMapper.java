package cn.jb.boot.biz.sales.mapper;


import cn.jb.boot.biz.sales.entity.DeliveryOrder;
import cn.jb.boot.biz.sales.vo.request.DeliveryMainPageReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryMainPageResp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 发货申请表 Mapper 接口
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-12-18 14:27:31
 */
public interface DeliveryOrderMapper extends BaseMapper<DeliveryOrder> {

    IPage<DeliveryMainPageResp> pageList(Page<DeliveryMainPageResp> page, @Param("p") DeliveryMainPageReq p);
}

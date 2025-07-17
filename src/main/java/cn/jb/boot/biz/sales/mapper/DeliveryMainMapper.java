package cn.jb.boot.biz.sales.mapper;


import cn.jb.boot.biz.sales.entity.DeliveryMain;
import cn.jb.boot.biz.sales.vo.request.DeliveryMainPageReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryMainPageResp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 发运单主表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-08-13 17:34:06
 */
public interface DeliveryMainMapper extends BaseMapper<DeliveryMain> {


    IPage<DeliveryMainPageResp> pageList(Page<DeliveryMainPageResp> p, @Param("p") DeliveryMainPageReq r);
}

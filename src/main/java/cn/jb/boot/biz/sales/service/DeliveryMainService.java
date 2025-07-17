package cn.jb.boot.biz.sales.service;


import cn.jb.boot.biz.sales.entity.DeliveryMain;
import cn.jb.boot.biz.sales.vo.request.DeliveryMainPageReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryMainPageResp;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 发运单主表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-08-13 17:34:06
 */
public interface DeliveryMainService extends IService<DeliveryMain> {

    IPage<DeliveryMainPageResp> pageList(Page<DeliveryMainPageResp> p, DeliveryMainPageReq r);
}

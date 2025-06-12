package cn.jb.boot.biz.sales.service.impl;


import cn.jb.boot.biz.sales.entity.DeliveryMain;
import cn.jb.boot.biz.sales.mapper.DeliveryMainMapper;
import cn.jb.boot.biz.sales.service.DeliveryMainService;
import cn.jb.boot.biz.sales.vo.request.DeliveryMainPageReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryMainPageResp;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 发运单主表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-08-13 17:34:06
 */
@Service
public class DeliveryMainServiceImpl extends ServiceImpl<DeliveryMainMapper, DeliveryMain> implements DeliveryMainService {

    @Resource
    private DeliveryMainMapper mapper;

    @Override
    public IPage<DeliveryMainPageResp> pageList(Page<DeliveryMainPageResp> p, DeliveryMainPageReq r) {
        return mapper.pageList(p, r);
    }
}

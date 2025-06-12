package cn.jb.boot.biz.device.service.impl;

import cn.jb.boot.biz.device.entity.RepairOrder;
import cn.jb.boot.biz.device.mapper.RepairOrderMapper;
import cn.jb.boot.biz.device.service.RepairOrderService;
import cn.jb.boot.biz.device.vo.request.RepairOrderCreateRequest;
import cn.jb.boot.biz.device.vo.request.RepairOrderPageRequest;
import cn.jb.boot.biz.device.vo.request.RepairOrderUpdateRequest;
import cn.jb.boot.biz.device.vo.response.RepairOrderInfoResponse;
import cn.jb.boot.biz.device.vo.response.RepairOrderPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 维修单 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 16:15:25
 */
@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService {

    @Resource
    private RepairOrderMapper mapper;

    @Override
    public void createInfo(RepairOrderCreateRequest params) {
        RepairOrder entity = PojoUtil.copyBean(params, RepairOrder.class);
        this.save(entity);
    }

    @Override
    public RepairOrderInfoResponse getInfoById(String id) {
        RepairOrder entity = this.getById(id);
        return PojoUtil.copyBean(entity, RepairOrderInfoResponse.class);
    }

    @Override
    public void updateInfo(RepairOrderUpdateRequest params) {
        RepairOrder entity = PojoUtil.copyBean(params, RepairOrder.class);
        this.updateById(entity);
    }

    @Override
    public BaseResponse<List<RepairOrderPageResponse>> pageInfo(Paging page, RepairOrderPageRequest params) {
        PageUtil<RepairOrderPageResponse, RepairOrderPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }
}

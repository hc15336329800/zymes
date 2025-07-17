package cn.jb.boot.biz.depository.service.impl;

import cn.jb.boot.biz.depository.entity.OutStoreOrder;
import cn.jb.boot.biz.depository.mapper.OutStoreOrderMapper;
import cn.jb.boot.biz.depository.service.OutStoreOrderService;
import cn.jb.boot.biz.depository.vo.request.OutStoreOrderCreateRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreOrderPageRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreOrderUpdateRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreStatusRequest;
import cn.jb.boot.biz.depository.vo.response.OutStoreOrderInfoResponse;
import cn.jb.boot.biz.depository.vo.response.OutStoreOrderPageResponse;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Service
public class OutStoreOrderServiceImpl extends ServiceImpl<OutStoreOrderMapper, OutStoreOrder> implements OutStoreOrderService {

    @Resource
    private OutStoreOrderMapper mapper;
    @Resource
    private MesItemStockService mesItemStockService;

    @Override
    public void createInfo(OutStoreOrderCreateRequest params) {
        OutStoreOrder entity = PojoUtil.copyBean(params, OutStoreOrder.class);
        this.save(entity);
    }

    @Override
    public OutStoreOrderInfoResponse getInfoById(String id) {
        OutStoreOrder entity = this.getById(id);
        entity.setRealCount(entity.getPlanCount());
        return PojoUtil.copyBean(entity, OutStoreOrderInfoResponse.class);
    }

    @Override
    public void updateInfo(OutStoreOrderUpdateRequest params) {
        OutStoreOrder entity = PojoUtil.copyBean(params, OutStoreOrder.class);
        entity.setOutTime(LocalDateTime.now());
        entity.setOutStatus(Constants.STATUS_01);
        this.updateById(entity);
        //从库存中减去这个用料
        BigDecimal realCount = entity.getRealCount();
        String itemNo = params.getUseItemNo();
        MesItemStock one = mesItemStockService.getOne(new LambdaQueryWrapper<MesItemStock>().eq(MesItemStock::getItemNo, itemNo));
        one.setItemCount(one.getItemCount().subtract(realCount));
        mesItemStockService.updateById(one);
    }

    @Override
    public BaseResponse<List<OutStoreOrderPageResponse>> pageInfo(Paging page, OutStoreOrderPageRequest params) {
        PageUtil<OutStoreOrderPageResponse, OutStoreOrderPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateCheckStatus(OutStoreStatusRequest params) {
        List<OutStoreOrder> list = this.listByIds(params.getIds());
        list.forEach(d -> {
            d.setReviewBy(UserUtil.uid());
            d.setReviewStatus(params.getStatus());
        });
        this.updateBatchById(list);
    }
}

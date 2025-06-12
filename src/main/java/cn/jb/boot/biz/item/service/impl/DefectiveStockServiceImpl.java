package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.DefectiveStock;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.enums.StockType;
import cn.jb.boot.biz.item.mapper.DefectiveStockMapper;
import cn.jb.boot.biz.item.service.DefectiveStockService;
import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.biz.item.vo.request.DefectiveStockCreateRequest;
import cn.jb.boot.biz.item.vo.request.DefectiveStockPageRequest;
import cn.jb.boot.biz.item.vo.request.DefectiveStockUpdateRequest;
import cn.jb.boot.biz.item.vo.response.DefectiveStockInfoResponse;
import cn.jb.boot.biz.item.vo.response.DefectiveStockPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 不良品库存 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 22:01:35
 */
@Service
public class DefectiveStockServiceImpl extends ServiceImpl<DefectiveStockMapper, DefectiveStock> implements DefectiveStockService {

    @Resource
    private DefectiveStockMapper mapper;
    @Resource
    private MidItemStockService midItemStockService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createInfo(DefectiveStockCreateRequest params) {
        DefectiveStock ds = PojoUtil.copyBean(params, DefectiveStock.class);
        this.save(ds);
        String itemNo = params.getItemNo();
        updateCount(itemNo, params.getStockType(), params.getItemCount());


    }


    @Override
    public BaseResponse<List<DefectiveStockPageResponse>> pageInfo(Paging page, DefectiveStockPageRequest params) {
        PageUtil<DefectiveStockPageResponse, DefectiveStockPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateInfo(DefectiveStockUpdateRequest params) {
        DefectiveStock dbs = this.getById(params.getId());
        DefectiveStock ds = PojoUtil.copyBean(params, DefectiveStock.class);
        this.updateById(ds);
        String itemNo = params.getItemNo();
        BigDecimal sub = ArithUtil.sub(ds.getItemCount(), dbs.getItemCount());

        updateCount(itemNo, params.getStockType(), sub);

    }

    private void updateCount(String itemNo, String stockType, BigDecimal sub) {
        Map<String, MidItemStock> midsMap = midItemStockService.selectStock(Collections.singletonList(itemNo));
        MidItemStock mid = midsMap.get(itemNo);
        if (StockType.IN.getCode().equals(stockType)) {
            mid.setOutStoreUsed(ArithUtil.sub(mid.getOutStoreUsed(), sub));
        } else {
            mid.setOutStoreUsed(ArithUtil.add(mid.getOutStoreUsed(), sub));
        }
        MidItemStock up = new MidItemStock();
        up.setId(mid.getId());
        up.setOutStoreUsed(mid.getOutStoreUsed());
        midItemStockService.updateById(up);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(String id) {
        DefectiveStock dbs = this.getById(id);
        updateCount(dbs.getItemNo(), dbs.getStockType(), ArithUtil.sub(BigDecimal.ZERO, dbs.getItemCount()));
        this.removeById(id);

    }

    @Override
    public DefectiveStockInfoResponse getInfoById(String id) {
        DefectiveStock ds = this.getById(id);
        return PojoUtil.copyBean(ds, DefectiveStockInfoResponse.class);
    }
}

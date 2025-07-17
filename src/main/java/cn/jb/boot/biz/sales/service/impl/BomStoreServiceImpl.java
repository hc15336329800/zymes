package cn.jb.boot.biz.sales.service.impl;

import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.enums.StockType;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.MidItemStockService;
import cn.jb.boot.biz.production.vo.req.ProductionOrderPageRequest;
import cn.jb.boot.biz.production.vo.resp.ProductionOrderPageResponse;
import cn.jb.boot.biz.sales.entity.BomStore;
import cn.jb.boot.biz.sales.mapper.BomStoreMapper;
import cn.jb.boot.biz.sales.service.BomStoreService;
import cn.jb.boot.biz.sales.vo.request.BomStoreAddReq;
import cn.jb.boot.biz.sales.vo.request.BomStorePageReq;
import cn.jb.boot.biz.sales.vo.request.BomStoreStaReq;
import cn.jb.boot.biz.sales.vo.request.BomStoreUpdateReq;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.StaDetailReq;
import cn.jb.boot.biz.sales.vo.response.BomStoreDetailResp;
import cn.jb.boot.biz.sales.vo.response.BomStoreResp;
import cn.jb.boot.biz.sales.vo.response.BomStoreStaResp;
import cn.jb.boot.biz.sales.vo.response.StaDetailResp;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.DictUtil;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.PageConvert;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 出入库流水 服务实现类
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-12-18 14:27:31
 */
@Service
public class BomStoreServiceImpl extends ServiceImpl<BomStoreMapper, BomStore> implements BomStoreService {

    @Autowired
    private MesItemStockService stockService;
    @Resource
    private BomStoreMapper bomStoreMapper;
    @Resource
    private MidItemStockService midItemStockService;


    @Override
    public void add(BomStoreAddReq params) {
        String itemNo = params.getItemNo();
        MesItemStock stock = checkItemStock(itemNo, params.getItemCount(), params.getBizType());
        BomStore bomStore = PojoUtil.copyBean(params, BomStore.class);
        bomStore.setItemNo(stock.getItemNo());
        bomStore.setStoreStatus(JbEnum.CODE_00.getCode());
        bomStore.setAutoFlag(JbEnum.CODE_00.getCode());
        this.save(bomStore);
    }

    @NotNull
    private MesItemStock checkItemStock(String itemNo, BigDecimal itemCount, String bizType) {
        MesItemStock stock = stockService.getByItemNo(itemNo);
//        if (StoreType.IN_STORE.getCode().equals(bizType)) {
////            if (ArithUtil.less(stock.getItemCount(), itemCount)) {
////                throw new CavException("MES库存不足，无法入库,库存量:" + stock.getItemCount());
////            }
//        } else {
//            //出库
////            if (ArithUtil.less(stock.getErpCount(), itemCount)) {
////                throw new CavException("Erp库存不足，无法入库,库存量:" + stock.getErpCount());
////            }
//        }
        return stock;
    }

    @Override
    public void updateStore(BomStoreUpdateReq params) {
        String id = params.getId();
        BomStore store = this.getById(id);
        if (JbEnum.CODE_01.getCode().equals(store.getStoreStatus())) {
            throw new CavException("申请已确认，不能修改");
        }
        store.setItemNo(params.getItemNo());
        store.setItemCount(params.getItemCount());
        this.updateById(store);
    }

    @Override
    public void delete(String id) {
        BomStore store = this.getById(id);
        if (JbEnum.CODE_01.getCode().equals(store.getStoreStatus())) {
            throw new CavException("申请已确认，不能删除");
        }
        this.removeById(id);
    }

    @Override
    public BaseResponse<List<BomStoreResp>> pageList(BaseRequest<BomStorePageReq> request) {
        PageUtil<BomStoreResp, BomStorePageReq> pu = (p, q) -> bomStoreMapper.pageList(p, q);

        return PageConvert.convert(pu.page(request.getPage(), MsgUtil.params(request)), BomStoreResp.class, (t, r) -> {
            t.setItemName(DictUtil.getDictName(DictType.BOM_NO, r.getItemNo()));
        });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(BaseRequest<ComIdsReq> request) {
        List<String> ids = MsgUtil.params(request).getIds();
        List<BomStore> bomStores = this.listByIds(ids);
        List<String> itemNos = bomStores.stream().map(BomStore::getItemNo).collect(Collectors.toList());
        Map<String, MidItemStock> sm = midItemStockService.selectStock(itemNos);
        for (BomStore bomStore : bomStores) {
            String itemNo = bomStore.getItemNo();
            MidItemStock mid = sm.get(itemNo);
            if (JbEnum.CODE_00.getCode().equals(bomStore.getAutoFlag())) {
                if (Objects.nonNull(mid)) {
                    BigDecimal count = StockType.IN.getCode().equals(bomStore.getBizType()) ?
                            ArithUtil.sub(BigDecimal.ZERO, bomStore.getItemCount()) : bomStore.getItemCount();
                    mid.setOutStoreUsed(ArithUtil.add(count, mid.getOutStoreUsed()));
                }
            } else {
                if (Objects.nonNull(mid) && StockType.OUT.getCode().equals(bomStore.getBizType())) {
                    mid.setOutStoreUsed(ArithUtil.add(bomStore.getItemCount(), mid.getOutStoreUsed()));
                }

            }
            bomStore.setStoreStatus(JbEnum.CODE_01.getCode());
            bomStore.setConfirmTime(LocalDateTime.now());
        }
        this.updateBatchById(bomStores);
        if (CollectionUtils.isNotEmpty(sm.values())) {
            List<MidItemStock> mids = sm.values().stream().map(
                    d -> {
                        MidItemStock mid = new MidItemStock();
                        mid.setId(d.getId());
                        mid.setOutStoreUsed(d.getOutStoreUsed());
                        return mid;
                    }
            ).collect(Collectors.toList());

            midItemStockService.updateBatchById(mids);
        }
    }

    @Override
    public List<BomStoreStaResp> sta(BomStoreStaReq req) {
        return bomStoreMapper.sta(req);
    }

    @Override
    public BaseResponse<List<StaDetailResp>> staDetail(BaseRequest<StaDetailReq> request) {
        PageUtil<StaDetailResp, StaDetailReq> pu = (p, r) -> bomStoreMapper.staDetail(p, r);
        return pu.page(request.getPage(), MsgUtil.params(request));
    }

    @Override
    public BomStoreDetailResp detail(ComId params) {
        BomStore en = this.getById(params.getId());
        return PojoUtil.copyBean(en, BomStoreDetailResp.class);
    }


}

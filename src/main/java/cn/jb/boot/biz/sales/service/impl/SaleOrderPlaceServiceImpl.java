package cn.jb.boot.biz.sales.service.impl;

import cn.jb.boot.biz.production.emuns.ProductionStatus;
import cn.jb.boot.biz.production.entity.ProductionOrder;
import cn.jb.boot.biz.production.service.ProductionOrderService;
import cn.jb.boot.biz.sales.entity.SaleOrder;
import cn.jb.boot.biz.sales.entity.SaleOrderPlace;
import cn.jb.boot.biz.sales.enums.ApprovalStatus;
import cn.jb.boot.biz.sales.mapper.SaleOrderPlaceMapper;
import cn.jb.boot.biz.sales.model.PlaceCheck;
import cn.jb.boot.biz.sales.service.SaleOrderPlaceService;
import cn.jb.boot.biz.sales.service.SaleOrderService;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.PlaceOrderInfos;
import cn.jb.boot.biz.sales.vo.request.PlaceOrderPageReq;
import cn.jb.boot.biz.sales.vo.request.PlaceOrderReq;
import cn.jb.boot.biz.sales.vo.request.PlaceRefuseReq;
import cn.jb.boot.biz.sales.vo.response.PlaceOrderPageRep;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 下单详情流水审批 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 00:13:32
 */
@Service
public class SaleOrderPlaceServiceImpl extends ServiceImpl<SaleOrderPlaceMapper, SaleOrderPlace> implements SaleOrderPlaceService {


    @Autowired
    private SaleOrderPlaceMapper mapper;

    @Autowired
    private SaleOrderService saleOrderService;
    @Resource
    private ProductionOrderService poService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void placeOrder(PlaceOrderReq params) {
        List<PlaceOrderInfos> places = params.getList();
        List<String> ids = new ArrayList<>();
        places.forEach(p -> ids.add(p.getId()));
        // 查询数据库
        List<PlaceCheck> checks = mapper.placeCheckedInfo(ids);
        Map<String, PlaceCheck> map = new HashMap<>();
        checks.forEach(o -> map.put(o.getId(), o));
        String uid = UserUtil.uid();
        List<SaleOrderPlace> list = new ArrayList<>();
        places.forEach(p -> {
            String id = p.getId();
            PlaceCheck pc = map.get(id);
            if (pc == null) {
                throw new CavException("销售单" + id + "不存在！");
            }
            if (ArithUtil.ge(BigDecimal.ZERO, p.getOrderedNum())) {
                throw new CavException("下生产单数量需要大于0");
            }
            // 需求量，已经下单数量 (所有的申请 除了审批拒绝的数量) ，输入的下单数量
            if (ArithUtil.lessZero(pc.getNeedNum(), pc.getOrderedNum(), p.getOrderedNum())) {
                throw new CavException(id + "下单数量超出剩余量！");
            }
            if (ArithUtil.lessZero(p.getOrderedNum())) {
                throw new CavException(id + "下单数量需大于0！");

            }
            SaleOrderPlace op = new SaleOrderPlace();
            op.setOrderNo(pc.getOrderNo());
            op.setSaleId(id);
            if (Objects.nonNull(params.getDeliverTime())) {
                op.setDeliverTime(params.getDeliverTime().atStartOfDay());
            }
            op.setItemNo(pc.getItemNo());
            op.setBizType(params.getBizType());
            op.setOrderedNum(p.getOrderedNum());
            op.setApplyTime(LocalDateTime.now());
            op.setApplyNo(uid);
            op.setApplyName(UserUtil.user().getNickName()); //获取用户信息
            op.setPlaceStatus(ApprovalStatus.TO_REVIEW.getCode());
            list.add(op);

        });
        saveBatch(list);
    }

    @Override
    public BaseResponse<List<PlaceOrderPageRep>> pageList(BaseRequest<PlaceOrderPageReq> request) {
        PlaceOrderPageReq params = MsgUtil.params(request);
        PageUtil<PlaceOrderPageRep, PlaceOrderPageReq> pu = (p, q) -> mapper.pageList(p, q);
        return pu.page(request.getPage(), params);
    }


    // 审批拒绝
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void approval(List<String> ids) {
        Map<String, SaleOrderPlace> map = checkedStatus(ids);
        List<String> saleIds = new ArrayList<>();
        for (Map.Entry<String, SaleOrderPlace> m : map.entrySet()) {
            saleIds.add(m.getValue().getSaleId());
        }
        List<SaleOrder> orders = saleOrderService.listByIds(saleIds);
        List<SaleOrderPlace> list = new ArrayList<>();
        // 写生产单明细
        List<ProductionOrder> pos = new ArrayList<>();
        ids.forEach(i -> {
            SaleOrderPlace p = new SaleOrderPlace();
            p.setId(i);
            p.setPlaceStatus(ApprovalStatus.PASSED.getCode());
            list.add(p);
            SaleOrder currso = null;
            // 销售单
            SaleOrderPlace oldPlace = map.get(i);
            for (SaleOrder so : orders) {
                if (oldPlace.getSaleId().equals(so.getId())) {
                    so.setApprovedOrderedNum((ArithUtil.add(so.getApprovedOrderedNum(), oldPlace.getOrderedNum())));
                    // 状态更改
                    if (JbEnum.CODE_00.getCode().equals(so.getOrderStatus())) {
                        so.setOrderStatus(JbEnum.CODE_01.getCode());
                    }
                    if (so.getApprovedOrderedNum().compareTo(so.getNeedNum()) == 0) {
                        so.setOrderStatus(JbEnum.CODE_02.getCode());
                    }
                    currso = so;
                    break;
                }
            }
            ProductionOrder po = new ProductionOrder();
            po.setSalesOrderNo(currso.getOrderNo());
            po.setItemNo(currso.getItemNo());
            po.setDeliverTime(oldPlace.getDeliverTime());
            po.setBizType(oldPlace.getBizType());
            po.setItemCount(oldPlace.getOrderedNum());
            po.setStatus(ProductionStatus.TO_READY.getCode());
            po.setPlaceId(oldPlace.getId());
            po.setProcedureCode(currso.getProcedureCode());
            po.setProcedureName(currso.getProcedureName());
            pos.add(po);
        });
        this.updateBatchById(list);
        saleOrderService.updateBatchById(orders);
        poService.saveBatch(pos);
    }


    //   修改： id、placeStatus（拒绝状态码）、approvalMsg（审批信息）
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void refuse(PlaceRefuseReq params) {
        List<String> ids = params.getList();
        checkedStatus(ids);

        // 更新 SaleOrderPlace 拒绝状态
        List<SaleOrderPlace> list = new ArrayList<>();
        ids.forEach(i -> {
            SaleOrderPlace p = new SaleOrderPlace();
            p.setId(i);
            p.setPlaceStatus(ApprovalStatus.REJECT.getCode());
            p.setApprovalMsg(params.getApprovalMsg());

            list.add(p);
        });
        this.updateBatchById(list);


        // 新增：根据 place_id 查出对应的 sale_id，然后更新 SaleOrder
        List<SaleOrder> orderList = new ArrayList<>();
        List<SaleOrderPlace> placeList = this.listByIds(ids); // 一次查出所有记录，避免多次查询DB
        placeList.forEach(place -> {
            if (place.getSaleId() != null) {
                SaleOrder o = new SaleOrder();
                o.setId(place.getSaleId()); // 用 sale_id 更新 SaleOrder
                o.setOrderStatus(JbEnum.CODE_00.getCode());
                orderList.add(o);
            }
        });

        if (!orderList.isEmpty()) {
            saleOrderService.updateBatchById(orderList);
        }


    }

    @Override
    public List<PlaceOrderPageRep> listDetails(ComIdsReq params) {
        return mapper.listDetails(params);
    }

    private Map<String, SaleOrderPlace> checkedStatus(List<String> ids) {
        // 查询未审核的
        LambdaQueryWrapper<SaleOrderPlace> eq =
                new LambdaQueryWrapper<SaleOrderPlace>()
                        .eq(SaleOrderPlace::getPlaceStatus, ApprovalStatus.TO_REVIEW.getCode())
                        .in(SaleOrderPlace::getId, ids);
        List<SaleOrderPlace> dbs = list(eq);
        Map<String, SaleOrderPlace> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dbs)) {
            dbs.forEach(p -> map.put(p.getId(), p));
        }
        ids.forEach(i -> {
            if (map.get(i) == null) {
                throw new CavException("订单状态不为待审批！");
            }
        });
        return map;
    }


}

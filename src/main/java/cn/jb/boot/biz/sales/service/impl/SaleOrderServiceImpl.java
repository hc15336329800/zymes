package cn.jb.boot.biz.sales.service.impl;

import cn.hutool.poi.excel.ExcelWriter;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.service.MesProcedureService;
import cn.jb.boot.biz.sales.entity.SaleOrder;
import cn.jb.boot.biz.sales.mapper.SaleOrderMapper;
import cn.jb.boot.biz.sales.mapper.SaleOrderPlaceMapper;
import cn.jb.boot.biz.sales.model.PlaceCheck;
import cn.jb.boot.biz.sales.model.SaleOrderExport;
import cn.jb.boot.biz.sales.service.SaleOrderService;
import cn.jb.boot.biz.sales.vo.request.SaleOrderBatchAddInfos;
import cn.jb.boot.biz.sales.vo.request.SaleOrderBatchAddReq;
import cn.jb.boot.biz.sales.vo.request.SaleOrderPageReq;
import cn.jb.boot.biz.sales.vo.request.SaleOrderUpdateReq;
import cn.jb.boot.biz.sales.vo.response.SaleOrderInfoResponse;
import cn.jb.boot.biz.sales.vo.response.SaleOrderPageRep;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.DateUtil;
import cn.jb.boot.util.DictUtil;
import cn.jb.boot.util.EasyExcelUtil;
import cn.jb.boot.util.ExcelUtil;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 销售单 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 00:13:32
 */
@Service
public class SaleOrderServiceImpl extends ServiceImpl<SaleOrderMapper, SaleOrder> implements SaleOrderService {

    @Resource
    private SaleOrderMapper mapper;
    @Autowired
    private SaleOrderPlaceMapper placeMapper;
    @Resource
    private MesProcedureService mesProcedureService;
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchAdd(SaleOrderBatchAddReq params) {
        List<SaleOrderBatchAddInfos> list = params.getList();
        String orderNo = genOrderNo();
        List<SaleOrder> orders = new ArrayList<>();
        SaleOrderBatchAddInfos dorder = list.get(0);
        if (StringUtils.isEmpty(dorder.getCustName())) {
            throw new CavException("第一个客户名称不能为空");
        }
        for (int i = 0; i < list.size(); i++) {
            SaleOrderBatchAddInfos s = list.get(i);
            if (StringUtils.isNotBlank(s.getItemNo())) {
                SaleOrder order = new SaleOrder();
                order.setCustName(StringUtils.isBlank(s.getCustName()) ? dorder.getCustName() : s.getCustName());
                order.setNeedNum(s.getNeedNum());
                order.setItemNo(s.getItemNo());
                order.setOrderNo(orderNo);
                order.setOrderStatus(JbEnum.CODE_00.getCode());
                order.setApprovedOrderedNum(BigDecimal.ZERO);
                order.setProducedNum(BigDecimal.ZERO);
                if (StringUtils.isNotBlank(s.getProcedureId())) {
                    MesProcedure mp = mesProcedureService.getById(s.getProcedureId());
                    order.setProcedureName(mp.getProcedureName());
                    order.setProcedureCode(mp.getProcedureCode());
                }
                orders.add(order);
            }
        }
        saveBatch(orders);

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(List<String> ids) {
        LambdaQueryWrapper<SaleOrder> eq =
                new LambdaQueryWrapper<SaleOrder>()
                        .eq(SaleOrder::getOrderStatus, JbEnum.CODE_00.getCode())
                        .in(SaleOrder::getId, ids);
        List<SaleOrder> orders = list(eq);
        if (CollectionUtils.isEmpty(orders) || orders.size() != ids.size()) {
            throw new CavException("销售单已经下单，无法删除！");
        }
        mapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public SaleOrderPageRep update(SaleOrderUpdateReq params) {
        String id = params.getId();
        List<String> ids = new ArrayList<>();
        ids.add(id);
        List<PlaceCheck> list = placeMapper.placeCheckedInfo(ids);
        if (CollectionUtils.isEmpty(list) || list.get(0) == null) {
            throw new CavException("销售单不存在！");
        }
        PlaceCheck check = list.get(0);
        SaleOrder so = new SaleOrder();
        so.setId(params.getId());
        so.setNeedNum(params.getNeedNum());
        if (ArithUtil.lessZero(params.getNeedNum(), check.getOrderedNum())) {
            throw new CavException("修改数量不能少于已经下单的数量！");
        }
        mapper.updateById(so);
        SaleOrderPageReq req = new SaleOrderPageReq();
        req.setIds(Collections.singletonList(id));
        List<SaleOrderPageRep> saleOrderPageReps = mapper.listSalesOrder(req);
        return saleOrderPageReps.get(0);
    }

    @Override
    public BaseResponse<List<SaleOrderPageRep>> pageList(BaseRequest<SaleOrderPageReq> request) {
        SaleOrderPageReq params = MsgUtil.params(request);
        PageUtil<SaleOrderPageRep, SaleOrderPageReq> pu = (p, q) -> mapper.pageList(p, q);
        BaseResponse<List<SaleOrderPageRep>> page = pu.page(request.getPage(), params);
//        page.getData().sort(Comparator.comparing(SaleOrderPageRep::getCustName));
        return page;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void importOrder(MultipartFile file) {
        String orderNo = genOrderNo();
        List<SaleOrder> saleOrders = EasyExcelUtil.importSalesOrder(file, orderNo);
        saveBatch(saleOrders);
    }

    @Override
    public void downTemp(HttpServletResponse response) {
        String name = "sale_order_temp.xlsx";
        EasyExcelUtil.downloadExcelTemp(response, name);
    }

    @Override
    public void exportOrder(List<String> ids, HttpServletResponse response) {
        List<SaleOrder> orders = list(new LambdaQueryWrapper<SaleOrder>().in(SaleOrder::getId, ids).orderByDesc(SaleOrder::getCreatedTime));
        ExcelUtil.writeExcel(response, "销售订单", w -> {
            w.renameSheet(0, "销售单");
            writeOrderData(w, orders);
        });
    }

    private void writeOrderData(ExcelWriter writer, List<SaleOrder> list) {
        //自定义标题别名
        List<SaleOrderExport> exports = new ArrayList<>();
        list.forEach(s -> {
            SaleOrderExport e = PojoUtil.copyBean(s, SaleOrderExport.class);
            e.setProducedNum(s.getProducedNum() + "/" + (s.getNeedNum().subtract(s.getProducedNum())));
//            e.setOrderedNum(s.getApprovedOrderedNum() + "/" + (s.getNeedNum().subtract(s.getApprovedOrderedNum())));
            e.setBomNo(DictUtil.getDictLabel(DictType.BOM_NO, s.getItemNo()));
            exports.add(e);
        });
        writer.addHeaderAlias("orderNo", "订单号");
        writer.addHeaderAlias("custName", "客户");
        writer.addHeaderAlias("bomNo", "图纸号");
        writer.addHeaderAlias("needNum", "需求数量");
        writer.addHeaderAlias("orderedNum", "已下单/待下单");
//        writer.addHeaderAlias("producedNum","已生产/待生产");
        writer.write(exports, true);
    }


    private String genOrderNo() {
        String dbNo = mapper.selectMaxOrderNo();
        String ymd = DateUtil.formatDate(LocalDate.now(), null);
        int index = 1;
        if (StringUtils.isNotBlank(dbNo)) {
            // 202210010001
            index += Integer.parseInt(StringUtils.substring(dbNo, 8, 12));
        }
        return ymd + String.format("%04d", index);
    }

    @Override
    public List<SaleOrderPageRep> listSalesOrder(List<String> list) {
        SaleOrderPageReq req = new SaleOrderPageReq();
        req.setIds(list);
        return mapper.listSalesOrder(req);
    }

    @Override
    public SaleOrderInfoResponse detail(ComId params) {
        SaleOrder so = this.getById(params.getId());
        return PojoUtil.copyBean(so, SaleOrderInfoResponse.class);
    }


}

package cn.jb.boot.biz.sales.service.impl;

import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.enums.StockType;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.sales.entity.BomStore;
import cn.jb.boot.biz.sales.entity.DeliveryDtl;
import cn.jb.boot.biz.sales.entity.DeliveryMain;
import cn.jb.boot.biz.sales.entity.DeliveryOrder;
import cn.jb.boot.biz.sales.entity.DeliveryRecord;
import cn.jb.boot.biz.sales.mapper.DeliveryOrderMapper;
import cn.jb.boot.biz.sales.service.BomStoreService;
import cn.jb.boot.biz.sales.service.DeliveryDtlService;
import cn.jb.boot.biz.sales.service.DeliveryMainService;
import cn.jb.boot.biz.sales.service.DeliveryOrderService;
import cn.jb.boot.biz.sales.service.DeliveryRecordService;
import cn.jb.boot.biz.sales.vo.request.BatchUpdateReq;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryBatchAddReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryDtlAddReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryMainPageReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryOrderAddReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryMainPageResp;
import cn.jb.boot.biz.sales.vo.response.DeliveryMainResp;
import cn.jb.boot.biz.sales.vo.response.DeliveryOrderDetailResp;
import cn.jb.boot.biz.sales.vo.response.DeliveryResp;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.entity.TokenCacheObj;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.framework.exception.CavException;

import cn.jb.boot.util.ArithUtil;
import cn.jb.boot.util.DateUtil;
import cn.jb.boot.util.EasyExcelUtil;
import cn.jb.boot.util.ExcelUtil;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.PageConvert;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.SnowFlake;
import cn.jb.boot.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 发货申请表 服务实现类
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-12-18 14:27:31
 */
@Service
public class DeliveryOrderServiceImpl extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder> implements DeliveryOrderService {


    @Autowired
    private DeliveryDtlService dtlService;
    @Autowired
    private MesItemStockService stockService;

    @Autowired
    private DeliveryRecordService deliveryRecordService;
    @Autowired
    private BomStoreService bomStoreService;
    @Resource
    private DeliveryOrderMapper deliveryOrderMapper;
    @Resource
    private DeliveryMainService mainService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchAdd(DeliveryBatchAddReq req) {
        DeliveryMain dm = PojoUtil.copyBean(req, DeliveryMain.class);
        dm.setId(SnowFlake.genId());
        List<DeliveryOrder> orders = new ArrayList<>();
        List<DeliveryDtl> dtlList = new ArrayList<>();
        List<DeliveryOrderAddReq> list = req.getList();
        for (DeliveryOrderAddReq deo : list) {
            DeliveryOrder order = PojoUtil.copyBean(deo, DeliveryOrder.class);
            if (StringUtils.isEmpty(deo.getCustomer())) {
                order.setCustomer(dm.getCustomer());
            }
            if (StringUtils.isEmpty(deo.getDestination())) {
                order.setDestination(dm.getDestination());
            }
            if (Objects.isNull(deo.getDeliveryDate())) {
                order.setDeliveryDate(dm.getDeliveryDate());
            }
            String id = SnowFlake.genId();
            order.setId(id);
            order.setMainId(dm.getId());
            orders.add(order);
            List<DeliveryDtlAddReq> dtls = deo.getDtls();
            for (int i = 0; i < dtls.size(); i++) {
                DeliveryDtlAddReq dtl = dtls.get(i);
                DeliveryDtl dd = PojoUtil.copyBean(dtl, DeliveryDtl.class);
                dd.setDeliveryId(id);
                dd.setItemNo(dtl.getItemNo());
                dd.setId(null);
                if (StringUtils.isEmpty(dd.getTruck()) && i == 0) {
                    dd.setTruck(dm.getDriver());
                }
                dtlList.add(dd);
            }
        }
        mainService.save(dm);
        this.saveBatch(orders);
        dtlService.saveBatch(dtlList);
    }

    @Override
    public BaseResponse<List<DeliveryMainPageResp>> pageList(BaseRequest<DeliveryMainPageReq> request) {
        PageUtil<DeliveryMainPageResp, DeliveryMainPageReq> pu = (p, r) -> mainService.pageList(p, r);
        BaseResponse<List<DeliveryMainPageResp>> page = pu.page(request.getPage(), MsgUtil.params(request));
        return PageConvert.convert(page, DeliveryMainPageResp.class);
    }

    @Override
    public BaseResponse<DeliveryMainResp> detail(ComId params) {
        String mainId = params.getId();
        List<DeliveryOrder> deos = this.list(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getMainId,
                mainId));
        List<String> ids = deos.stream().map(DeliveryOrder::getId).collect(Collectors.toList());
        List<DeliveryDtl> list = dtlService.list(new LambdaQueryWrapper<DeliveryDtl>().in(DeliveryDtl::getDeliveryId,
                ids));
        Map<String, List<DeliveryDtl>> map = list.stream().collect(Collectors.groupingBy(DeliveryDtl::getDeliveryId));
        List<DeliveryOrderDetailResp> resps = PojoUtil.copyList(deos, DeliveryOrderDetailResp.class);
        resps.forEach(resp -> {
            String id = resp.getId();
            List<DeliveryDtl> dtls = map.get(id);
            List<DeliveryResp> deliveryResps = PojoUtil.copyList(dtls, DeliveryResp.class);
            resp.setDtls(deliveryResps);
        });
        DeliveryMain main = mainService.getById(mainId);
        DeliveryMainResp resp = PojoUtil.copyBean(main, DeliveryMainResp.class);
        resp.setList(resps);
        return MsgUtil.ok(resp);
    }

    @Override
    public void delete(String id) {
        DeliveryMain main = mainService.getById(id);
        if (JbEnum.CODE_01.getCode().equals(main.getStatus())) {
            throw new CavException("发运单已经被确认，不能修改");
        }
        mainService.removeById(id);
        this.remove(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getMainId, id));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchUpdate(BatchUpdateReq params) {
        String id = params.getId();
        delete(id);
        batchAdd(params);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void confirm(ComIdsReq params) {
        List<String> ids = params.getIds();
        mainService.update(new LambdaUpdateWrapper<DeliveryMain>()
                .in(DeliveryMain::getId, ids)
                .set(DeliveryMain::getStatus, JbEnum.CODE_01.getCode())
        );
    }

    @Override
    public void downTemp(HttpServletResponse response) {
        String name = "delivery_order.xlsx";
        EasyExcelUtil.downloadExcelTemp(response, name);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void importDelivery(MultipartFile file) {
        List<DeliveryOrder> deliveries = readExcel(file);
        saveBatch(deliveries);
    }

    private List<DeliveryOrder> readExcel(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (!StringUtils.endsWithIgnoreCase(fileName, "xlsx")) {
            throw new CavException("文件必须是xlsx！");
        }
        List<String> boms = new ArrayList<>();
        List<DeliveryDtl> dtls = new ArrayList<>();
        List<DeliveryOrder> list = ExcelUtil.importExcel(file, (sheet, lastRowNumber) -> {
            XSSFRow row;
            String bom;
            double needNum;
            List<DeliveryOrder> deliveries = Lists.newArrayList();
            for (int i = 0; i < lastRowNumber; ) {
                row = sheet.getRow(i + 1);
                XSSFCell cell;
                if (row == null) {
                    i++;
                    continue;
                }
                String id = SnowFlake.genId();
                DeliveryOrder delivery = new DeliveryOrder();
                delivery.setId(id);
//                delivery.setMainId(main.getId());
                cell = row.getCell(1);
                String dest = ExcelUtil.cellString(cell);
                delivery.setDestination(dest);
                cell = row.getCell(2);
                String cust = ExcelUtil.cellString(cell);
                delivery.setCustomer(cust);
                cell = row.getCell(5);
                String dateStr = ExcelUtil.cellString(cell);
                LocalDate parse = DateUtil.parse(dateStr, DateUtil.DATE_PATTERN);
                delivery.setDeliveryDate(parse);
                List<DeliveryDtl> details = new ArrayList<>();
                //明细数据
                for (int j = i + 3; j < i + 11; j++) {
                    row = sheet.getRow(j);
                    if (Objects.isNull(row)) {
                        continue;
                    }
                    DeliveryDtl dtl = new DeliveryDtl();
                    dtl.setDeliveryId(id);
                    cell = row.getCell(0);
//                    dtl.setItemName(ExcelUtil.cellString(cell));
                    cell = row.getCell(1);
                    bom = ExcelUtil.cellString(cell);
                    dtl.setBomNo(bom);
                    if (StringUtils.isEmpty(bom)) {
                        continue;
                    }
                    boms.add(bom);
                    cell = row.getCell(2);
                    needNum = ExcelUtil.needNum(cell, i + 1);
                    dtl.setPlanCount(BigDecimal.valueOf(needNum));
                    cell = row.getCell(3);
                    dtl.setItemMeasure(ExcelUtil.cellString(cell));
                    cell = row.getCell(6);
                    dtl.setTruck(ExcelUtil.cellString(cell));
                    if (StringUtils.isEmpty(delivery.getDriver()) && StringUtils.isNotBlank(dtl.getTruck())) {
                        delivery.setDriver(dtl.getTruck());
                    }
                    cell = row.getCell(7);
                    dtl.setTruckNo(ExcelUtil.cellString(cell));
                    cell = row.getCell(8);
                    dtl.setRemark(ExcelUtil.cellString(cell));
                    details.add(dtl);
                }

                if (CollectionUtils.isEmpty(details)) {
                    i++;
                    continue;
                }
                dtls.addAll(details);
                row = sheet.getRow(i + 11);
                cell = row.getCell(1);
                delivery.setPreparedBy(ExcelUtil.cellString(cell));
                cell = row.getCell(3);
                delivery.setDeliverer(ExcelUtil.cellString(cell));
                cell = row.getCell(7);
                delivery.setQualityBy(ExcelUtil.cellString(cell));
                row = sheet.getRow(i + 12);
                cell = row.getCell(1);
                delivery.setDoorman(ExcelUtil.cellString(cell));
                cell = row.getCell(3);
                delivery.setAcceptedBy(ExcelUtil.cellString(cell));
                cell = row.getCell(6);
                delivery.setBoxNum(ExcelUtil.cellString(cell));
                deliveries.add(delivery);
                i += 14;
            }
            return deliveries;
        });
        Map<String, MesItemStock> map = stockService.checkedBoms(boms);
        dtls.forEach(dtl -> {
            MesItemStock stock = map.get(dtl.getBomNo());
            dtl.setItemNo(stock.getItemNo());
        });
        dtlService.saveBatch(dtls);
        Map<String, List<DeliveryOrder>> orderMap =
                list.stream().collect(Collectors.groupingBy(d -> d.getDriver() + "|" + d.getDeliveryDate()));
        List<DeliveryMain> mains = new ArrayList<>();
        for (String s : orderMap.keySet()) {
            List<DeliveryOrder> dos = orderMap.get(s);
            DeliveryOrder dorder = dos.get(0);
            DeliveryMain main = new DeliveryMain();
            main.setId(SnowFlake.genId());
            String cust =
                    dos.stream().map(DeliveryOrder::getCustomer).distinct().filter(StringUtils::isNotEmpty).collect(Collectors.joining(","));
            main.setCustomer(cust);
            main.setDeliveryDate(dorder.getDeliveryDate());
            String dest =
                    dos.stream().map(DeliveryOrder::getDestination).distinct().filter(StringUtils::isNotEmpty).collect(Collectors.joining(","));
            main.setDestination(dest);
            main.setDriver(dorder.getDriver());
            dos.forEach(d -> d.setMainId(main.getId()));
            mains.add(main);
        }
        if (CollectionUtils.isNotEmpty(mains)) {
            mainService.saveBatch(mains);
        }
        return list;
    }


    public void autoStore(List<DeliveryDtl> list) {
        List<BomStore> bomStores = new ArrayList<>();
        for (DeliveryDtl dtl : list) {
            String itemNo = dtl.getItemNo();
            if (ArithUtil.ge(BigDecimal.ZERO, dtl.getRealCount())) {
                throw new CavException(dtl.getItemNo() + "发货数量为空");
            }

            BomStore out = new BomStore();
            out.setDeliveryId(dtl.getDeliveryId());
            out.setItemCount(dtl.getRealCount());
            out.setStoreStatus(JbEnum.CODE_00.getCode());
            out.setItemNo(itemNo);
            out.setAutoFlag(JbEnum.CODE_01.getCode());
            out.setBizType(StockType.OUT.getCode());
            out.setId(SnowFlake.genId());
            bomStores.add(out);
        }
        bomStoreService.saveBatch(bomStores);

    }

    @Override
    public void exportOrder(ComIdsReq params, HttpServletResponse response) {
        List<String> ids = params.getIds();
        List<DeliveryOrder> orders = this.list(new LambdaQueryWrapper<DeliveryOrder>().in(DeliveryOrder::getMainId,
                ids));
        List<String> oids = orders.stream().map(DeliveryOrder::getId).collect(Collectors.toList());
        List<DeliveryDtl> dtls = dtlService.list(new LambdaQueryWrapper<DeliveryDtl>().in(DeliveryDtl::getDeliveryId,
                oids));
        Map<String, List<DeliveryDtl>> dtlMap =
                dtls.stream().collect(Collectors.groupingBy(DeliveryDtl::getDeliveryId));
        ExcelUtil.writeDeliveryOrder(orders, dtlMap, response);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateCount(BatchUpdateReq params) {
        String id = params.getId();
        DeliveryMain main = mainService.getById(id);
        if (!JbEnum.CODE_01.getCode().equals(main.getStatus())) {
            throw new CavException("请先确认领料单!");
        }
        List<DeliveryOrderAddReq> list = params.getList();
        List<DeliveryDtl> dtlList = new ArrayList<>();
        list.stream().forEach(d -> {
            d.getDtls().forEach(dtl -> {
                DeliveryDtl dd = new DeliveryDtl();
                dd.setId(dtl.getId());
                dd.setRealCount(dtl.getRealCount());
                dtlList.add(dd);
            });
        });

        dtlService.updateBatchById(dtlList);


    }

    @Override
    public void commitOrder(ComId params) {
        String id = params.getId();
        DeliveryMain dm = new DeliveryMain();
        dm.setId(id);
        dm.setCommitStatus(JbEnum.CODE_01.getCode());
        mainService.updateById(dm);
        List<DeliveryOrder> olist = this.list(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getMainId, id));
        List<String> oids = olist.stream().map(DeliveryOrder::getId).collect(Collectors.toList());
        List<DeliveryDtl> ddList = dtlService.list(new LambdaQueryWrapper<DeliveryDtl>().in(DeliveryDtl::getDeliveryId, oids));
        autoStore(ddList);
        saveRecords(olist.get(0).getCustomer(), ddList);
    }

    private void saveRecords(String customer, List<DeliveryDtl> ddList) {
        List<DeliveryRecord> saveList = new ArrayList<>();
        for (DeliveryDtl dtl : ddList) {
            DeliveryRecord deliveryRecord = new DeliveryRecord();
            deliveryRecord.setDeliveryId(dtl.getDeliveryId());
            deliveryRecord.setItemNo(dtl.getItemNo());
            deliveryRecord.setCustName(customer);
            deliveryRecord.setSendCount(dtl.getRealCount());
            deliveryRecord.setSendTime(LocalDateTime.now());
            TokenCacheObj user = UserUtil.user();
            deliveryRecord.setSendAcctNo(user.getUid());
            deliveryRecord.setSendAcctName(user.getUserName());
            saveList.add(deliveryRecord);
        }
        if (CollectionUtils.isNotEmpty(saveList)) {
            deliveryRecordService.saveBatch(saveList);
        }
    }

}

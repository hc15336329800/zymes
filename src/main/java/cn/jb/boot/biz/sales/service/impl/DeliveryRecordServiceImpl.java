package cn.jb.boot.biz.sales.service.impl;

import cn.hutool.poi.excel.ExcelWriter;
import cn.jb.boot.biz.sales.entity.DeliveryRecord;
import cn.jb.boot.biz.sales.mapper.DeliveryRecordMapper;
import cn.jb.boot.biz.sales.model.DeliveryRecordSta;
import cn.jb.boot.biz.sales.model.DeliveryRecordStaSusb;
import cn.jb.boot.biz.sales.service.DeliveryRecordService;
import cn.jb.boot.biz.sales.vo.request.DeliveryRecordPageReq;
import cn.jb.boot.biz.sales.vo.request.DeliveryRecordStaReq;
import cn.jb.boot.biz.sales.vo.response.DeliveryRecordInfos;
import cn.jb.boot.biz.sales.vo.response.DeliveryRecordPageRep;
import cn.jb.boot.biz.sales.vo.response.DeliveryRecordStaRep;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.DateUtil;
import cn.jb.boot.util.EasyExcelUtil;
import cn.jb.boot.util.ExcelUtil;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发货单记录 服务实现类
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-01 12:29:38
 */
@Service
public class DeliveryRecordServiceImpl extends ServiceImpl<DeliveryRecordMapper, DeliveryRecord> implements DeliveryRecordService {
    @Autowired
    private DeliveryRecordMapper recordMapper;


    @Override
    public DeliveryRecordStaRep sta(DeliveryRecordStaReq params) {
        String ym = params.getDate();
        int days;
        try {
            LocalDate ld = DateUtil.parse(ym + "-01", null);
            days = ld.lengthOfMonth();
        } catch (Exception e) {
            throw new CavException("日期格式不正常");
        }
        List<DeliveryRecordSta> list = recordMapper.sta(ym);
        List<DeliveryRecordInfos> results = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            int finalDays = days;
            list.forEach(i -> {
                DeliveryRecordInfos info = new DeliveryRecordInfos();
                info.setItemNo(i.getItemNo());
                List<BigDecimal> bds = toList(i.getList(), finalDays);
                info.setCounts(bds);
                results.add(info);
            });
        }
        DeliveryRecordStaRep response = new DeliveryRecordStaRep();
        response.setDays(days);
        response.setList(results);
        return response;
    }

    @Override
    public BaseResponse<List<DeliveryRecordPageRep>> pageList(BaseRequest<DeliveryRecordPageReq> request) {
        PageUtil<DeliveryRecordPageRep, DeliveryRecordPageReq> pu = (p, q) -> recordMapper.deliveryRecordPage(p, q);
        DeliveryRecordPageReq params = MsgUtil.params(request);
        return pu.page(request.getPage(), params);
    }

    @Override
    public void export(DeliveryRecordPageReq params, HttpServletResponse response) {
        boolean paramEmpty = StringUtils.isAllBlank(params.getCustName(), params.getSendAcctName());
        if (paramEmpty && StringUtils.isAnyEmpty(params.getStartDate(), params.getEndDate())) {
            throw new CavException("请设置查询条件！");
        }
        List<DeliveryRecordPageRep> list = recordMapper.deliveryRecordList(params);
        if (CollectionUtils.isNotEmpty(list) && list.size() > 5000) {
            throw new CavException("导出数据过多，请重新筛选！");
        }
        ExcelUtil.writeExcel(response, "发货单明细", w -> {
            w.renameSheet(0, "发货单明细");
            writeRecordData(w, list);
        });
    }


    private void writeRecordData(ExcelWriter writer, List<DeliveryRecordPageRep> list) {
        writer.addHeaderAlias("sendAcctName", "发货人");
        writer.addHeaderAlias("sendTime", "发货日期");
        writer.addHeaderAlias("bomNo", "图纸号");
        writer.addHeaderAlias("custName", "接收客户");
        writer.addHeaderAlias("sendCount", "发货数量");
        writer.write(list, true);
    }

    private List<BigDecimal> toList(List<DeliveryRecordStaSusb> subs, int days) {
        List<BigDecimal> list = new ArrayList<>();
        Map<Integer, BigDecimal> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(subs)) {
            subs.forEach(s -> map.put(s.getMm(), s.getCounts()));
        }
        for (int i = 0; i < days; i++) {
            list.add(MapUtils.getObject(map, i + 1, BigDecimal.ZERO));
        }
        return list;
    }
}

package cn.jb.boot.biz.tray.service.impl;

import cn.jb.boot.biz.tray.entity.TrayManageInfo;
import cn.jb.boot.biz.tray.mapper.TrayManageInfoMapper;
import cn.jb.boot.biz.tray.service.TrayManageInfoService;
import cn.jb.boot.biz.tray.vo.request.TrayManageInfoRequest;
import cn.jb.boot.biz.tray.vo.response.TrayManageInfoResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.com.response.RequestResponseTool;
import cn.jb.boot.util.DateUtil;
import cn.jb.boot.util.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mortbay.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 托盘信息
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-11 20:04:08
 */
@Service
public class TrayManageInfoServiceImpl extends ServiceImpl<TrayManageInfoMapper, TrayManageInfo> implements TrayManageInfoService {

    @Autowired
    TrayManageInfoMapper mapper;

    @Override
    public void createInfo(TrayManageInfoRequest params) {
        if (params.getId().isEmpty()) {
            //获取32位随机数作为主键
            UUID uuid = UUID.randomUUID();
            String randomCode = uuid.toString().replace("-", "");
            params.setId(randomCode.substring(0, 32));
            //根据物料名称获取物料编码
            Map<String, Object> item = mapper.getItemNo(params);
            params.setItemno(item.get("item_no").toString());
            params.setStatus("1");
            mapper.add(params);
        } else {
            params.setStatus("1");
            mapper.upproduce(params);
        }
    }

    @Override
    public void delete(TrayManageInfoRequest params) {
        if (!params.getId().isEmpty()) {
            mapper.delete(params);
        }
    }

    @Override
    public void createAllInfo(List<TrayManageInfoRequest> paramsList) {
        for (TrayManageInfoRequest params : paramsList) {
            createInfo(params);
        }
    }

    @Override
    public BaseResponse<List<TrayManageInfoResponse>> pageInfo(Paging page, TrayManageInfoRequest params) {
        PageUtil<TrayManageInfoResponse, TrayManageInfoRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    public void getPdaData(Map<String, Object> params) {
        //获取32位随机数作为主键
        UUID uuid = UUID.randomUUID();
        String randomCode = uuid.toString().replace("-", "");
        params.put("id", randomCode.substring(0, 32));
        params.put("status", "0");
        //获取当前时间
        params.put("updated_time", DateUtil.nowDateTime());
        //根据库位和工位获取设备名称
        Map<String, Object> deviceparam = new HashMap<>();
        String station = params.get("station").toString(); //工位
        deviceparam.put("station", station);
        String locationId = params.get("locationId").toString(); //库位
        deviceparam.put("locationId", locationId);
        Map<String, Object> resultData = mapper.getDevice(deviceparam);
        String device = resultData.get("device").toString();
        params.put("deviceName", device);
        mapper.addPdaData(params);
    }

    @Override
    public String getItem(Map<String, Object> params) {
        String result = "";
        try {
            Map<String, Object> newparam = (Map) params.get("params");
            List<Map<String, Object>> itemDate = mapper.getItem(newparam);
            result = JSON.toString(itemDate);
            result = RequestResponseTool.getJsonMessage("00", "处理成功", result);
        } catch (Exception e) {
            RequestResponseTool.getJsonMessage("00", "处理失败");
            e.printStackTrace();
        }
        return result;
    }
}

package cn.jb.boot.biz.device.service.impl;

import cn.jb.boot.biz.device.entity.DeviceInfo;
import cn.jb.boot.biz.device.mapper.DeviceInfoMapper;
import cn.jb.boot.biz.device.service.DeviceInfoService;
import cn.jb.boot.biz.device.vo.request.DeviceInfoCreateRequest;
import cn.jb.boot.biz.device.vo.request.DeviceInfoPageRequest;
import cn.jb.boot.biz.device.vo.request.DeviceInfoUpdateRequest;
import cn.jb.boot.biz.device.vo.response.DeviceInfoInfoResponse;
import cn.jb.boot.biz.device.vo.response.DeviceInfoPageResponse;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.enums.ValidEnum;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.response.DictListResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备信息 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 04:46:16
 */
@Service
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfo> implements DeviceInfoService {
    @Resource
    private DeviceInfoMapper mapper;
    @Override
    public void createInfo(DeviceInfoCreateRequest params) {
        DeviceInfo entity = PojoUtil.copyBean(params, DeviceInfo.class);
        entity.setDataStatus(Constants.STATUS_00);
        this.checkDeviceNo(entity.getDeviceNo(), null);
        this.save(entity);
    }
    private void checkDeviceNo(String deviceNo, String id) {
        DeviceInfo one = this.getOne(new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getDeviceNo, deviceNo));
        if (StringUtils.isEmpty(id)) {
            if (Objects.nonNull(one)) {
                throw new CavException("设备编码已存在");
            }
        } else {
            if (!id.equals(one.getId())) {
                throw new CavException("设备编码已存在");
            }
        }

    }
    @Override
    public DeviceInfoInfoResponse getInfoById(String id) {
        DeviceInfo entity = this.getById(id);
        return PojoUtil.copyBean(entity, DeviceInfoInfoResponse.class);
    }
    @Override
    public void updateInfo(DeviceInfoUpdateRequest params) {
        DeviceInfo entity = PojoUtil.copyBean(params, DeviceInfo.class);
        this.checkDeviceNo(entity.getDeviceNo(), entity.getId());
        this.updateById(entity);
    }
    @Override
    public BaseResponse<List<DeviceInfoPageResponse>> pageInfo(Paging page, DeviceInfoPageRequest params) {
        PageUtil<DeviceInfoPageResponse, DeviceInfoPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }
    @Override
    public void delete(String id) {
        DeviceInfo di = this.getById(id);
        di.setIsValid(ValidEnum.INVALID.getCode());
        this.updateById(di);
    }
    @Override
    public List<DictListResponse> deviceSelect() {
        //new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getIsValid,ValidEnum.VALID.getCode())
        List<DeviceInfo> list = this.list();
        return list.stream().map(d -> {
            DictListResponse response = new DictListResponse();
            response.setName(d.getDeviceName());
            response.setCode(d.getId());
            return response;
        }).collect(Collectors.toList());
    }
    @Override
    public Map<String,Object> deviceTypeCount(){
        Map<String,Object> result = new HashMap<>();
        List<Map<String,Object>> deviceList = mapper.deviceTypeCount();
        String[] types = new String[deviceList.size()];
        Integer[] counts = new Integer[deviceList.size()];
        int i = 0;
        for(Map<String,Object> deviceCount:deviceList){
            String typeName = deviceCount.get("typeName").toString();
            String count = deviceCount.get("count").toString();
            types[i] = typeName;
            counts[i] = Integer.valueOf(count);
            i++;
        }
        result.put("type",types);
        result.put("count",counts);
        return  result;
    }
}

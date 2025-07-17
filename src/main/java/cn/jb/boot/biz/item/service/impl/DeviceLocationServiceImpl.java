package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.DeviceLocationInfo;
import cn.jb.boot.biz.item.mapper.DeviceLocationMapper;
import cn.jb.boot.biz.item.service.DeviceLocationService;
import cn.jb.boot.biz.item.vo.request.DeviceLocationCreateRequest;
import cn.jb.boot.biz.item.vo.response.DeviceLocationPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库位信息 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-02 00:44:38
 */
@Service
public class DeviceLocationServiceImpl extends ServiceImpl<DeviceLocationMapper, DeviceLocationInfo> implements DeviceLocationService {

    @Resource
    private DeviceLocationMapper mapper;

    @Override
    public void createInfo(DeviceLocationCreateRequest params) {
        DeviceLocationInfo entity = PojoUtil.copyBean(params, DeviceLocationInfo.class);
        this.save(entity);
    }

    @Override
    public DeviceLocationPageResponse getInfoById(String id) {
        DeviceLocationInfo entity = this.getById(id);
        return PojoUtil.copyBean(entity, DeviceLocationPageResponse.class);
    }

    @Override
    public void updateInfo(DeviceLocationCreateRequest params) {
        DeviceLocationInfo entity = PojoUtil.copyBean(params, DeviceLocationInfo.class);
        this.updateById(entity);
    }

    @Override
    public BaseResponse<List<DeviceLocationPageResponse>> pageInfo(Paging page, DeviceLocationCreateRequest params) {
        PageUtil<DeviceLocationPageResponse, DeviceLocationCreateRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    public Map<String, Object> selectAGVLocation(Map<String, Object> params) {
        Map<String, Object> agvLocaion = mapper.selectAGVLocation(params);
        return agvLocaion;
    }
}

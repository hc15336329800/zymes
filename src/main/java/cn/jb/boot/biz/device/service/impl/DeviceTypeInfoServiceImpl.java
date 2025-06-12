package cn.jb.boot.biz.device.service.impl;

import cn.jb.boot.biz.device.entity.DeviceInfo;
import cn.jb.boot.biz.device.entity.DeviceTypeInfo;
import cn.jb.boot.biz.device.mapper.DeviceInfoMapper;
import cn.jb.boot.biz.device.mapper.DeviceTypeInfoMapper;
import cn.jb.boot.biz.device.service.DeviceTypeInfoService;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoCreateRequest;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoPageRequest;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoUpdateRequest;
import cn.jb.boot.biz.device.vo.response.DeviceTypeInfoInfoResponse;
import cn.jb.boot.biz.device.vo.response.DeviceTypeInfoPageResponse;
import cn.jb.boot.biz.device.vo.response.DeviceTypeListResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.enums.ValidEnum;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.response.DictListResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备类型信息 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-31 03:22:39
 */
@Service
public class DeviceTypeInfoServiceImpl extends ServiceImpl<DeviceTypeInfoMapper, DeviceTypeInfo> implements DeviceTypeInfoService {

    @Resource
    private DeviceTypeInfoMapper mapper;
    @Resource
    private DeviceInfoMapper deviceInfoMapper;

    @Override
    public void createInfo(DeviceTypeInfoCreateRequest params) {
        DeviceTypeInfo entity = PojoUtil.copyBean(params, DeviceTypeInfo.class);
        this.save(entity);
    }

    @Override
    public DeviceTypeInfoInfoResponse getInfoById(String id) {
        DeviceTypeInfo entity = this.getById(id);
        return PojoUtil.copyBean(entity, DeviceTypeInfoInfoResponse.class);
    }

    @Override
    public void updateInfo(DeviceTypeInfoUpdateRequest params) {
        DeviceTypeInfo entity = PojoUtil.copyBean(params, DeviceTypeInfo.class);
        this.updateById(entity);
    }

    @Override
    public BaseResponse<List<DeviceTypeInfoPageResponse>> pageInfo(Paging page, DeviceTypeInfoPageRequest params) {
        PageUtil<DeviceTypeInfoPageResponse, DeviceTypeInfoPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    public List<DictListResponse> listAllType() {
        List<DeviceTypeInfo> list = this.list();
        return list.stream().map(d -> {
            DictListResponse dict = new DictListResponse();
            dict.setCode(d.getId());
            dict.setName(d.getName());
            return dict;
        }).collect(Collectors.toList());

    }

    @Override
    public void delete(String id) {
        List<DeviceInfo> list = deviceInfoMapper.selectList(new LambdaQueryWrapper<DeviceInfo>()
                .eq(DeviceInfo::getTypeId, id).eq(DeviceInfo::getIsValid, ValidEnum.VALID.getCode()));
        if (CollectionUtils.isNotEmpty(list)) {
            throw new CavException("该类型下存在设备，请先删除设备!");
        }
        this.removeById(id);
    }
}

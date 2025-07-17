package cn.jb.boot.biz.device.service.impl;

import cn.jb.boot.biz.device.entity.CheckInfo;
import cn.jb.boot.biz.device.mapper.CheckInfoMapper;
import cn.jb.boot.biz.device.service.CheckInfoService;
import cn.jb.boot.biz.device.vo.request.CheckInfoCreateRequest;
import cn.jb.boot.biz.device.vo.request.CheckInfoPageRequest;
import cn.jb.boot.biz.device.vo.request.CheckInfoUpdateRequest;
import cn.jb.boot.biz.device.vo.response.CheckInfoInfoResponse;
import cn.jb.boot.biz.device.vo.response.CheckInfoPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 点检信息 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@Service
public class CheckInfoServiceImpl extends ServiceImpl<CheckInfoMapper, CheckInfo> implements CheckInfoService {

    @Resource
    private CheckInfoMapper mapper;

    @Override
    public void createInfo(CheckInfoCreateRequest params) {
        CheckInfo entity = PojoUtil.copyBean(params, CheckInfo.class);
        this.save(entity);
    }

    @Override
    public CheckInfoInfoResponse getInfoById(String id) {
        CheckInfo entity = this.getById(id);
        return PojoUtil.copyBean(entity, CheckInfoInfoResponse.class);
    }

    @Override
    public void updateInfo(CheckInfoUpdateRequest params) {
        CheckInfo entity = PojoUtil.copyBean(params, CheckInfo.class);
        this.updateById(entity);
    }

    @Override
    public BaseResponse<List<CheckInfoPageResponse>> pageInfo(Paging page, CheckInfoPageRequest params) {
        PageUtil<CheckInfoPageResponse, CheckInfoPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }
}

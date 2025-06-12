package cn.jb.boot.biz.depository.service.impl;

import cn.jb.boot.biz.depository.entity.OutStoreMid;
import cn.jb.boot.biz.depository.mapper.OutStoreMidMapper;
import cn.jb.boot.biz.depository.service.OutStoreMidService;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidCreateRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidPageRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidUpdateRequest;
import cn.jb.boot.biz.depository.vo.response.OutStoreMidInfoResponse;
import cn.jb.boot.biz.depository.vo.response.OutStoreMidPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 中间件使用表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Service
public class OutStoreMidServiceImpl extends ServiceImpl<OutStoreMidMapper, OutStoreMid> implements OutStoreMidService {

    @Resource
    private OutStoreMidMapper mapper;

    @Override
    public void createInfo(OutStoreMidCreateRequest params) {
        OutStoreMid entity = PojoUtil.copyBean(params, OutStoreMid.class);
        this.save(entity);
    }

    @Override
    public OutStoreMidInfoResponse getInfoById(String id) {
        OutStoreMid entity = this.getById(id);
        return PojoUtil.copyBean(entity, OutStoreMidInfoResponse.class);
    }

    @Override
    public void updateInfo(OutStoreMidUpdateRequest params) {
        OutStoreMid entity = PojoUtil.copyBean(params, OutStoreMid.class);
        this.updateById(entity);
    }

    @Override
    public BaseResponse<List<OutStoreMidPageResponse>> pageInfo(Paging page, OutStoreMidPageRequest params) {
        PageUtil<OutStoreMidPageResponse, OutStoreMidPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }
}

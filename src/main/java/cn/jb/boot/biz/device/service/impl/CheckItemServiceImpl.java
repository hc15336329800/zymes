package cn.jb.boot.biz.device.service.impl;

import cn.jb.boot.biz.device.entity.CheckItem;
import cn.jb.boot.biz.device.mapper.CheckItemMapper;
import cn.jb.boot.biz.device.service.CheckItemService;
import cn.jb.boot.biz.device.vo.request.CheckItemCreateRequest;
import cn.jb.boot.biz.device.vo.request.CheckItemPageRequest;
import cn.jb.boot.biz.device.vo.request.CheckItemUpdateRequest;
import cn.jb.boot.biz.device.vo.response.CheckItemInfoResponse;
import cn.jb.boot.biz.device.vo.response.CheckItemPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 质检项目 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@Service
public class CheckItemServiceImpl extends ServiceImpl<CheckItemMapper, CheckItem> implements CheckItemService {

    @Resource
    private CheckItemMapper mapper;

    @Override
    public void createInfo(CheckItemCreateRequest params) {
        CheckItem entity = PojoUtil.copyBean(params, CheckItem.class);
        this.save(entity);
    }

    @Override
    public CheckItemInfoResponse getInfoById(String id) {
        CheckItem entity = this.getById(id);
        return PojoUtil.copyBean(entity, CheckItemInfoResponse.class);
    }

    @Override
    public void updateInfo(CheckItemUpdateRequest params) {
        CheckItem entity = PojoUtil.copyBean(params, CheckItem.class);
        this.updateById(entity);
    }

    @Override
    public BaseResponse<List<CheckItemPageResponse>> pageInfo(Paging page, CheckItemPageRequest params) {
        PageUtil<CheckItemPageResponse, CheckItemPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }
}

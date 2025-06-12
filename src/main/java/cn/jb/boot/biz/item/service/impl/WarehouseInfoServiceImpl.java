package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.WarehouseInfo;
import cn.jb.boot.biz.item.mapper.WarehouseInfoMapper;
import cn.jb.boot.biz.item.service.WarehouseInfoService;
import cn.jb.boot.biz.item.vo.request.WarehouseInfoCreateRequest;
import cn.jb.boot.biz.item.vo.request.WarehouseInfoPageRequest;
import cn.jb.boot.biz.item.vo.request.WarehouseInfoUpdateRequest;
import cn.jb.boot.biz.item.vo.response.WarehouseInfoInfoResponse;
import cn.jb.boot.biz.item.vo.response.WarehouseInfoPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.response.DictListResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库位信息 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-02 00:44:38
 */
@Service
public class WarehouseInfoServiceImpl extends ServiceImpl<WarehouseInfoMapper, WarehouseInfo> implements WarehouseInfoService {

    @Resource
    private WarehouseInfoMapper mapper;

    @Override
    public void createInfo(WarehouseInfoCreateRequest params) {
        WarehouseInfo entity = PojoUtil.copyBean(params, WarehouseInfo.class);
        this.save(entity);
    }

    @Override
    public WarehouseInfoInfoResponse getInfoById(String id) {
        WarehouseInfo entity = this.getById(id);
        return PojoUtil.copyBean(entity, WarehouseInfoInfoResponse.class);
    }

    @Override
    public void updateInfo(WarehouseInfoUpdateRequest params) {
        WarehouseInfo entity = PojoUtil.copyBean(params, WarehouseInfo.class);
        this.updateById(entity);
    }

    @Override
    public BaseResponse<List<WarehouseInfoPageResponse>> pageInfo(Paging page, WarehouseInfoPageRequest params) {
        PageUtil<WarehouseInfoPageResponse, WarehouseInfoPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    public List<DictListResponse> selected() {
        List<DictDataVo> list = mapper.selected();
        return list.stream().map(d -> {
            DictListResponse response = new DictListResponse();
            response.setCode(d.getDictValue());
            response.setName(d.getDictLabel());
            return response;
        }).collect(Collectors.toList());
    }
}

package cn.jb.boot.biz.group.service.impl;

import cn.jb.boot.biz.group.entity.GroupDtl;
import cn.jb.boot.biz.group.enums.LeaderType;
import cn.jb.boot.biz.group.mapper.GroupDtlMapper;
import cn.jb.boot.biz.group.service.GroupDtlService;
import cn.jb.boot.biz.group.vo.request.GroupDtlCreateRequest;
import cn.jb.boot.biz.group.vo.request.GroupDtlPageRequest;
import cn.jb.boot.biz.group.vo.request.GroupDtlUpdateRequest;
import cn.jb.boot.biz.group.vo.response.GroupDtlInfoResponse;
import cn.jb.boot.biz.group.vo.response.GroupDtlPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分组明细 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Service
public class GroupDtlServiceImpl extends ServiceImpl<GroupDtlMapper, GroupDtl> implements GroupDtlService {

    @Resource
    private GroupDtlMapper mapper;

    @Override
    public void createInfo(GroupDtlCreateRequest params) {
        GroupDtl entity = PojoUtil.copyBean(params, GroupDtl.class);
        entity.setLeaderType(LeaderType.WORKER.getCode());
        this.save(entity);
    }

    @Override
    public GroupDtlInfoResponse getInfoById(String id) {
        GroupDtl entity = this.getById(id);
        return PojoUtil.copyBean(entity, GroupDtlInfoResponse.class);
    }

    @Override
    public void updateInfo(GroupDtlUpdateRequest params) {
        GroupDtl entity = PojoUtil.copyBean(params, GroupDtl.class);
        this.updateById(entity);
    }

    @Override
    public BaseResponse<List<GroupDtlPageResponse>> pageInfo(Paging page, GroupDtlPageRequest params) {
        PageUtil<GroupDtlPageResponse, GroupDtlPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }
}

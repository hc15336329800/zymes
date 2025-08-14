package cn.jb.boot.biz.group.service.impl;

import cn.jb.boot.biz.group.entity.GroupDtl;
import cn.jb.boot.biz.group.entity.GroupInfo;
import cn.jb.boot.biz.group.enums.LeaderType;
import cn.jb.boot.biz.group.mapper.GroupDtlMapper;
import cn.jb.boot.biz.group.mapper.GroupInfoMapper;
import cn.jb.boot.biz.group.service.GroupInfoService;
import cn.jb.boot.biz.group.vo.request.GroupInfoCreateRequest;
import cn.jb.boot.biz.group.vo.request.GroupInfoPageRequest;
import cn.jb.boot.biz.group.vo.request.GroupInfoUpdateRequest;
import cn.jb.boot.biz.group.vo.response.GroupInfoInfoResponse;
import cn.jb.boot.biz.group.vo.response.GroupInfoPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.SnowFlake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 工人分组 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Service
public class GroupInfoServiceImpl extends ServiceImpl<GroupInfoMapper, GroupInfo> implements GroupInfoService {

    @Resource
    private GroupInfoMapper mapper;
    @Resource
    private GroupDtlMapper groupDtlMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createInfo(GroupInfoCreateRequest params) {
        GroupInfo entity = PojoUtil.copyBean(params, GroupInfo.class);
        entity.setId(SnowFlake.genId());
        this.save(entity);
        GroupDtl dtl = new GroupDtl();
        dtl.setGroupId(entity.getId());
        dtl.setUserId(params.getGroupUid());
        dtl.setPercentage(BigDecimal.ZERO);
        dtl.setLeaderType(LeaderType.LEADER.getCode());
        groupDtlMapper.insert(dtl);
    }

    @Override
    public GroupInfoInfoResponse getInfoById(String id) {
        GroupInfo entity = this.getById(id);
        return PojoUtil.copyBean(entity, GroupInfoInfoResponse.class);
    }

//    @Override
//    @Transactional(rollbackFor = Throwable.class)
//    public void updateInfo(GroupInfoUpdateRequest params) {
//        GroupInfo db = this.getById(params.getId());
//        GroupInfo entity = PojoUtil.copyBean(params, GroupInfo.class);
//        if (!db.getGroupUid().equals(entity.getGroupUid())) {
//            GroupDtl dtl = groupDtlMapper.selectOne(new LambdaQueryWrapper<GroupDtl>().eq(GroupDtl::getGroupId, db.getId())
//                    .eq(GroupDtl::getUserId, db.getGroupUid()));
//            dtl.setLeaderType(LeaderType.WORKER.getCode());
//            groupDtlMapper.updateById(dtl);
//            GroupDtl dtl2 = groupDtlMapper.selectOne(new LambdaQueryWrapper<GroupDtl>().eq(GroupDtl::getGroupId, db.getId())
//                    .eq(GroupDtl::getUserId, params.getGroupUid()));
//            if (Objects.isNull(dtl2)) {
//                dtl = new GroupDtl();
//                dtl.setGroupId(entity.getId());
//                dtl.setUserId(params.getGroupUid());
//                dtl.setPercentage(BigDecimal.ZERO);
//                dtl.setLeaderType(LeaderType.LEADER.getCode());
//                groupDtlMapper.insert(dtl);
//            } else {
//                dtl.setLeaderType(LeaderType.LEADER.getCode());
//                groupDtlMapper.updateById(dtl);
//            }
//        }
//        this.updateById(entity);
//
//    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateInfo(GroupInfoUpdateRequest params) {
        GroupInfo db = this.getById(params.getId());
        GroupInfo entity = PojoUtil.copyBean(params, GroupInfo.class);

        if (!Objects.equals(db.getGroupUid(), entity.getGroupUid())) {
            // 查询现有组长明细
            GroupDtl leaderDtl = groupDtlMapper.selectOne(new LambdaQueryWrapper<GroupDtl>()
                    .eq(GroupDtl::getGroupId, db.getId())
                    .eq(GroupDtl::getLeaderType, LeaderType.LEADER.getCode()));

            if (leaderDtl != null) {
                // 直接替换组长的 userId
                leaderDtl.setUserId(entity.getGroupUid());
                groupDtlMapper.updateById(leaderDtl);
            } else {
                // 若未找到组长记录，则新增一条
                leaderDtl = new GroupDtl();
                leaderDtl.setGroupId(db.getId());
                leaderDtl.setUserId(entity.getGroupUid());
                leaderDtl.setPercentage(BigDecimal.ZERO);
                leaderDtl.setLeaderType(LeaderType.LEADER.getCode());
                groupDtlMapper.insert(leaderDtl);
            }

            // 删除新组长作为普通成员的记录，避免重复
            groupDtlMapper.delete(new LambdaQueryWrapper<GroupDtl>()
                    .eq(GroupDtl::getGroupId, db.getId())
                    .eq(GroupDtl::getUserId, entity.getGroupUid())
                    .eq(GroupDtl::getLeaderType, LeaderType.WORKER.getCode()));
        }

        this.updateById(entity);
    }

    @Override
    public BaseResponse<List<GroupInfoPageResponse>> pageInfo(Paging page, GroupInfoPageRequest params) {
        PageUtil<GroupInfoPageResponse, GroupInfoPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
        return pu.page(page, params);
    }

    @Override
    public List<DictListResponse> selected() {
        return mapper.selected();
    }
}

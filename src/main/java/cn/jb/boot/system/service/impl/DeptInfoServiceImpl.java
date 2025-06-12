package cn.jb.boot.system.service.impl;

import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.response.DeptInfoDetailResponse;
import cn.jb.boot.system.vo.response.DeptTreeSelectResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
import org.apache.commons.lang3.StringUtils;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.system.entity.DeptInfo;
import cn.jb.boot.system.entity.UserInfo;
import cn.jb.boot.system.mapper.DeptInfoMapper;
import cn.jb.boot.system.service.DeptInfoService;
import cn.jb.boot.system.service.UserInfoService;
import cn.jb.boot.system.vo.request.DeptInfoCreateRequest;
import cn.jb.boot.system.vo.request.DeptInfoUpdateRequest;
import cn.jb.boot.framework.com.entity.TreeSelect;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部门信息 服务实现类
 *
 * @author xieyubin
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-09-06 11:22:14
 */
@Service
public class DeptInfoServiceImpl extends ServiceImpl<DeptInfoMapper, DeptInfo> implements DeptInfoService {

    @Resource
    private DeptInfoMapper mapper;
    @Resource
    private UserInfoService userInfoService;


    @Override
    public void createInfo(DeptInfoCreateRequest params) {
        DeptInfo entity = PojoUtil.copyBean(params, DeptInfo.class);
        if (StringUtils.isBlank(params.getPaterId())) {
            entity.setPaterId(Constants.DICT_ROOT);
        }
        this.save(entity);
    }


    @Override
    public void updateInfo(DeptInfoUpdateRequest params) {
        DeptInfo entity = PojoUtil.copyBean(params, DeptInfo.class);
        this.updateById(entity);
    }

    @Override
    public void deleteDept(String deptId) {
        deptInfo(deptId);
        List<DeptTreeSelectResponse> list = listByPaterId(deptId, false);
        if (CollectionUtils.isNotEmpty(list)) {
            throw new CavException("部门存在子部门！");
        }
        List<UserInfo> users = userInfoService.list(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getDeptId, deptId));
        if (CollectionUtils.isNotEmpty(users)) {
            throw new CavException("部门下存在员工，请先处理！");
        }
        this.removeById(deptId);
    }


    @Override
    public List<DeptTreeSelectResponse> listByPaterId(String paterId, boolean tree) {
        List<DeptTreeSelectResponse> list = mapper.listByPaterId(paterId);
        if (tree && CollectionUtils.isNotEmpty(list)) {
            List<DeptTreeSelectResponse> deptTree = PojoUtil.toTree(list, paterId);
            checked(deptTree);
            return deptTree;
        }
        return null;
    }

    private DeptInfo deptInfo(String deptId) {
        DeptInfo info = this.getById(deptId);
        if (info == null) {
            throw new CavException("未找到对应的部门信息！");
        }
        return info;
    }

    @Override
    public List<String> subDeptId(String paterId) {
        return mapper.subDeptId(paterId);
    }


    @Override
    public String topDeptId(String deptId) {
        return mapper.topDeptId(deptId);
    }

    @Override
    public List<DeptTreeSelectResponse> deptTree(String paterId) {
        if (StringUtils.isEmpty(paterId)) {
            paterId = Constants.DICT_ROOT;
        }

        return listByPaterId(paterId, true);
    }

    @Override
    public DeptInfoDetailResponse detail(ComId params) {
        DeptInfo deptInfo = this.getById(params.getId());

        return PojoUtil.copyBean(deptInfo, DeptInfoDetailResponse.class);
    }


    private void checked(List<? extends TreeSelect> tree) {
        if (CollectionUtils.isNotEmpty(tree)) {
            for (TreeSelect t : tree) {
                if (!t.isChecked() && CollectionUtils.isNotEmpty(t.getChildren())) {
                    t.setChecked(true);
                }
                checked(t.getChildren());
            }
        }
    }
}

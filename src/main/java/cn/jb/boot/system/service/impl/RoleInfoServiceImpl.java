package cn.jb.boot.system.service.impl;


import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.system.entity.MenuInfo;
import cn.jb.boot.system.entity.RoleInfo;
import cn.jb.boot.system.entity.RoleMenu;
import cn.jb.boot.system.entity.UserRole;
import cn.jb.boot.system.mapper.RoleInfoMapper;
import cn.jb.boot.system.service.MenuInfoService;
import cn.jb.boot.system.service.RoleInfoService;
import cn.jb.boot.system.service.RoleMenuService;
import cn.jb.boot.system.service.UserRoleService;
import cn.jb.boot.system.vo.request.RoleInfoCreateRequest;
import cn.jb.boot.system.vo.request.RoleInfoPageRequest;
import cn.jb.boot.system.vo.request.RoleInfoRequest;
import cn.jb.boot.system.vo.request.RoleInfoUpdateRequest;
import cn.jb.boot.system.vo.request.RoleMenuAddRequest;
import cn.jb.boot.system.vo.request.RoleMenuQueryRequest;
import cn.jb.boot.system.vo.response.RoleInfoPageResponse;
import cn.jb.boot.system.vo.response.RoleInfoResponse;
import cn.jb.boot.system.vo.response.UserRoleResponse;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collectors;

/**
 * 角色信息表 服务实现类
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:45
 */
@Service
@Slf4j
public class RoleInfoServiceImpl extends ServiceImpl<RoleInfoMapper, RoleInfo> implements RoleInfoService {

    @Resource
    private RoleInfoMapper mapper;

    @Resource
    private RoleMenuService roleMenuService;

    @Resource
    private MenuInfoService menuInfoService;

    @Resource
    private UserRoleService userRoleService;
    /**
     * 角色编码前缀
     */
    private static final String ROLE_CODE_PRE = "JS";

    @Override
    public List<String> roleCheckedMenu(RoleMenuQueryRequest params) {
        return roleMenuService.roleCheckedMenu(params);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleMenu(RoleMenuAddRequest params) {
        String roleCode = params.getRoleCode();
        String channel = params.getChannel();
        roleMenuService.deleteResource(roleCode, channel);
        List<String> menuCodes = params.getMenuCodeList();
        if (CollectionUtils.isNotEmpty(menuCodes)) {
            List<RoleMenu> refList = menuCodes.stream().map(obj -> {
                RoleMenu ref = new RoleMenu();
                LambdaQueryWrapper<MenuInfo> qw = new LambdaQueryWrapper<>();
                qw.eq(MenuInfo::getMenuCode, obj);
                List<MenuInfo> menuInfo = menuInfoService.list(qw);
                if (CollectionUtils.isEmpty(menuInfo) || menuInfo.size() != 1) {
                    throw new CavException("菜单资源不存在！");
                }
                ref.setRoleCode(roleCode);
                ref.setResourceCode(obj);
                ref.setDataValid(Constants.STATUS_COMMON);
                ref.setResourceType(menuInfo.get(0).getMenuType());
                return ref;
            }).collect(Collectors.toList());
            roleMenuService.saveBatch(refList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleInfoCreateRequest request) {
        RoleInfo roleInf = PojoUtil.copyBean(request, RoleInfo.class);
        String roleCode = generateCode();
        roleInf.setRoleCode(roleCode);
        this.save(roleInf);
    }

    @Override
    public void updateRole(RoleInfoUpdateRequest params) {
        RoleInfo roleInf = PojoUtil.copyBean(params, RoleInfo.class);
        roleInf.setRoleDesc(StringUtils.trimToEmpty(roleInf.getRoleDesc()));
        this.updateById(roleInf);
    }

    @Override
    public void deleteRole(String roleId) {
        RoleInfo roleInf = detailInfo(roleId);
        // 系统角色不可以删除
        if (Constants.STATUS_01.equals(roleInf.getSysRole())) {
            throw new CavException("系统角色不可以删除！");
        }
        this.removeById(roleInf.getId());
        roleMenuService.deleteResource(roleInf.getRoleCode(), null);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getRoleCode, roleInf.getRoleCode());
        userRoleService.remove(wrapper);
    }

    @Override
    public RoleInfoResponse detail(String roleId) {
        RoleInfo roleInf = detailInfo(roleId);
        return PojoUtil.copyBean(roleInf, RoleInfoResponse.class);
    }

    @Override
    public BaseResponse<List<RoleInfoPageResponse>> pageList(BaseRequest<RoleInfoPageRequest> request) {
        String uid = UserUtil.uid();
        RoleInfoPageRequest params = MsgUtil.params(request);
        params.setUserId(uid);
        params.setDataRole(UserUtil.userDataRole());
        PageUtil<RoleInfoPageResponse, RoleInfoPageRequest> pu = (p, q) -> mapper.userRolePage(p, q);
        return pu.page(request.getPage(), params);
    }

    @Override
    public List<UserRoleResponse> userRoles() {
        RoleInfoPageRequest params = new RoleInfoPageRequest();
        params.setDataRole(UserUtil.userDataRole());
        params.setUserId(UserUtil.uid());
        return mapper.userRoles(params);
    }


    private RoleInfo detailInfo(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            throw new CavException("请传入角色id！");
        }
        RoleInfo roleInf = this.getById(roleId);
        if (roleInf == null) {
            throw new CavException("角色不存在！");
        }
        return roleInf;
    }

    /**
     * 生成角色编码,按照数据库最大的角色编码+1,编码最多1000个(三位数)
     *
     * @return
     */
    private String generateCode() {
        LambdaQueryWrapper<RoleInfo> wrapper = new LambdaQueryWrapper<>();
        List<RoleInfo> list = this.list(wrapper);
        OptionalLong optionalLong = list.stream().mapToLong(obj -> {
            String roleCode = obj.getRoleCode();
            String[] split = roleCode.split(ROLE_CODE_PRE);
            return Long.parseLong(split[1]);
        }).max();
        Long maxSeq = optionalLong.isPresent() ? optionalLong.getAsLong() + 1 : 1;
        return String.format(ROLE_CODE_PRE + "%09d", maxSeq);
    }
}

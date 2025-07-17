package cn.jb.boot.system.service.impl;

import cn.hutool.json.JSONUtil;
import cn.jb.boot.framework.com.Constants;
import cn.jb.boot.framework.com.cache.TokenCache;
import cn.jb.boot.framework.com.entity.TokenCacheObj;
import cn.jb.boot.framework.com.entity.TreeSelect;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.enums.JbEnum;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.system.entity.UserInfo;
import cn.jb.boot.system.mapper.UserInfoMapper;
import cn.jb.boot.system.service.DeptInfoService;
import cn.jb.boot.system.service.UserInfoService;
import cn.jb.boot.system.service.UserRoleService;
import cn.jb.boot.system.vo.request.LoginPasswordRequest;
import cn.jb.boot.system.vo.request.UserInfoCreateRequest;
import cn.jb.boot.system.vo.request.UserInfoPageRequest;
import cn.jb.boot.system.vo.request.UserInfoPageResponse;
import cn.jb.boot.system.vo.request.UserInfoUpdatePasswordRequest;
import cn.jb.boot.system.vo.request.UserInfoUpdateRequest;
import cn.jb.boot.system.vo.response.DictListResponse;
import cn.jb.boot.system.vo.response.LoginResponse;
import cn.jb.boot.system.vo.response.UserInfoDetailResponse;
import cn.jb.boot.system.vo.response.UserSelectResponse;
import cn.jb.boot.util.MsgUtil;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import cn.jb.boot.util.StringUtil;
import cn.jb.boot.util.UserUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserRoleService userRoleService;
    @Resource
    private UserInfoMapper mapper;


    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();


    @Override
    public LoginResponse login(LoginPasswordRequest params, HttpServletResponse response) {
        String um = StringUtils.trimToEmpty(params.getUserName());
        String pwd = StringUtils.trimToEmpty(params.getPassword());
        UserInfo ui = this.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserName, um));
        if (Objects.isNull(ui)) {
            throw new CavException("用户不存在！");
        }
        checkPassword(ui, pwd);
        cacheToken(response, ui);
        return PojoUtil.copyBean(ui, LoginResponse.class);
    }

    @Override
    public UserInfoDetailResponse userInfoDetail(String uid) {
        UserInfo info = userDetail(uid);
        UserInfoDetailResponse response = PojoUtil.copyBean(info, UserInfoDetailResponse.class);
        List<String> roles = userRoleService.userRoles(uid);
        response.setRoleIds(roles);
        return response;
    }


    private void cacheToken(HttpServletResponse response, UserInfo userInfo) {
        String token = StringUtil.createToken(userInfo.getId());
        TokenCacheObj obj = this.buildCache(token, userInfo);
        TokenCache.put(token, obj);
        response.setHeader(JbEnum.TOKEN_HEADER.getCode(), token);
    }

    private TokenCacheObj buildCache(String token, UserInfo ui) {
        TokenCacheObj obj = PojoUtil.copyBean(ui, TokenCacheObj.class);
        obj.setUid(ui.getId());
        obj.setUserName(ui.getUserName());
        obj.setNickName(ui.getNickName());
        obj.setToken(token);
        return obj;
    }


    /**
     * 校验用户密码
     *
     * @param ui
     * @param pwd
     */
    private void checkPassword(UserInfo ui, String pwd) {
        String encode = ENCODER.encode(pwd);
        if (!ENCODER.matches(pwd, ui.getPassWord())) {
            throw new CavException("密码不匹配！");
        }
    }

    @Resource
    private DeptInfoService deptInfoService;

    @Override
    public BaseResponse<List<UserInfoPageResponse>> userPageInfo(BaseRequest<UserInfoPageRequest> request) {
        UserInfoPageRequest params = MsgUtil.params(request);
        String deptId = params.getDeptId();
        if (StringUtils.isNotBlank(deptId)) {
            List<String> ids = deptInfoService.subDeptId(deptId);
            List<String> deptids = new ArrayList<>(ids);
            deptids.add(deptId);
            params.setDeptIds(deptids);
        }
        PageUtil<UserInfoPageResponse, UserInfoPageRequest> pu = (p, q) -> mapper.userInfoPage(p, q);
        return pu.page(request.getPage(), params);
    }


    @Override
    public void deleteUserInfo(String id) {
        UserInfo info = userDetail(id);
        this.removeById(info.getId());
        // 删除对应的角色
        userRoleService.deleteUserRoles(id);
    }

    @Override
    public void updateUserStatus(String userId, String status) {
        UserInfo info = userDetail(userId);
        if (status.equals(info.getDataStatus())) {
            throw new CavException("状态未改变！");
        }
        UserInfo updateInfo = new UserInfo();
        updateInfo.setId(userId);
        updateInfo.setDataStatus(status);
        this.updateById(updateInfo);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(UserInfoUpdateRequest updateRequest) {
        UserInfo info = PojoUtil.copyBean(updateRequest, UserInfo.class);
        checkUserUniqueness(info);
        this.updateById(info);
        userRoleService.addUserRoles(info.getId(), updateRequest.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserInfo(UserInfoCreateRequest req) {
        UserInfo info = PojoUtil.copyBean(req, UserInfo.class);
        info.setDataStatus(Constants.STATUS_00);
        checkUserUniqueness(info);
        info.setPassWord(ENCODER.encode(req.getPassword()));
        this.save(info);
        userRoleService.addUserRoles(info.getId(), req.getRoleIds());
    }


    /**
     * 重置用户密码
     *
     * @param id          用户id
     * @param newPassword 新密码
     */
    @Override
    public void resetPassword(String id, String newPassword) {
        UserInfo info = userDetail(id);
        savePassword(info.getId(), newPassword);
    }

    /**
     * 用用户密码的方式去更新用户密码
     *
     * @param params 请求参数
     */
    @Override
    public void updatePassword(UserInfoUpdatePasswordRequest params) {
        UserInfo info = userDetail(params.getId());
        checkPassword(info, params.getOldPassword());
        updatePassword(info, params.getNewPassword());
    }


    /**
     * 更新用户密码
     *
     * @param info            用户信息
     * @param newPassword     新密码
     * @param confirmPassword 确认密码
     */
    private void updatePassword(UserInfo info, String newPassword) {

        if (ENCODER.matches(newPassword, info.getPassWord())) {
            throw new CavException("新密码不能与旧密码一致！");
        }
        savePassword(info.getId(), newPassword);
    }


    /**
     * 保存密码到数据库
     *
     * @param id       用户id
     * @param password 密码
     */
    private void savePassword(String id, String password) {
        UserInfo updateInfo = new UserInfo();
        updateInfo.setId(id);
        updateInfo.setPassWord(ENCODER.encode(password));
        this.updateById(updateInfo);
    }

    /**
     * 检查用户的唯一性
     *
     * @param info 用户查询参数
     */
    private void checkUserUniqueness(UserInfo info) {
        // email phone username 唯一
        List<UserInfo> list = lambdaQueryUser(info, true);
        if (CollectionUtils.isNotEmpty(list)) {
            for (UserInfo db : list) {
                if (existUser(db, info, UserInfo::getUserName)) {
                    throw new CavException("用户名{}已经存在！", info.getUserName());
                }
                if (existUser(db, info, UserInfo::getMobile)) {
                    throw new CavException("手机{}已经存在！", info.getMobile());
                }
            }
        }
    }

    /**
     * 查询用户信息
     *
     * @param info      查询条件
     * @param onlyQuery 是否只是查询
     * @return 用户信息
     */
    private List<UserInfo> lambdaQueryUser(UserInfo info, boolean onlyQuery) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<UserInfo>()
                .ne(StringUtils.isNotBlank(info.getId()), UserInfo::getId, info.getId())
                .and(w -> w.eq(StringUtils.isNotBlank(info.getMobile()), UserInfo::getMobile,
                        info.getMobile())
                        .or().eq(StringUtils.isNotBlank(info.getUserName()), UserInfo::getUserName, info.getUserName())
                );
        List<UserInfo> list = this.list(wrapper);
        if (!onlyQuery && list.size() != 1) {
            throw new CavException("用户不存在或者存在多个！");
        }
        return list;
    }

    /**
     * 判断属性是否存在
     *
     * @param db       数据库的值
     * @param form     表单值
     * @param function 取值过程
     * @return 是否存在
     */
    private boolean existUser(UserInfo db, UserInfo form, Function<UserInfo, String> function) {
        String formValue = function.apply(form);
        return StringUtils.isNotBlank(formValue) && formValue.equals(function.apply(db));
    }

    private UserInfo userDetail(String id) {
        UserInfo info = this.getById(id);
        if (info == null) {
            throw new CavException("用户信息为空！");
        }
        return info;
    }


    @Override
    public void logout() {
        TokenCache.remove(UserUtil.token());
    }

    @Override
    public List<DictListResponse> outerUserSelected() {
        return mapper.outerUserSelected();
    }


}

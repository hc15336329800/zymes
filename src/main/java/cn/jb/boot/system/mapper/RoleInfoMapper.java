package cn.jb.boot.system.mapper;


import cn.jb.boot.system.entity.RoleInfo;
import cn.jb.boot.system.vo.request.RoleInfoPageRequest;
import cn.jb.boot.system.vo.response.RoleInfoPageResponse;
import cn.jb.boot.system.vo.response.UserRoleResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色信息表 Mapper 接口
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:45
 */
public interface RoleInfoMapper extends BaseMapper<RoleInfo> {

    /**
     * 角色分页
     *
     * @param page
     * @param request
     * @return
     */
    IPage<RoleInfoPageResponse> userRolePage(Page<RoleInfoPageResponse> page, @Param("p") RoleInfoPageRequest request);

    /**
     * 用户角色信息
     *
     * @param request
     * @return
     */
    List<UserRoleResponse> userRoles(@Param("p") RoleInfoPageRequest request);

}

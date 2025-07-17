package cn.jb.boot.system.service;


import cn.jb.boot.system.entity.RoleMenu;
import cn.jb.boot.system.vo.request.RoleMenuQueryRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色菜单表 服务类
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:45
 */
public interface RoleMenuService extends IService<RoleMenu> {

    /**
     * 查询角色已有的菜单
     *
     * @param params 参数
     * @return 菜单id
     */
    List<String> roleCheckedMenu(RoleMenuQueryRequest params);


    /**
     * 删除对应角色的菜单
     *
     * @param code
     * @param channel
     * @return
     */
    boolean deleteResource(String code, String channel);
}

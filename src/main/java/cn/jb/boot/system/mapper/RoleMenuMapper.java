package cn.jb.boot.system.mapper;


import cn.jb.boot.system.entity.RoleMenu;
import cn.jb.boot.system.vo.request.RoleMenuQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单表 Mapper 接口
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:45
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 查询角色已有的菜单
     *
     * @param params 参数
     * @return 菜单id
     */
    List<String> roleCheckedMenu(RoleMenuQueryRequest params);

    /**
     * 查询菜单使用数量
     *
     * @param menuCode 菜单Code
     * @return 结果
     */
    int checkMenuExistRole(String menuCode);

    /**
     * 删除对应角色的菜单
     *
     * @param roleCode
     * @param channel
     * @return
     */
    boolean deleteResource(@Param("code") String roleCode, @Param("channel") String channel);
}

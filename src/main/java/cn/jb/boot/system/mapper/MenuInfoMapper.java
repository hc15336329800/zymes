package cn.jb.boot.system.mapper;


import cn.jb.boot.framework.com.entity.TreeSelect;
import cn.jb.boot.system.entity.MenuInfo;
import cn.jb.boot.system.vo.request.MenuButtonMaxCode;
import cn.jb.boot.system.vo.request.MenuLazyPageRequest;
import cn.jb.boot.system.vo.request.MenuTreeRequest;
import cn.jb.boot.system.vo.request.UserRoleQueryRequest;
import cn.jb.boot.system.vo.response.MenuLazyPageResponse;
import cn.jb.boot.system.vo.response.UserSystemMenuResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 菜单信息表 Mapper 接口
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:44
 */
public interface MenuInfoMapper extends BaseMapper<MenuInfo> {

    /**
     * 获取用户的菜单或者按钮
     *
     * @param params
     * @return
     */
    List<UserSystemMenuResponse> userMenuOrButton(UserRoleQueryRequest params);

    /**
     * 懒加载分页
     *
     * @param params
     * @return
     */
    List<MenuLazyPageResponse> menuLazyPage(MenuLazyPageRequest params);

    /**
     * 菜单信息返回属性结构
     *
     * @param params
     * @return
     */
    List<TreeSelect> menuTree(MenuTreeRequest params);

    /**
     * 是否存在菜单子节点
     *
     * @param menuCode 菜单 code
     * @return 结果
     */
    int hasChildByMenuId(String menuCode);


    /**
     * 获取到最大的 code
     *
     * @param params 参数
     * @return 最大编码
     */
    String maxCodeNo(MenuButtonMaxCode params);
}

package cn.jb.boot.system.service;


import cn.jb.boot.framework.com.entity.TreeSelect;
import cn.jb.boot.system.entity.MenuInfo;
import cn.jb.boot.system.vo.request.MenuAddRequest;
import cn.jb.boot.system.vo.request.MenuInfoEditRequest;
import cn.jb.boot.system.vo.request.MenuLazyPageRequest;
import cn.jb.boot.system.vo.request.MenuTreeRequest;
import cn.jb.boot.system.vo.response.MenuDetailResponse;
import cn.jb.boot.system.vo.response.MenuLazyPageResponse;
import cn.jb.boot.system.vo.response.UserSystemMenuResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 菜单信息表 服务类
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:44
 */
public interface MenuInfoService extends IService<MenuInfo> {

    /**
     * 新增菜单信息
     *
     * @param params
     */
    void addMenu(MenuAddRequest params);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    void deleteMenuById(String menuId);

    /**
     * 根据id 或者菜单
     *
     * @param id
     * @return
     */
    MenuDetailResponse selectMenuById(String id);

    /**
     * 修改菜单
     *
     * @param params
     */
    void editMenu(MenuInfoEditRequest params);


    /**
     * 获取用户的菜单信息 用于系统
     *
     * @param channel 渠道
     * @return
     */
    List<UserSystemMenuResponse> userSystemMenu(String channel);

    /**
     * 用户按钮
     *
     * @param channel
     * @return
     */
    List<String> userSystemButton(String channel);

    /**
     * 菜单分页懒加载
     *
     * @param params
     * @return
     */
    List<MenuLazyPageResponse> menuLazyPage(MenuLazyPageRequest params);

    /**
     * 菜单数
     *
     * @param param
     * @return
     */
    List<TreeSelect> menuTree(MenuTreeRequest param);

}

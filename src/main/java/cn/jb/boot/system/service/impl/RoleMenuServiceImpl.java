package cn.jb.boot.system.service.impl;


import cn.jb.boot.system.entity.RoleMenu;
import cn.jb.boot.system.mapper.RoleMenuMapper;
import cn.jb.boot.system.service.RoleMenuService;
import cn.jb.boot.system.vo.request.RoleMenuQueryRequest;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色菜单表 服务实现类
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:45
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Resource
    private RoleMenuMapper mapper;

    @Override
    public List<String> roleCheckedMenu(RoleMenuQueryRequest params) {
        return mapper.roleCheckedMenu(params);
    }

    @Override
    public boolean deleteResource(String code, String channel) {
        return mapper.deleteResource(code, channel);
    }
}

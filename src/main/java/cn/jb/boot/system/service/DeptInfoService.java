package cn.jb.boot.system.service;


import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.entity.TreeSelect;
import cn.jb.boot.system.entity.DeptInfo;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.request.DeptInfoCreateRequest;
import cn.jb.boot.system.vo.request.DeptInfoUpdateRequest;
import cn.jb.boot.system.vo.response.DeptInfoDetailResponse;
import cn.jb.boot.system.vo.response.DeptTreeSelectResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 部门信息 服务类
 *
 * @author xieyubin
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-09-06 11:22:14
 */
public interface DeptInfoService extends IService<DeptInfo> {

    /**
     * 新增部门信息
     *
     * @param params 部门信息
     */
    void createInfo(DeptInfoCreateRequest params);

    /**
     * 修改部门信息
     *
     * @param params 部门信息
     */
    void updateInfo(DeptInfoUpdateRequest params);

    /**
     * 删除部门
     *
     * @param deptId 部门id
     */
    void deleteDept(String deptId);

    /**
     * 根据 父id 查询所有的部门
     *
     * @param paterId paterId 父id
     * @param tree    是否为树
     * @return 所有的部门
     */
    List<DeptTreeSelectResponse> listByPaterId(String paterId, boolean tree);

    /**
     * 获取所有的子部门
     *
     * @param paterId 部门id
     * @return 部门
     */
    List<String> subDeptId(String paterId);


    String topDeptId(String deptId);

    List<DeptTreeSelectResponse> deptTree(String paterId);

    DeptInfoDetailResponse detail(ComId params);


}

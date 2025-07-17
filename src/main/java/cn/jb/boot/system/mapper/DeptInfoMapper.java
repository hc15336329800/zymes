package cn.jb.boot.system.mapper;


import cn.jb.boot.system.entity.DeptInfo;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.response.DeptTreeSelectResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 部门信息 Mapper 接口
 *
 * @author xieyubin
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-09-06 11:22:14
 */
public interface DeptInfoMapper extends BaseMapper<DeptInfo> {

    /**
     * 根据父id 查找数据
     *
     * @param paterId 父id
     * @return 数据
     */
    List<DeptTreeSelectResponse> listByPaterId(String paterId);

    /**
     * 向上查找 父id
     *
     * @param deptId 部门id
     * @return 父id
     */
    String topDeptId(String deptId);

    /**
     * 获取所有的子部门
     *
     * @param paterId 部门id
     * @return 部门
     */
    List<String> subDeptId(String paterId);


    List<DictDataVo> workShopSelect();
}

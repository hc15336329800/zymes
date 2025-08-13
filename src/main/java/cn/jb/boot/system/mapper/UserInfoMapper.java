package cn.jb.boot.system.mapper;


import cn.jb.boot.system.entity.UserInfo;
import cn.jb.boot.system.vo.DictDataVo;
import cn.jb.boot.system.vo.request.UserInfoPageRequest;
import cn.jb.boot.system.vo.request.UserInfoPageResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单信息表 Mapper 接口
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:44
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {


    IPage<UserInfoPageResponse> userInfoPage(Page<UserInfoPageResponse> page, @Param("p") UserInfoPageRequest p);


    List<DictDataVo> getCache();

    List<DictListResponse> outerUserSelected();

    /**  ：全部用户列表 */
    List<DictListResponse> userInfoAll();

}

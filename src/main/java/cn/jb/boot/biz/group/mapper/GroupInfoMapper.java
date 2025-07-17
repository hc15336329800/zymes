package cn.jb.boot.biz.group.mapper;

import cn.jb.boot.biz.group.entity.GroupInfo;
import cn.jb.boot.biz.group.vo.request.GroupInfoPageRequest;
import cn.jb.boot.biz.group.vo.response.GroupInfoPageResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工人分组 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
public interface GroupInfoMapper extends BaseMapper<GroupInfo> {

    /**
     * 根据条件分页查询工人分组列表
     *
     * @param params 工人分组信息
     * @return 工人分组信息集合信息
     */
    IPage<GroupInfoPageResponse> pageInfo(Page<GroupInfoPageResponse> page, @Param("p") GroupInfoPageRequest params);

    List<DictListResponse> selected();

}

package cn.jb.boot.biz.group.mapper;

import cn.jb.boot.biz.group.entity.GroupDtl;
import cn.jb.boot.biz.group.vo.request.GroupDtlPageRequest;
import cn.jb.boot.biz.group.vo.response.GroupDtlPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 分组明细 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
public interface GroupDtlMapper extends BaseMapper<GroupDtl> {

    /**
     * 根据条件分页查询分组明细列表
     *
     * @param params 分组明细信息
     * @return 分组明细信息集合信息
     */
    IPage<GroupDtlPageResponse> pageInfo(Page<GroupDtlPageResponse> page, @Param("p") GroupDtlPageRequest params);
}

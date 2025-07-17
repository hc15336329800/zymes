package cn.jb.boot.biz.depository.mapper;

import cn.jb.boot.biz.depository.entity.OutStoreMid;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidPageRequest;
import cn.jb.boot.biz.depository.vo.response.OutStoreMidPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 中间件使用表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
public interface OutStoreMidMapper extends BaseMapper<OutStoreMid> {

    /**
     * 根据条件分页查询中间件使用表列表
     *
     * @param params 中间件使用表信息
     * @return 中间件使用表信息集合信息
     */
    IPage<OutStoreMidPageResponse> pageInfo(Page<OutStoreMidPageResponse> page, @Param("p") OutStoreMidPageRequest params);
}

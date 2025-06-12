package cn.jb.boot.biz.depository.mapper;

import cn.jb.boot.biz.depository.entity.OutStoreOrder;
import cn.jb.boot.biz.depository.vo.request.OutStoreOrderPageRequest;
import cn.jb.boot.biz.depository.vo.response.OutStoreOrderPageResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 出库单表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
public interface OutStoreOrderMapper extends BaseMapper<OutStoreOrder> {

    /**
     * 根据条件分页查询出库单表列表
     *
     * @param params 出库单表信息
     * @return 出库单表信息集合信息
     */
    IPage<OutStoreOrderPageResponse> pageInfo(Page<OutStoreOrderPageResponse> page, @Param("p") OutStoreOrderPageRequest params);
}

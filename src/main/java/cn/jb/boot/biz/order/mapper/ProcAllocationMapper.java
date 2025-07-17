package cn.jb.boot.biz.order.mapper;

import cn.jb.boot.biz.order.entity.ProcAllocation;
import cn.jb.boot.biz.order.vo.request.DistListRequest;
import cn.jb.boot.biz.order.vo.request.OuterPubPageRequest;
import cn.jb.boot.biz.order.vo.request.ProcAllocationPageRequest;
import cn.jb.boot.biz.order.vo.response.DistInfoResponse;
import cn.jb.boot.biz.order.vo.response.OuterDistInfoResponse;
import cn.jb.boot.biz.order.vo.response.ProcAllocationPageResponse;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工序分配表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
public interface ProcAllocationMapper extends BaseMapper<ProcAllocation> {

    /**
     * 根据条件分页查询工序分配表列表
     *
     * @param params 工序分配表信息
     * @return 工序分配表信息集合信息
     */
    IPage<ProcAllocationPageResponse> pageInfo(Page<ProcAllocationPageResponse> page, @Param("p") ProcAllocationPageRequest params);

    List<DistInfoResponse> distList(@Param("p") DistListRequest params);

    IPage<OuterDistInfoResponse> outerPubList(Page<OuterDistInfoResponse> page, @Param("p") OuterPubPageRequest param);

}

package cn.jb.boot.biz.sales.mapper;


import cn.jb.boot.biz.sales.entity.BomStore;
import cn.jb.boot.biz.sales.vo.request.BomStorePageReq;
import cn.jb.boot.biz.sales.vo.request.BomStoreStaReq;
import cn.jb.boot.biz.sales.vo.request.StaDetailReq;
import cn.jb.boot.biz.sales.vo.response.BomStoreResp;
import cn.jb.boot.biz.sales.vo.response.BomStoreStaResp;
import cn.jb.boot.biz.sales.vo.response.StaDetailResp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 出入库流水 Mapper 接口
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-12-18 14:27:31
 */
public interface BomStoreMapper extends BaseMapper<BomStore> {

    List<BomStoreStaResp> sta(@Param("req") BomStoreStaReq req);

    IPage<StaDetailResp> staDetail(Page<StaDetailResp> p, @Param("req") StaDetailReq req);

    IPage<BomStoreResp> pageList(Page<BomStoreResp> page, @Param("p") BomStorePageReq params);
}

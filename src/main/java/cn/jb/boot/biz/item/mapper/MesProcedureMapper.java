package cn.jb.boot.biz.item.mapper;

import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.vo.request.ItemNoRequest;
import cn.jb.boot.biz.item.vo.request.ItemProcedureRequest;
import cn.jb.boot.biz.item.vo.request.MesProcedurePageRequest;
import cn.jb.boot.biz.item.vo.request.ShortCodeReq;
import cn.jb.boot.biz.item.vo.response.ItemProcedureResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedureHeaderResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedurePageResponse;
import cn.jb.boot.biz.item.vo.response.ProcListResp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工序表 Mapper 接口
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
public interface MesProcedureMapper extends BaseMapper<MesProcedure> {

    IPage<MesProcedurePageResponse> pageInfo(Page<MesProcedurePageResponse> page, @Param("p") MesProcedurePageRequest p);

    MesProcedureHeaderResponse headerInfo(@Param("p") ItemNoRequest params);

    List<String> selectNearItemNo(String startTime);

    List<ProcListResp> listNameByShortCode(@Param("p") ShortCodeReq params);

    List<ItemProcedureResponse> listProcedureByItem(@Param("p") ItemProcedureRequest params);
}

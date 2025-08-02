package cn.jb.boot.biz.item.service;

import cn.jb.boot.biz.item.dto.MesProcedureImportResult;
import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.vo.request.ItemNoRequest;
import cn.jb.boot.biz.item.vo.request.ItemProcedureRequest;
import cn.jb.boot.biz.item.vo.request.MesProcedureCreateRequest;
import cn.jb.boot.biz.item.vo.request.MesProcedurePageRequest;
import cn.jb.boot.biz.item.vo.request.MesProcedureUpdateRequest;
import cn.jb.boot.biz.item.vo.request.ShortCodeReq;
import cn.jb.boot.biz.item.vo.response.ItemProcedureResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedureHeaderResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedureInfoResponse;
import cn.jb.boot.biz.item.vo.response.MesProcedurePageResponse;
import cn.jb.boot.biz.item.vo.response.ProcListResp;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 工序表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 16:55:58
 */
public interface MesProcedureService extends IService<MesProcedure> {

    void export(HttpServletResponse response, String id);


    List<MesProcedureInfoResponse> listByItem(ItemNoRequest request);

    MesProcedureImportResult upload(HttpServletRequest request);

    void createInfo(MesProcedureCreateRequest params);

    void updateInfo(MesProcedureUpdateRequest params);

    MesProcedureInfoResponse getInfoById(String id);

    BaseResponse<List<MesProcedurePageResponse>> pageInfo(Paging page, MesProcedurePageRequest params);

    MesProcedureHeaderResponse headerInfo(ItemNoRequest params);

    List<ProcListResp> listNameByShortCode(ShortCodeReq params);

    List<ItemProcedureResponse> listProcedureByItem(ItemProcedureRequest params);
}

package cn.jb.boot.biz.sales.service;

import cn.jb.boot.biz.sales.entity.BomStore;
import cn.jb.boot.biz.sales.vo.request.BomStoreAddReq;
import cn.jb.boot.biz.sales.vo.request.BomStorePageReq;
import cn.jb.boot.biz.sales.vo.request.BomStoreStaReq;
import cn.jb.boot.biz.sales.vo.request.BomStoreUpdateReq;
import cn.jb.boot.biz.sales.vo.request.ComIdsReq;
import cn.jb.boot.biz.sales.vo.request.StaDetailReq;
import cn.jb.boot.biz.sales.vo.response.BomStoreDetailResp;
import cn.jb.boot.biz.sales.vo.response.BomStoreResp;
import cn.jb.boot.biz.sales.vo.response.BomStoreStaResp;
import cn.jb.boot.biz.sales.vo.response.StaDetailResp;
import cn.jb.boot.framework.com.entity.ComId;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 出入库流水 服务类
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-12-18 14:27:31
 */
public interface BomStoreService extends IService<BomStore> {

    void add(BomStoreAddReq params);

    void updateStore(BomStoreUpdateReq params);

    void delete(String id);

    BaseResponse<List<BomStoreResp>> pageList(BaseRequest<BomStorePageReq> request);

    void confirm(BaseRequest<ComIdsReq> request);

    List<BomStoreStaResp> sta(BomStoreStaReq params);

    BaseResponse<List<StaDetailResp>> staDetail(BaseRequest<StaDetailReq> request);

    BomStoreDetailResp detail(ComId params);
}

package cn.jb.boot.biz.item.service;

import cn.jb.boot.biz.item.vo.request.BomNoSelectedRequest;
import cn.jb.boot.biz.item.vo.request.BomPageRequest;
import cn.jb.boot.biz.item.vo.request.ItemNoSelectedRequest;
import cn.jb.boot.biz.item.vo.response.ItemSelectedResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.biz.item.entity.MesItemStock;
import cn.jb.boot.biz.item.vo.request.MesItemStockCreateRequest;
import cn.jb.boot.biz.item.vo.request.MesItemStockPageRequest;
import cn.jb.boot.biz.item.vo.request.MesItemStockUpdateRequest;
import cn.jb.boot.biz.item.vo.response.MesItemStockInfoResponse;
import cn.jb.boot.biz.item.vo.response.MesItemStockPageResponse;
import cn.jb.boot.system.vo.response.DictListResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 产品库存表 服务类
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-08-03 13:24:13
 */
public interface MesItemStockService extends IService<MesItemStock> {

    /**
     * 新增产品库存表
     *
     * @param params 产品库存表
     */
    void createInfo(MesItemStockCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    MesItemStockInfoResponse getInfoById(String id);

    /**
     * 修改产品库存表
     *
     * @param params 产品库存表
     */
    void updateInfo(MesItemStockUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<MesItemStockPageResponse>> pageInfo(Paging page, MesItemStockPageRequest params);

    void delete(String id);

    void upload(HttpServletRequest request);

    Map<String, MesItemStock> checkedItemNos(List<String> itemNos);


    Map<String, MesItemStock> getByBomNos(List<String> boms);

    Map<String, MesItemStock> getByItemNos(List<String> itemNos);

    MesItemStock getByItemNo(String userItemNo);

    List<ItemSelectedResponse> itemNoSelected(ItemNoSelectedRequest request);


    List<ItemSelectedResponse> listBomByNo(BomNoSelectedRequest params);

    Map<String, MesItemStock> checkedBoms(List<String> boms);

    BaseResponse<List<MesItemStockPageResponse>> bomPageList(Paging page, BomPageRequest params);

}

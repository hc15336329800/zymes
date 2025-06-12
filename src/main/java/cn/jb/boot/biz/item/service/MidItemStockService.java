package cn.jb.boot.biz.item.service;

import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.vo.request.MidItemStockPageRequest;
import cn.jb.boot.biz.item.vo.response.MidItemStockPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * mes中间件库存表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 17:28:52
 */
public interface MidItemStockService extends IService<MidItemStock> {


    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<MidItemStockPageResponse>> pageInfo(Paging page, MidItemStockPageRequest params);

    List<MesProcedure> getMissingMid();

    /**
     * 查询BOM库存
     *
     * @param itemNos
     * @return
     */
    Map<String, MidItemStock> selectStock(List<String> itemNos);


}

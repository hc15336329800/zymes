package cn.jb.boot.biz.item.service;

import cn.jb.boot.biz.item.entity.DefectiveStock;
import cn.jb.boot.biz.item.vo.request.DefectiveStockCreateRequest;
import cn.jb.boot.biz.item.vo.request.DefectiveStockPageRequest;
import cn.jb.boot.biz.item.vo.request.DefectiveStockUpdateRequest;
import cn.jb.boot.biz.item.vo.response.DefectiveStockInfoResponse;
import cn.jb.boot.biz.item.vo.response.DefectiveStockPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 不良品库存 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 22:01:35
 */
public interface DefectiveStockService extends IService<DefectiveStock> {

    /**
     * 新增不良品库存
     *
     * @param params 不良品库存
     */
    void createInfo(DefectiveStockCreateRequest params);


    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<DefectiveStockPageResponse>> pageInfo(Paging page, DefectiveStockPageRequest params);

    void updateInfo(DefectiveStockUpdateRequest params);

    void delete(String id);

    DefectiveStockInfoResponse getInfoById(String id);
}

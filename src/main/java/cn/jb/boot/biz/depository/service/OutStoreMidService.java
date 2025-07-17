package cn.jb.boot.biz.depository.service;

import cn.jb.boot.biz.depository.entity.OutStoreMid;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidCreateRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidPageRequest;
import cn.jb.boot.biz.depository.vo.request.OutStoreMidUpdateRequest;
import cn.jb.boot.biz.depository.vo.response.OutStoreMidInfoResponse;
import cn.jb.boot.biz.depository.vo.response.OutStoreMidPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 中间件使用表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
public interface OutStoreMidService extends IService<OutStoreMid> {

    /**
     * 新增中间件使用表
     *
     * @param params 中间件使用表
     */
    void createInfo(OutStoreMidCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    OutStoreMidInfoResponse getInfoById(String id);

    /**
     * 修改中间件使用表
     *
     * @param params 中间件使用表
     */
    void updateInfo(OutStoreMidUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<OutStoreMidPageResponse>> pageInfo(Paging page, OutStoreMidPageRequest params);
}

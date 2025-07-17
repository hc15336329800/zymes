package cn.jb.boot.biz.device.service;

import cn.jb.boot.biz.device.entity.CheckItem;
import cn.jb.boot.biz.device.vo.request.CheckItemCreateRequest;
import cn.jb.boot.biz.device.vo.request.CheckItemPageRequest;
import cn.jb.boot.biz.device.vo.request.CheckItemUpdateRequest;
import cn.jb.boot.biz.device.vo.response.CheckItemInfoResponse;
import cn.jb.boot.biz.device.vo.response.CheckItemPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 质检项目 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
public interface CheckItemService extends IService<CheckItem> {

    /**
     * 新增质检项目
     *
     * @param params 质检项目
     */
    void createInfo(CheckItemCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    CheckItemInfoResponse getInfoById(String id);

    /**
     * 修改质检项目
     *
     * @param params 质检项目
     */
    void updateInfo(CheckItemUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<CheckItemPageResponse>> pageInfo(Paging page, CheckItemPageRequest params);
}

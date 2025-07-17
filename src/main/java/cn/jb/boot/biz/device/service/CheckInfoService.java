package cn.jb.boot.biz.device.service;

import cn.jb.boot.biz.device.entity.CheckInfo;
import cn.jb.boot.biz.device.vo.request.CheckInfoCreateRequest;
import cn.jb.boot.biz.device.vo.request.CheckInfoPageRequest;
import cn.jb.boot.biz.device.vo.request.CheckInfoUpdateRequest;
import cn.jb.boot.biz.device.vo.response.CheckInfoInfoResponse;
import cn.jb.boot.biz.device.vo.response.CheckInfoPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 点检信息 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
public interface CheckInfoService extends IService<CheckInfo> {

    /**
     * 新增点检信息
     *
     * @param params 点检信息
     */
    void createInfo(CheckInfoCreateRequest params);

    /**
     * 查询详情
     *
     * @param id 主键
     * @return 对象
     */
    CheckInfoInfoResponse getInfoById(String id);

    /**
     * 修改点检信息
     *
     * @param params 点检信息
     */
    void updateInfo(CheckInfoUpdateRequest params);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<CheckInfoPageResponse>> pageInfo(Paging page, CheckInfoPageRequest params);
}

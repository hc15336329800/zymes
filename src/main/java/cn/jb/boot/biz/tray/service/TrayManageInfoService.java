package cn.jb.boot.biz.tray.service;

import cn.hutool.json.JSONObject;
import cn.jb.boot.biz.device.vo.request.CheckInfoPageRequest;
import cn.jb.boot.biz.device.vo.response.CheckInfoPageResponse;
import cn.jb.boot.biz.tray.entity.TrayManageInfo;
import cn.jb.boot.biz.tray.vo.request.TrayManageInfoRequest;
import cn.jb.boot.biz.tray.vo.response.TrayManageInfoResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 点检信息 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
public interface TrayManageInfoService extends IService<TrayManageInfo> {

    /**
     * 托盘数据插入
     *
     * @param params 托盘数据
     */
    void createInfo(TrayManageInfoRequest params);

    /**
     * 托盘数据删除
     *
     * @param params 托盘数据
     */
    void delete(TrayManageInfoRequest params);

    /**
     * 托盘数据批量插入
     *
     * @param paramsList 托盘数据
     */
    void createAllInfo(List<TrayManageInfoRequest> paramsList);

    /**
     * 分页查询
     *
     * @param page   分页对象
     * @param params 参数
     * @return 分页集合
     */
    BaseResponse<List<TrayManageInfoResponse>> pageInfo(Paging page, TrayManageInfoRequest params);

    /**
     * 获取PDA发送的数据
     *
     * @param params Pda数据
     */
    void getPdaData(Map<String, Object> params);

    /**
     * 根据设备获取物料数据
     *
     * @param params 物料数据
     */
    String getItem(Map<String, Object> params);
}

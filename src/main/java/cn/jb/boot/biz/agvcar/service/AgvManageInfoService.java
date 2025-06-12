package cn.jb.boot.biz.agvcar.service;

import cn.jb.boot.biz.agvcar.entity.AgvManageInfo;
import cn.jb.boot.biz.agvcar.vo.request.AgvManageInfoPageRequest;
import cn.jb.boot.biz.agvcar.vo.response.AgvManageInfoPageResponse;
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
public interface AgvManageInfoService extends IService<AgvManageInfo> {

    /**
     * 新增叉车项目
     *
     * @param params 叉车项目
     */
    void createInfo(AgvManageInfoPageRequest params);

    /**
     * 查询使用中的叉车
     */
    List<AgvManageInfoPageResponse> usingCarInfo();

    /**
     * 获取工位库位
     */
    String getStation(Map<String, Object> params);

    List<Map<String, Object>> bomInfo();

    /**
     * 一周内系统agv的任务量
     */
    String getTaskOfWeek();

    List<Map<String,Object>> setKukaStatus();

    String getKukaStatus(Map<String, Object> params);
}

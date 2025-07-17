package cn.jb.boot.biz.agvcar.mapper;

import cn.jb.boot.biz.agvcar.entity.AgvManageInfo;
import cn.jb.boot.biz.agvcar.vo.request.AgvManageInfoPageRequest;
import cn.jb.boot.biz.agvcar.vo.response.AgvManageInfoPageResponse;
import cn.jb.boot.biz.device.entity.CheckInfo;
import cn.jb.boot.biz.device.vo.request.CheckInfoPageRequest;
import cn.jb.boot.biz.device.vo.request.DeviceTypeInfoPageRequest;
import cn.jb.boot.biz.device.vo.response.CheckInfoPageResponse;
import cn.jb.boot.system.vo.DictDataVo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* 点检信息 Mapper 接口
* @Description
* @Copyright Copyright (c) 2024
* @author lxl
* @since 2024-01-01 20:04:08
*/
@DS("db2")
public interface AgvManageInfoMapper extends BaseMapper<AgvManageInfo> {

    /**
    * 插入叉车数据
    *
    * @param params 名称、是否使用、时间
    * @return AGV叉车信息
    */
    int add(AgvManageInfoPageRequest params);

    /**
     * 查询使用中的叉车数据
     *
     * @return AGV叉车信息
     */
    List<AgvManageInfoPageResponse> usingCar();
    /**
     * 查询起始命令字和结束命令字
     *
     * @return 起始命令字和结束命令字
     */
    List<Map<String,Object>> locationCmd();

    /**
     * 查询起始点和结束点
     *
     * @return 起始点和结束点
     */
    List<Map<String,Object>> location();
    /**
     * 查询起始命令字和结束命令字
     *
     * @return 获取一周内的任务量
     */
    List<Map<String,Object>> getTaskOfWeek();
}

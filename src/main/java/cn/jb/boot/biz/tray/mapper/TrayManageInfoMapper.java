package cn.jb.boot.biz.tray.mapper;

import cn.jb.boot.biz.tray.entity.TrayManageInfo;
import cn.jb.boot.biz.tray.vo.request.TrayManageInfoRequest;
import cn.jb.boot.biz.tray.vo.response.TrayManageInfoResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 托盘 Mapper 接口
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-11 20:04:08
 */
public interface TrayManageInfoMapper extends BaseMapper<TrayManageInfo> {

    /**
     * 修改托盘数据
     *
     * @param params 托盘信息
     * @return
     */
    int upproduce(TrayManageInfoRequest params);

    /**
     * 删除数据
     *
     * @param params 托盘信息
     * @return
     */
    int delete(TrayManageInfoRequest params);

    /**
     * 保存托盘数据
     *
     * @param params 托盘信息
     * @return
     */
    int add(TrayManageInfoRequest params);

    /**
     * 根据条件分页查询托盘列表
     *
     * @param params 托盘信息
     * @return 托盘信息信息集合信息
     */
    IPage<TrayManageInfoResponse> pageInfo(Page<TrayManageInfoResponse> page, @Param("p") TrayManageInfoRequest params);

    /**
     * 获取PDA发送的数据
     *
     * @param params pda信息
     * @return
     */
    void addPdaData(Map<String, Object> params);

    /**
     * 获取物料信息
     *
     * @param
     * @return 物料信息
     */
    Map<String, Object> selectItem(Map<String, Object> params);

    /**
     * 获取设备信息
     *
     * @param params 工位、库位
     * @return 设备信息
     */
    Map<String, Object> getDevice(Map<String, Object> params);

    /**
     * 获取最近一次的托盘信息
     *
     * @param params 设备、库位
     * @return 设备信息
     */
    Map<String, Object> getTrayData(Map<String, Object> params);

    /**
     * 获取工位和库位
     *
     * @param params 工位
     * @return 工位库位信息
     */
    List<Map<String, Object>> getStation(Map<String, Object> params);

    /**
     * 获取所有的设备
     *
     * @return 各设备的文件获取地址
     */
    List<Map<String, Object>> getAllDeviceUrl();

    /**
     * 获取物料编码
     *
     * @param params 设备、库位
     * @return 设备信息
     */
    Map<String, Object> getItemNo(TrayManageInfoRequest params);

    /**
     * 根据设备获取物料数据
     *
     * @return 物料数据
     */
    List<Map<String, Object>> getItem(Map<String, Object> params);
}

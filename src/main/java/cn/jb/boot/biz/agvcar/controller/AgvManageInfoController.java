package cn.jb.boot.biz.agvcar.controller;

import cn.jb.boot.biz.agvcar.service.AgvManageInfoService;
import cn.jb.boot.biz.agvcar.vo.request.AgvManageInfoPageRequest;
import cn.jb.boot.biz.agvcar.vo.response.AgvManageInfoPageResponse;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 前端控制器
 * @Description
 * @Copyright Copyright (c) 2024
 * @author shihy
 * @since 2024-04-04 20:04:08
 */
@RestController
@RequestMapping("/api/agvcar/agv_info")
@Tag(name = "agv-info-controller", description = "AGV叉车信息")
public class AgvManageInfoController {

    @Resource
    private AgvManageInfoService sevice;

    /**
     * 插入信息
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/add")
    @Operation(summary = "agv叉车数据插入")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<AgvManageInfoPageRequest> request) {
        AgvManageInfoPageRequest params = MsgUtil.params(request);
        sevice.createInfo(params);
        return MsgUtil.ok();
    }
    /**
     * 查询正在使用的叉车
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/useingCar")
    @Operation(summary = "agv叉车数据查询")
    public BaseResponse<List<AgvManageInfoPageResponse>> useingCar(@RequestBody @Valid BaseRequest<AgvManageInfoPageRequest> request) {
        List<AgvManageInfoPageResponse> result = sevice.usingCarInfo();
        return MsgUtil.ok(result);
    }
    /**
     * 获取工位
     * @param params 查询参数
     * @return 工位库位
     */
    @PostMapping("/getStation")
    @Operation(summary = "获取工位")
    public String getStation(@RequestBody Map<String, Object> params) {
        String sation = sevice.getStation(params);
        return sation;
    }
    /**
     * 自动更新基础数据
     *
     * @return 工
     */
    @PostMapping("/getBomData")
    @Operation(summary = "获取bom数据")
    public String getBomData() {
        List<Map<String, Object>> sation = sevice.bomInfo();
        return "";
    }

    /**
     * 获取一周内AGV的任务量
     *
     *
     * @return 工
     */
    @PostMapping("/getTaskOfWeek")
    @Operation(summary = "获取一周内AGV的任务量")
    public String getTaskOfWeek() {
        String allTask = sevice.getTaskOfWeek();
        return allTask;
    }

    /**
     * 获取库卡机器人的状态
     *
     *
     * @return 工
     */
    @PostMapping("/getKukaStatus")
    @Operation(summary = "托盘已装满")
    public String taskByKukaStatus(@RequestBody Map<String, Object> params) {
        String allTask = sevice.getKukaStatus(params);
        return allTask;
    }

    /**
     * 发送库卡机器人的状态
     *
     * @return 工
     */
    @GetMapping("/setKukaStatus")
    @Operation(summary = "判断空托盘是否已经放在库卡上")
    public List<Map<String,Object>> returnTaskStatus() {
        List<Map<String,Object>> status = sevice.setKukaStatus();
        return status;
    }
}

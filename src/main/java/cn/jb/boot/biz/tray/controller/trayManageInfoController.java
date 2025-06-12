package cn.jb.boot.biz.tray.controller;

import cn.jb.boot.biz.tray.service.TrayManageInfoService;
import cn.jb.boot.biz.tray.vo.request.TrayManageInfoRequest;
import cn.jb.boot.biz.tray.vo.response.TrayManageInfoResponse;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 前端控制器
 *
 * @author shihy
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-11 20:04:08
 */
@RestController
@RequestMapping("/api/tray/tray_info")
@Tag(name = "tray-info-controller", description = "托盘信息")
public class trayManageInfoController {

    @Resource
    TrayManageInfoService sevice;

    /**
     * 插入信息
     *
     * @param request 查询参数
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "托盘数据插入")
    public BaseResponse<String> add(@RequestBody @Valid BaseRequest<TrayManageInfoRequest> request) {
        TrayManageInfoRequest params = MsgUtil.params(request);
        sevice.createInfo(params);
        return MsgUtil.ok();
    }

    /**
     * 删除数据
     *
     * @param request 查询参数
     * @return
     */
    @PostMapping("/delete")
    @Operation(summary = "托盘数据删除")
    public BaseResponse<String> delete(@RequestBody @Valid BaseRequest<TrayManageInfoRequest> request) {
        TrayManageInfoRequest params = MsgUtil.params(request);
        sevice.delete(params);
        return MsgUtil.ok();
    }

    /**
     * 数据的批量插入
     *
     * @param requestList 插入参数
     * @return
     */
    @PostMapping("/addAll")
    @Operation(summary = "托盘数据批量插入")
    public BaseResponse<String> addAll(@RequestBody @Valid BaseRequest<List<TrayManageInfoRequest>> requestList) {
        List<TrayManageInfoRequest> paramsList = requestList.getParams();
        sevice.createAllInfo(paramsList);
        return MsgUtil.ok();
    }

    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "托盘信息分页列表")
    public BaseResponse<List<TrayManageInfoResponse>> pageList(@RequestBody @Valid BaseRequest<TrayManageInfoRequest> request) {
        TrayManageInfoRequest params = MsgUtil.params(request);
        BaseResponse<List<TrayManageInfoResponse>> retrunData = sevice.pageInfo(request.getPage(), params);
        return retrunData;
    }

    /**
     * 获取PDA发送的数据
     *
     * @return
     */
    @RequestMapping("/getPdaData")
    public BaseResponse<String> getDetailsToKafKa(@RequestBody Map<String, Object> params) {
        sevice.getPdaData(params);
        return MsgUtil.ok();
    }

    /**
     * 根据设备获取物料数据
     *
     * @param params 查询参数
     * @return
     */
    @PostMapping("/getItem")
    @Operation(summary = "获取物料数据")
    public String getItem(@RequestBody Map<String, Object> params) {
        return sevice.getItem(params);
    }
}

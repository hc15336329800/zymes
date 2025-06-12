package cn.jb.boot.biz.shift.controller;

import cn.jb.boot.biz.shift.service.ShiftSettingService;
import cn.jb.boot.biz.shift.vo.request.ShiftSettingCreateRequest;
import cn.jb.boot.biz.shift.vo.request.ShiftSettingPageRequest;
import cn.jb.boot.biz.shift.vo.request.ShiftSettingUpdateRequest;
import cn.jb.boot.biz.shift.vo.response.ShiftSettingInfoResponse;
import cn.jb.boot.biz.shift.vo.response.ShiftSettingPageResponse;
import cn.jb.boot.framework.com.entity.ComId;
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

/**
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 14:26:37
 */
@RestController
@RequestMapping("/api/shift/shift_setting")
@Tag(name = "shift-setting-controller", description = "班次设定表接口")
public class ShiftSettingController {

    @Resource
    private ShiftSettingService service;


    @PostMapping("/list")
    @Operation(summary = "查询所有记录")
    public BaseResponse<List<ShiftSettingInfoResponse>> list() {
        List<ShiftSettingInfoResponse> list = service.listDetails();
        return MsgUtil.ok(list);
    }


    /**
     * 保存班次信息
     *
     * @param request 参数
     * @return 是否成功
     */
    @PostMapping("/save")
    @Operation(summary = "保存班次信息")
    public BaseResponse<String> save(@RequestBody @Valid BaseRequest<ShiftSettingCreateRequest> request) {
        service.saveShift(MsgUtil.params(request));
        return MsgUtil.ok();
    }
}

package cn.jb.boot.biz.shift.service;

import cn.hutool.core.lang.Tuple;
import cn.jb.boot.biz.shift.entity.ShiftSetting;
import cn.jb.boot.biz.shift.vo.request.ShiftSettingCreateRequest;
import cn.jb.boot.biz.shift.vo.request.ShiftSettingPageRequest;
import cn.jb.boot.biz.shift.vo.request.ShiftSettingUpdateRequest;
import cn.jb.boot.biz.shift.vo.response.ShiftSettingInfoResponse;
import cn.jb.boot.biz.shift.vo.response.ShiftSettingPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 班次设定表 服务类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 14:26:37
 */
public interface ShiftSettingService extends IService<ShiftSetting> {


    List<ShiftSettingInfoResponse> listDetails();

    void saveShift(ShiftSettingCreateRequest params);

    /**
     * 获取当前时间班次
     *
     * @return
     */
    String getCurrentShift();


    Tuple getShiftTime();
}

package cn.jb.boot.biz.shift.service.impl;

import cn.hutool.core.lang.Tuple;
import cn.jb.boot.biz.shift.entity.ShiftSetting;
import cn.jb.boot.biz.shift.enums.ShiftEnum;
import cn.jb.boot.biz.shift.mapper.ShiftSettingMapper;
import cn.jb.boot.biz.shift.service.ShiftSettingService;
import cn.jb.boot.biz.shift.vo.request.ShiftSettingCreateRequest;
import cn.jb.boot.biz.shift.vo.request.ShiftSettingPageRequest;
import cn.jb.boot.biz.shift.vo.request.ShiftSettingUpdateRequest;
import cn.jb.boot.biz.shift.vo.response.ShiftSettingInfoResponse;
import cn.jb.boot.biz.shift.vo.response.ShiftSettingPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.exception.CavException;
import cn.jb.boot.util.PageUtil;
import cn.jb.boot.util.PojoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 班次设定表 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-07 14:26:37
 */
@Service
public class ShiftSettingServiceImpl extends ServiceImpl<ShiftSettingMapper, ShiftSetting> implements ShiftSettingService {

    @Resource
    private ShiftSettingMapper mapper;

    @Override
    public List<ShiftSettingInfoResponse> listDetails() {
        List<ShiftSetting> list = this.list();
        return PojoUtil.copyList(list, ShiftSettingInfoResponse.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveShift(ShiftSettingCreateRequest params) {
        List<ShiftSettingCreateRequest.ShiftSettingReq> list = params.getList();
        List<ShiftSetting> shiftSettings = PojoUtil.copyList(list, ShiftSetting.class);
        Map<Integer, List<ShiftSetting>> map = shiftSettings.stream().collect(Collectors.groupingBy(ShiftSetting::getWeekDay));
        for (Integer v : map.keySet()) {
            List<ShiftSetting> settings = map.get(v);
            if (settings.size() > 1) {
                Map<String, ShiftSetting> smap = settings.stream().collect(Collectors.toMap(ShiftSetting::getShiftType, Function.identity()));
                ShiftSetting day = smap.get(ShiftEnum.DAY_SHIFT.getCode());
                ShiftSetting night = smap.get(ShiftEnum.NIGHT_SHIFT.getCode());
                if (day.getEndTime().isAfter(night.getStartTime())) {
                    throw new CavException("班次时间存在交集");
                }
            }

        }

        this.remove(new LambdaQueryWrapper<ShiftSetting>().in(ShiftSetting::getShiftType,
                Arrays.asList(ShiftEnum.DAY_SHIFT.getCode(), ShiftEnum.NIGHT_SHIFT.getCode())));
        this.saveBatch(shiftSettings);
    }


    @Override
    public String getCurrentShift() {
        List<ShiftSetting> shifts = this.list();
        Map<Integer, List<ShiftSetting>> map = shifts.stream().collect(Collectors.groupingBy(ShiftSetting::getWeekDay));
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        int week = dayOfWeek.getValue();
        List<ShiftSetting> shiftSettings = map.get(week);
        Optional<ShiftSetting> optional = shiftSettings.stream().filter(ss -> ShiftEnum.DAY_SHIFT.getCode().equals(ss.getShiftType())).findAny();
        if (optional.isPresent()) {
            ShiftSetting ss = optional.get();
            LocalTime endTime = ss.getEndTime();
            LocalTime startTime = ss.getStartTime();
            if (LocalTime.now().isBefore(endTime) && LocalTime.now().isAfter(startTime)) {
                return ShiftEnum.DAY_SHIFT.getCode();
            }
        }
        return ShiftEnum.NIGHT_SHIFT.getCode();
    }

    @Override
    public Tuple getShiftTime() {
        List<ShiftSetting> shifts = this.list();
        Map<Integer, List<ShiftSetting>> map = shifts.stream().collect(Collectors.groupingBy(ShiftSetting::getWeekDay));
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        //今天是周一
        if (LocalDate.now().compareTo(monday) == 0) {
            List<ShiftSetting> shiftSettings = map.get(1);
            Optional<ShiftSetting> optional = shiftSettings.stream().filter(ss -> ShiftEnum.DAY_SHIFT.getCode().equals(ss.getShiftType())).findAny();
            ShiftSetting ss = optional.get();
            LocalTime startTime = ss.getStartTime();
            //归属到上周末的时间
            if (LocalTime.now().isBefore(startTime)) {
                shiftSettings = map.get(7);
                ShiftSetting nightSs = shiftSettings.stream().filter(data -> ShiftEnum.NIGHT_SHIFT.getCode().equals(data.getShiftType())).findAny().get();
                LocalDateTime end = LocalDateTime.of(LocalDate.now(), nightSs.getEndTime());
                LocalDateTime start = LocalDateTime.of(LocalDate.now().plusDays(-1), nightSs.getStartTime());
                return new Tuple(start, end);
            }
        }
        for (int i = 1; i <= 7; i++) {
            LocalDate shiftDay = monday.plusDays(i - 1);
            List<ShiftSetting> shiftSettings = map.get(i);
            Optional<ShiftSetting> optional = shiftSettings.stream().filter(ss -> ShiftEnum.DAY_SHIFT.getCode().equals(ss.getShiftType())).findAny();
            ShiftSetting nightSs = shiftSettings.stream().filter(ss -> ShiftEnum.NIGHT_SHIFT.getCode().equals(ss.getShiftType())).findAny().get();
            LocalDateTime startTime;
            if (optional.isPresent()) {
                ShiftSetting ss = optional.get();
                startTime = LocalDateTime.of(shiftDay, ss.getStartTime());
            } else {
                startTime = LocalDateTime.of(shiftDay, nightSs.getStartTime());
            }
            LocalDateTime endTime = LocalDateTime.of(shiftDay.plusDays(1), nightSs.getEndTime());
            if (LocalDateTime.now().isBefore(endTime) && LocalDateTime.now().isAfter(startTime)) {
                return new Tuple(startTime, endTime);
            }
        }

        return null;


    }
}

package cn.jb.boot.biz.shift.enums;

import cn.jb.boot.biz.shift.vo.response.EnumResp;
import cn.jb.boot.util.SnowFlake;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ShiftEnum {
    DAY_SHIFT("01", "白班"),
    NIGHT_SHIFT("02", "夜班"),
    ;

    private String code;
    private String desc;

    public static LocalDate getShiftEndDate(String shiftType) {
        if (ShiftEnum.NIGHT_SHIFT.getCode().equals(shiftType)) {
            //小于当24点，取当天，超过24点，取第二天
            if (LocalTime.now().isAfter(LocalTime.of(12, 1, 0)) && LocalTime.now().isBefore(LocalTime.of(23, 59, 59))) {
                return LocalDate.now().plusDays(1);
            }
        }
        return LocalDate.now();
    }


}

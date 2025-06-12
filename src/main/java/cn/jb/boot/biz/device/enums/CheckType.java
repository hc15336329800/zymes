package cn.jb.boot.biz.device.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckType {
    CHECK("01", "点检"),

    MAINTENANCE("02", "保养"),


    ;


    private String code;
    private String name;
}

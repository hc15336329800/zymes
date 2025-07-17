package cn.jb.boot.framework.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ValidEnum {
    VALID("01", "有效的"),
    INVALID("00", "无效的"),
    ;
    private String code;
    private String name;
}

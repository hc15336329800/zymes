package cn.jb.boot.biz.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProcStatus {
    RUNNING("01", "启用"),
    CLOSED("00", "停用");
    private String code;
    private String name;
}

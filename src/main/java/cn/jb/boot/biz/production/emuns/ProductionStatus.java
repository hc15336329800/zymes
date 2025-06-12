package cn.jb.boot.biz.production.emuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductionStatus {
    TO_READY("01", "就绪"),
    SCHEDULED("02", "已排产"),

    ;

    private String code;
    private String desc;
}

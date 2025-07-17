package cn.jb.boot.biz.group.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeaderType {
    WORKER("00", "工人"),

    LEADER("01", "组长"),


    ;
    private String code;

    private String name;
}

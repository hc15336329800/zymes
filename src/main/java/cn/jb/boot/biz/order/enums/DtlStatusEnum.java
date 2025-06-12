package cn.jb.boot.biz.order.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 详情状态枚举值
 * 01:就绪，02:排程中,03:待生产,04:生产中,05:待发货,06:关闭,07:暂停
 *
 * @author xyb
 * @Description
 * @Date 2022/8/15 22:55
 */
public enum DtlStatusEnum {



    AWAITING_PRODUCTION("03", "待生产"),
    IN_PRODUCED("04", "生产中"),
    TOBE_SHIPPED("05", "待发货"),
    CLOSE("06", "关闭"),

    SUSPEND("07", "暂停"),
    END("08", "完成"),
    ;

    DtlStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


}

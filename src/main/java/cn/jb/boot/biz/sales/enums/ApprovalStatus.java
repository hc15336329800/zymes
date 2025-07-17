package cn.jb.boot.biz.sales.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalStatus {
    TO_REVIEW("00", "待审批"),
    PASSED("01", "审批通过"),
    REJECT("02", "审批拒绝"),

    ;
    private String code;
    private String desc;
}

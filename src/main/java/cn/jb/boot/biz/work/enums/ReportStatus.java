package cn.jb.boot.biz.work.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportStatus {
    TO_CHECK("00", "待审核"),
    TO_REVIEW("01", "待验收"),
    CHECK_REJECT("02", "审核拒绝"),
    REVIEW_PASS("03", "验收通过"),
    REVIEW_REJECT("04", "验收拒绝"),


    ;

    private String code;
    private String name;
}

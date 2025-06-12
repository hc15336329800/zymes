package cn.jb.boot.biz.work.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportTypeEnum {

    PERSON_NUMBER("01", "按件数分(人)"),
    PERSON_COOPERATION("02", "合工(人)"),
    GROUP_COOPERATION("03", "合工(组)"),
    ;

    private String code;
    private String name;
}

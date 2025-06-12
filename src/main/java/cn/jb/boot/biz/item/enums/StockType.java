package cn.jb.boot.biz.item.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockType {
    OUT("00", "出库"),
    IN("01", "入库"),

    ;
    private String code;
    private String name;


}

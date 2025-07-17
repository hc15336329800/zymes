package cn.jb.boot.biz.item.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemType {
    MATERIALS("00", "原料"),
    BOM("01", "加工件"),

    ;
    private String code;
    private String name;


    public static String getCodeByName(String name) {
        if (BOM.name.equals(name)) {
            return BOM.code;
        }
        return MATERIALS.code;
    }

    public static boolean isBom(String itemType) {
        return BOM.code.equals(itemType);
    }

    public static boolean isMaterials(String itemType) {
        return MATERIALS.code.equals(itemType);
    }
}

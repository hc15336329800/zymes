package cn.jb.boot.framework.com;

/**
 * 数据字段
 *
 * @Author: xyb
 * @Description:
 * @Date: 2022-11-14 下午 09:38
 **/
public enum DictType {
    /**
     * 根
     **/
    ROOT_TYPE("JB_ROOT"),

    /**
     * 性别
     **/
    GENDER("GENDER"),
    DATA_STATUS("DATA_STATUS"),
    /**
     * 物料类型
     */
    ITEM_TYPE("ITEM_TYPE"),

    /**
     * 来源
     */
    ITEM_ORIGIN("ITEM_ORIGIN"),

    BOM_NO("BOM_NO"),

    ITEM_NAME("ITEM_NAME"),


    USER_INFO("USER_INFO"),
    WORK_SHOP("WORK_SHOP"),
    WARE_HOUSE("WARE_HOUSE"),
    ORDER_TYPE("ORDER_TYPE"),
    APPROVAL_STATUS("APPROVAL_STATUS"),
    PRODUCTION_STATUS("PRODUCTION_STATUS"),
    CHECK_TYPE("CHECK_TYPE"),
    DEVICE("DEVICE"),
    STOCK_TYPE("STOCK_TYPE"),
    LEADER_TYPE("LEADER_TYPE"),

    SHIFT_TYPE("SHIFT_TYPE"),

    PROC_STATUS("PROC_STATUS"),

    REPORT_TYPE("REPORT_TYPE"),
    /**
     * 报工审核状态
     */
    REP_CHECK_STATUS("REP_CHECK_STATUS"),
    /**
     * 报工验收状态
     */
    REP_CERIFY_STATUS("REP_CERIFY_STATUS"),


    OUT_STATUS("OUT_STATUS"),


    ORDER_STATUS("ORDER_STATUS"),

    REPORT_STATUS("REPORT_STATUS"),

    ACCEPT_STATUS("ACCEPT_STATUS"),

    CONFIRM_STATUS("CONFIRM_STATUS"),


    ;
    /**
     * code
     */
    private final String code;

    DictType(String code) {
        this.code = code;
    }

    /**
     * 获取 code
     *
     * @return code code值
     */
    public String getCode() {
        return this.code;
    }

    public static DictType byCode(String code) {
        for (DictType bt : DictType.values()) {
            if (bt.getCode().equals(code)) {
                return bt;
            }
        }
        return ROOT_TYPE;
    }
}

package cn.jb.boot.framework.enums;


/**
 * 工单管理相关枚举
 *
 * @author
 */
public enum WorkEnum {

    /**
     * 补录单状态
     * 01:待确认
     */
    TOBECONFIRMED("01", "待确认"),

    /**
     * 补录单状态
     * 02:已确认
     */
    CONFIRMED("02", "已确认");
    /**
     * 编码
     */
    private final String code;
    /**
     * 描述
     */
    private final String message;

    /**
     * 枚举构造
     *
     * @param code
     * @param message
     */
    WorkEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

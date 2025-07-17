package cn.jb.boot.framework.enums;

/**
 * @author YX
 * @Description 常量枚举值
 * @Date 2021/8/18 0:10
 */
public enum JbEnum {
    /**
     * 成功
     */
    SUCCESS("00"),
    /**
     * 失败
     */
    FAIL("01"),
    ATTR_SYS_TRACE("sys_trace"),
    ATTR_REQUEST_CONTENT("request_content"),
    ATTR_REQUEST_TIME("request_time"),

    /**
     * token头
     */
    TOKEN_HEADER("Authorization"),
    /**
     * MISSING_TOKEN
     */
    MISSING_TOKEN("missing_token"),

    /**
     * INVALID_TOKEN
     */
    INVALID_TOKEN("invalid_token"),

    POST("POST"),
    GET("GET"),

    ROOT_TYPE("JB_ROOT"),

    CODE_00("00"),
    CODE_01("01"),
    CODE_02("02"),
    CODE_03("03"),
    CODE_04("04"),
    CODE_05("05"),


    CODE_0("0"),
    LSJG("LSJG"),
    ADIV("ADIV"),
    ROOT("root"),


    CODE_1("1"),


    // 常量值 大写字母集合
    STRING_LETTER("QWERTYUIOPASDFGHJKLZXCVBNM"),
    // 常量值 小写字母集合
    STRING_letter("qwertyuiopasdfghjklzxcvbnm"),
    // 常量值 数字集合
    STRING_NUMBER("0123456789");


    private String code;

    JbEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}

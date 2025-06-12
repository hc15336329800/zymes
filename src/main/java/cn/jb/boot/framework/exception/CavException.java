package cn.jb.boot.framework.exception;

/**
 * 全局异常
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2019
 * @Date 2019年10月14日 上午11:04:00
 */
public class CavException extends RuntimeException {
    /**
     * @Filelds serialVersionUID :
     */
    private static final long serialVersionUID = -6618584040122506454L;

    private static final Object[] OS = new Object[1];

    public CavException(String code) {
        this(code, OS);
    }

    public CavException(String code, Object... params) {
        this.code = code;
        this.params = params;
    }

    private String code;

    private Object[] params;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object[] getParameters() {
        return this.params;
    }
}

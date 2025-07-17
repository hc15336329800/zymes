package cn.jb.boot.framework.value;

import lombok.Data;

import java.util.List;

/**
 * 黑白名单设置
 *
 * @author xyb
 * @Description
 * @Date 2022/7/2 15:04
 */
@Data
public class WhiteListProperties {

    /**
     * 普通 api
     */
    private List<String> apiBlackList;
    /**
     * 普通 api 白名单
     */
    private List<String> apiPermitAll;

}

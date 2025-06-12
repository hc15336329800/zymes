package cn.jb.boot.framework.value;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xyb
 * @Description
 * @Date 2022/7/2 13:28
 */
@Data
@Component
@ConfigurationProperties("jbtcl.local")
public class AppProperties {



    /** 黑 白名单 设置 */
    private WhiteListProperties whiteList = new WhiteListProperties();

    private DingTalkProperties dingTalk = new DingTalkProperties();

}

package cn.jb.boot.framework.value;

import lombok.Data;

@Data
public class DingTalkProperties {
    private String appKey;
    private String appSecret;
    private Long agentId;
}

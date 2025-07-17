package cn.jb.boot.framework.com.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TokenCacheObj implements Serializable {
    private static final long serialVersionUID = -9123418452977467869L;

    private String token;

    private String uid;
    private String userName;
    private String deptId;
    private String dingUserId;
    private String mobile;
    private String nickName;

    public TokenCacheObj() {

    }

    public TokenCacheObj(String token, String uid, String userName) {
        this.token = token;
        this.uid = uid;
        this.userName = userName;
    }

}

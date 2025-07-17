package cn.jb.boot.system.vo.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息表表 分页返回参数
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-03-24 14:52:38
 */
@Data
@Schema(name = "UserInfoPageResponse", description = "用户信息表 分页返回参数")
public class UserInfoPageResponse implements Serializable {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String id;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String userName;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickName;


    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String mobile;


    /**
     * 帐号状态;00正常 01停用
     */
    @Schema(description = "帐号状态;00正常 01停用")
    private String dataStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "部门名称")
    private String deptName;


    private String roleNameStr;


    @Schema(description = "部门Id")
    private String deptId;


}

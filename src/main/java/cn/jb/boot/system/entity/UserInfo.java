package cn.jb.boot.system.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户信息表表
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-03-24 14:52:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_info")
@Schema(name = "1BaseUserInfo", description = "用户信息表")
public class UserInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    @TableField("user_name")
    private String userName;


    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    @TableField("nick_name")
    private String nickName;


    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    @TableField("dept_id")
    private String deptId;


    /**
     * 密码
     */
    @Schema(description = "密码")
    @TableField("pass_word")
    private String passWord;


    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @TableField("mobile")
    private String mobile;


    @Schema(description = "钉钉用户Id")
    @TableField("ding_user_id")
    private String dingUserId;


    /**
     * 帐号状态;00 未启用 01 启用
     */
    @Schema(description = "帐号状态;00 未启用 01 启用")
    @TableField("data_status")
    private String dataStatus;

    @TableField(exist = false)
    private String shortCode;


}

package cn.jb.boot.system.entity;


import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色信息表表
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_info")
@Schema(name = "RoleInfo", description = "角色信息表")
public class RoleInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    @TableField("role_code")
    private String roleCode;


    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    @TableField("role_name")
    private String roleName;


    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    @TableField("role_desc")
    private String roleDesc;


    /**
     * 系统角色
     */
    @Schema(description = "系统角色")
    @TableField("sys_role")
    private String sysRole;


}

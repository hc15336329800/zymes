package cn.jb.boot.system.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单信息表表
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-04-11 20:40:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu_info")
@Schema(name = "MenuInfo", description = "菜单信息表")
public class MenuInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 菜单编码
     */
    @Schema(description = "菜单编码")
    @TableField(value = "menu_code", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private String menuCode;


    /**
     * 父亲菜单编码
     */
    @Schema(description = "父亲菜单编码")
    @TableField(value = "parent_code", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private String parentCode;


    /**
     * 顺序号
     */
    @Schema(description = "顺序号")
    @TableField("seq_no")
    private Integer seqNo;


    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    @TableField("menu_name")
    private String menuName;


    /**
     * 菜单地址
     */
    @Schema(description = "菜单地址")
    @TableField("menu_url")
    private String menuUrl;


    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    @TableField("menu_icon")
    private String menuIcon;


    /**
     * 菜单类型;01 菜单 02按钮
     */
    @Schema(description = "菜单类型;01 菜单 02按钮")
    @TableField(value = "menu_type", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private String menuType;


    /**
     * 是否有效 00 正常，01 无效
     */
    @Schema(description = "是否有效 00 正常，01 无效")
    @TableField("data_valid")
    private String dataValid;


    /**
     * 渠道 01:web 02:手机
     */
    @Schema(description = "渠道 01:web 02:手机")
    @TableField(value = "channel", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private String channel;

}

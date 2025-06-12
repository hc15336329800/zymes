package cn.jb.boot.biz.device.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 质检项目表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_check_item")
@Schema(name = "CheckItem", description = "质检项目")
public class CheckItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 点检类型;01:点检，02:保养
     */
    @Schema(description = "点检类型;01:点检，02:保养")
    @TableField("check_type")
    private String checkType;


    /**
     * 项目内容
     */
    @Schema(description = "项目内容")
    @TableField("check_content")
    private String checkContent;


    /**
     * 标准
     */
    @Schema(description = "标准")
    @TableField("check_standard")
    private String checkStandard;


    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

}

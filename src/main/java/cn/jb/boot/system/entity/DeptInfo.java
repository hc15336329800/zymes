package cn.jb.boot.system.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * 部门信息表
 *
 * @author xieyubin
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-09-13 11:13:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept_info")
@Schema(name = "DeptInfo", description = "部门信息")
public class DeptInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    @TableField("dept_name")
    private String deptName;


    /**
     * 父部门id
     */
    @Schema(description = "父部门id")
    @TableField("pater_id")
    private String paterId;

    @TableField("seq_no")
    private Integer seqNo;

    @TableField("director_uid")
    private String directorUid;
    @Schema(description = "部门类型 00:普通部门，01:车间")
    @TableField("dept_type")
    private String deptType;


}

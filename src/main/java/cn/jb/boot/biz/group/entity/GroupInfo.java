package cn.jb.boot.biz.group.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工人分组表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_group_info")
@Schema(name = "GroupInfo", description = "工人分组")
public class GroupInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 组名
     */
    @Schema(description = "组名")
    @TableField("group_name")
    private String groupName;


    /**
     * 组报人id
     */
    @Schema(description = "组报人id")
    @TableField("group_uid")
    private String groupUid;


    /**
     * 车间id
     */
    @Schema(description = "车间id")
    @TableField("dept_id")
    private String deptId;

    @TableField("remark")
    private String remark;


}

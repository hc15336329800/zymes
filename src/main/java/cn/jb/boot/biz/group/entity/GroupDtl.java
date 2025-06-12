package cn.jb.boot.biz.group.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 分组明细表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_group_dtl")
@Schema(name = "GroupDtl", description = "分组明细")
public class GroupDtl extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 分组id
     */
    @Schema(description = "分组id")
    @TableField("group_id")
    private String groupId;


    /**
     * 用户Id
     */
    @Schema(description = "用户Id")
    @TableField("user_id")
    private String userId;


    /**
     * 合共占比
     */
    @Schema(description = "合共占比")
    @TableField("percentage")
    private BigDecimal percentage;

    @TableField("leader_type")
    private String leaderType;


    @TableField(exist = false)
    private String groupUid;

    @Schema(description = "报工数量")
    @TableField(exist = false)
    private BigDecimal reportCount;


    /**
     * 归属用户数量
     */
    @Schema(description = "归属用户数量")
    @TableField(exist = false)
    private BigDecimal userCount;


}

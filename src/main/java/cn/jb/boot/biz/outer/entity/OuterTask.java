package cn.jb.boot.biz.outer.entity;

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
 * 外协任务表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_outer_task")
@Schema(name = "OuterTask", description = "外协任务")
public class OuterTask extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 外协账号
     */
    @Schema(description = "外协账号")
    @TableField("user_id")
    private String userId;


    /**
     * 工序分配id
     */
    @Schema(description = "工序分配id")
    @TableField("alloc_id")
    private String allocId;


    /**
     * 外协数量
     */
    @Schema(description = "外协数量")
    @TableField("outer_count")
    private BigDecimal outerCount;


    /**
     * 待验收数量
     */
    @Schema(description = "待验收数量")
    @TableField("wait_real_count")
    private BigDecimal waitRealCount;


    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    @TableField("real_count")
    private BigDecimal realCount;


    /**
     * 待验收次品数量
     */
    @Schema(description = "待验收次品数量")
    @TableField("wait_deff_count")
    private BigDecimal waitDeffCount;


    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    @TableField("deff_count")
    private BigDecimal deffCount;


    @TableField("accept_status")
    private String acceptStatus;

}

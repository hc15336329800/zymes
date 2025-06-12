package cn.jb.boot.biz.work.entity;

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
 * 工单下达表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_work_assign")
@Schema(name = "WorkAssign", description = "工单下达表")
public class WorkAssign extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 工单Id
     */
    @Schema(description = "工单Id")
    @TableField("work_order_id")
    private String workOrderId;


    /**
     * 下达数量
     */
    @Schema(description = "下达数量")
    @TableField("assign_count")
    private BigDecimal assignCount;

}

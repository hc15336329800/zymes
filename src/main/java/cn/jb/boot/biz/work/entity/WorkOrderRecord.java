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
 * 工序分配记录表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-08 18:34:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_work_order_record")
@Schema(name = "WorkOrderRecord", description = "工序分配记录")
public class WorkOrderRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 工单表记录
     */
    @Schema(description = "工单表记录")
    @TableField("work_order_id")
    private String workOrderId;


    /**
     * 分配数量
     */
    @Schema(description = "分配数量")
    @TableField("item_count")
    private BigDecimal itemCount;

}

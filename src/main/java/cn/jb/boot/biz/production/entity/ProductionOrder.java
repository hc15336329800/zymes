package cn.jb.boot.biz.production.entity;

import cn.jb.boot.framework.com.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 生产任务单表
 *
 * @author user
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-01-15 21:08:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_production_order")
@Schema(name = "ProductionOrder", description = "生产任务单")
public class ProductionOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 销售单号
     */
    @Schema(description = "销售单号")
    @TableField("sales_order_no")
    private String salesOrderNo;


    /**
     * 产品号
     */
    @Schema(description = "产品号")
    @TableField("item_no")
    private String itemNo;

    /**
     * 数量
     */
    @Schema(description = "数量")
    @TableField("item_count")
    private BigDecimal itemCount;


    @TableField("status")
    private String status;

    /**
     * 优先级  "紧急" ="02" "加急" ="03" "正常" ="01" "延后""04"
     */
    @TableField("biz_Type")
    private String bizType;

    /**
     * 承诺交期
     */
    @Schema(description = "承诺交期")
    @TableField("deliver_time")
    private LocalDateTime deliverTime;


    @Schema(description = "审核Id")
    @TableField("place_id")
    private String placeId;

    @Schema(description = "工序编码")
    @TableField("procedure_code")
    private String procedureCode;
    @Schema(description = "工序名称")
    @TableField("procedure_name")
    private String procedureName;


    /**
     * 订单类型  "销售单" ="01" "加急单" ="03" "追加计划" ="01" "月度单""04"     新
     */
    @TableField("order_type")
    private String orderType;

}

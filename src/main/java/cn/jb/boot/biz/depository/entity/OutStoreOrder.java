package cn.jb.boot.biz.depository.entity;

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
 * 出库单表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_out_store_order")
@Schema(name = "OutStoreOrder", description = "出库单表")
public class OutStoreOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 工单表ID或外协任务Id
     */
    @Schema(description = "工单表ID或外协任务Id")
    @TableField("biz_id")
    private String bizId;


    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    @TableField("biz_type")
    private String bizType;


    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 部门Id
     */
    @Schema(description = "部门Id")
    @TableField("dept_id")
    private String deptId;


    /**
     * 出库状态;出库状态 00：待出库，01：已出库
     */
    @Schema(description = "出库状态;出库状态 00：待出库，01：已出库")
    @TableField("out_status")
    private String outStatus;


    /**
     * 领料时间
     */
    @Schema(description = "领料时间")
    @TableField("out_time")
    private LocalDateTime outTime;


    /**
     * 领料人
     */
    @Schema(description = "领料人")
    @TableField("out_user")
    private String outUser;


    /**
     * 下达数量
     */
    @Schema(description = "下达数量")
    @TableField("assign_count")
    private BigDecimal assignCount;


    /**
     * 用料编码
     */
    @Schema(description = "用料编码")
    @TableField("use_item_no")
    private String useItemNo;


    /**
     * 预估数量
     */
    @Schema(description = "预估数量")
    @TableField("plan_count")
    private BigDecimal planCount;


    /**
     * 实际数量
     */
    @Schema(description = "实际数量")
    @TableField("real_count")
    private BigDecimal realCount;


    /**
     * 审核状态
     */
    @Schema(description = "审核状态")
    @TableField("review_status")
    private String reviewStatus;


    /**
     * 审核人
     */
    @Schema(description = "审核人")
    @TableField("review_by")
    private String reviewBy;

}

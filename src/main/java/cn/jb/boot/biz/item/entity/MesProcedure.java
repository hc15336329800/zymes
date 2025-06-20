package cn.jb.boot.biz.item.entity;

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
 * 工序表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 00:00:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mes_procedure")
@Schema(name = "MesProcedure", description = "工序表")
public class MesProcedure extends BaseEntity {

    private static final long serialVersionUID = 1L;



    /**
     * 是否有效
     */
    @Schema(description = "是否有效 00:无效，01:有效")
    @TableField("is_valid")
    private String IsValid;

    /**
     * bom编码
     */
    @Schema(description = "bom编码")
    @TableField("bom_no")
    private String bomNo;


    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 序号，执行顺序
     */
    @Schema(description = "序号，执行顺序")
    @TableField("seq_no")
    private Integer seqNo;


    /**
     * 工序编码
     */
    @Schema(description = "工序编码")
    @TableField("procedure_code")
    private String procedureCode;


    /**
     * 工序名称
     */
    @Schema(description = "工序名称")
    @TableField("procedure_name")
    private String procedureName;


    /**
     * 加工工时
     */
    @Schema(description = "加工工时")
    @TableField("hours_work")
    private BigDecimal hoursWork;


    /**
     * 工作车间Id
     */
    @Schema(description = "工作车间Id")
    @TableField("dept_id")
    private String deptId;


    /**
     * 设备Id
     */
    @Schema(description = "设备Id")
    @TableField("device_id")
    private String deviceId;


    /**
     * 额定工时
     */
    @Schema(description = "额定工时")
    @TableField("hours_fixed")
    private BigDecimal hoursFixed;


    /**
     * 准备工时
     */
    @Schema(description = "准备工时")
    @TableField("hours_prepare")
    private BigDecimal hoursPrepare;


    /**
     * 简码
     */
    @Schema(description = "简码")
    @TableField("short_code")
    private String shortCode;

}

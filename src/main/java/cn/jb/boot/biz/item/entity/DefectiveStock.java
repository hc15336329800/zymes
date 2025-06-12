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
 * 不良品库存表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 22:01:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_defective_stock")
@Schema(name = "DefectiveStock", description = "不良品库存")
public class DefectiveStock extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
     * 数量
     */
    @Schema(description = "数量")
    @TableField("item_count")
    private BigDecimal itemCount;


    /**
     * 出入库类型
     */
    @Schema(description = "出入库类型")
    @TableField("stock_type")
    private String stockType;

    @TableField("remark")
    private String remark;

}

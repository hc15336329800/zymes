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

/**
 * 中间件使用表表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_out_store_mid")
@Schema(name = "OutStoreMid", description = "中间件使用表")
public class OutStoreMid extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 中间件表ID
     */
    @Schema(description = "中间件表ID")
    @TableField("mid_id")
    private String midId;


    /**
     * 件数
     */
    @Schema(description = "件数")
    @TableField("item_count")
    private BigDecimal itemCount;


    /**
     * 明细Id
     */
    @Schema(description = "明细Id")
    @TableField("order_dtl_id")
    private String orderDtlId;

}

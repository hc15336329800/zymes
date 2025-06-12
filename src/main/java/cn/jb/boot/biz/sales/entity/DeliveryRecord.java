package cn.jb.boot.biz.sales.entity;

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
 * 发货单记录表
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 12:56:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_delivery_record")
@Schema(name = "DeliveryRecord", description = "发货单记录")
public class DeliveryRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 发货单Id
     */
    @Schema(description = "发货单Id")
    @TableField("delivery_id")
    private String deliveryId;


    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    @TableField("item_no")
    private String itemNo;


    /**
     * 客户名称
     */
    @Schema(description = "客户名称")
    @TableField("cust_name")
    private String custName;


    /**
     * 发货数量
     */
    @Schema(description = "发货数量")
    @TableField("send_count")
    private BigDecimal sendCount;


    /**
     * 发货时间
     */
    @Schema(description = "发货时间")
    @TableField("send_time")
    private LocalDateTime sendTime;


    /**
     * 发送人账号
     */
    @Schema(description = "发送人账号")
    @TableField("send_acct_no")
    private String sendAcctNo;


    /**
     * 发送人名称
     */
    @Schema(description = "发送人名称")
    @TableField("send_acct_name")
    private String sendAcctName;

}

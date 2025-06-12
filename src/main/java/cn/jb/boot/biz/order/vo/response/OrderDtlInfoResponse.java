package cn.jb.boot.biz.order.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细表表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-05 19:00:38
 */
@Data
@Schema(name = "OrderDtlInfoResponse", description = "订单明细表 详情返回信息")
public class OrderDtlInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String id;

    /**
     * 生产单号
     */
    @Schema(description = "生产单号")
    private String orderNo;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    private String itemNo;

    /**
     * 产品数量
     */
    @Schema(description = "产品数量")
    private BigDecimal itemCount;

    /**
     * 已生产数量
     */
    @Schema(description = "已生产数量")
    private BigDecimal productionCount;

    /**
     * 订单明细状态;01:就绪，02:排程中
     */
    @Schema(description = "订单明细状态;01:就绪，02:排程中")
    private String orderDtlStatus;

    /**
     * 待计算中间件数量
     */
    @Schema(description = "待计算中间件数量")
    private BigDecimal needMidCount;

}

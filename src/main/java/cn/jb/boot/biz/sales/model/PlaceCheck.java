package cn.jb.boot.biz.sales.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author user
 * @description
 * @date 2022年10月02日 12:27
 */
@Data
public class PlaceCheck {
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo;

    /**
     * bom编号
     */
    @Schema(description = "产品编码")
    private String itemNo;

    /**
     * 需求量
     */
    @Schema(description = "需求量")
    private BigDecimal needNum;


    /**
     * 下单数量
     */
    @Schema(description = "下单数量")
    private BigDecimal orderedNum;

}

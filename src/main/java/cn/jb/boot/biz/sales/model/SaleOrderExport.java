package cn.jb.boot.biz.sales.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售单表 导出
 *
 * @author X
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-02 12:29:38
 */
@Data
public class SaleOrderExport {

    /**
     * 销售单号
     */
    @Schema(description = "销售单号")
    private String orderNo;

    /**
     * 客户名称
     */
    @Schema(description = "客户名称")
    private String custName;

    /**
     * bom编号
     */
    @Schema(description = "bom编号")
    private String bomNo;

    /**
     * 需求量
     */
    @Schema(description = "需求量")
    private BigDecimal needNum;

    /**
     * 已下单数量
     */
    @Schema(description = "已下单数量/待下单")
    private String orderedNum;

    /**
     * 已生产数量
     */
    @Schema(description = "已生产数量/待生产")
    private String producedNum;

}

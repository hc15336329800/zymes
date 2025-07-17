package cn.jb.boot.biz.sales.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售单表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 21:12:37
 */
@Data
@Schema(name = "SaleOrderInfoResponse", description = "销售单 详情返回信息")
public class SaleOrderInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

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
     * 产品编码
     */
    @Schema(description = "产品编码")
    private String itemNo;

    /**
     * 需求量
     */
    @Schema(description = "需求量")
    private BigDecimal needNum;

    /**
     * 已审核的下单数量
     */
    @Schema(description = "已审核的下单数量")
    private BigDecimal approvedOrderedNum;

    /**
     * 已生产数量
     */
    @Schema(description = "已生产数量")
    private BigDecimal producedNum;

    /**
     * 工序编码
     */
    @Schema(description = "工序编码")
    private String procedureCode;

    /**
     * 工序名称
     */
    @Schema(description = "工序名称")
    private String procedureName;


}

package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * author: zhou.yun
 * date: 2022/10/3 17:22
 * description: 发送确认对象
 */
@Data
public class DeliveryAuditReq {

    /**
     * 发货单Id
     */
    @Schema(description = "发货单Id")
    private String deliveryId;

    /**
     * 图纸号
     */
    @Schema(description = "图纸号")
    private String bomNo;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    private String itemNo;

    /**
     * 客户名称
     */
    @Schema(description = "客户名称")
    private String custName;

    /**
     * 发货数量
     */
    @Schema(description = "申请发货数量")
    private BigDecimal applyCount;

    /**
     * 发货数量
     */
    @Schema(description = "发货数量")
    private BigDecimal sendCount;

}

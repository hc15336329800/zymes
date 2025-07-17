package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发货表表
 *
 * @author X
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-01 12:29:38
 */
@Data
public class DeliveryPageReq {

    /**
     * 图纸号
     */
    @Schema(description = "图纸号")
    private String bomNo;

    /**
     * 接受客户
     */
    @Schema(description = "接收客户")
    private String custName;

    /**
     * 申请人
     */
    @Schema(description = "申请人")
    private String applyUser;

    @Schema(description = "发送人")
    private String sendAcctName;
    /**
     * 发货单状态
     */
    @Schema(description = "发货单状态")
    private String deliveryStatus;

    @Schema(description = "申请日期 开始时间 yyyy-MM-dd")
    private String applyStartDate;

    @Schema(description = "申请日期 结束时间 yyyy-MM-dd")
    private String applyEndDate;


    @Schema(description = "发货日期 开始时间 yyyy-MM-dd")
    private String sendStartDate;

    @Schema(description = "发货日期 结束时间 yyyy-MM-dd")
    private String sendEndDate;
    @Schema(description = "库存量")
    private BigDecimal itemCount;

}

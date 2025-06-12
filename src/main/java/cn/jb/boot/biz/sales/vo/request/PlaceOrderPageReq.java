package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 发货表表
 *
 * @author X
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-01 12:29:38
 */
@Data
public class PlaceOrderPageReq {


    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo;
    /**
     * 申请人
     */
    @Schema(description = "申请人")
    private String applyUser;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;

    /**
     * 发货单状态
     */
    @Schema(description = "审批状态 00 待处理 其他已处理")
    @NotBlank(message = "审批状态不能为空")
    private String placeStatus;

    @Schema(description = "申请日期 开始时间 yyyy-MM-dd")
    private String applyStartDate;

    @Schema(description = "申请日期 结束时间 yyyy-MM-dd")
    private String applyEndDate;


    @Schema(description = "承诺交期 开始时间 yyyy-MM-dd")
    private String deliverStartDate;

    @Schema(description = "承诺交期 结束时间 yyyy-MM-dd")
    private String deliverEndDate;
    @Schema(description = "产品编码")
    private List<String> itemNos;

}

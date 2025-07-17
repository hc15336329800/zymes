package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DeliveryOrderUpdateReq {

    @Schema(description = "主键")
    private String id;

    /**
     * 目的地
     */
    @Schema(description = "目的地")
    private String destination;

    /**
     * 客户
     */
    @Schema(description = "客户")
    private String customer;

    /**
     * 发货日期
     */
    @Schema(description = "发货日期")
    private LocalDate deliveryDate;

    /**
     * 发货人
     */
    @Schema(description = "发货人")
    private String deliverer;

    /**
     * 接收人
     */
    @Schema(description = "接收人")
    private String acceptedBy;

    /**
     * 门卫
     */
    @Schema(description = "门卫")
    private String doorman;

    /**
     * 制作单人
     */
    @Schema(description = "制作单人")
    private String preparedBy;

    /**
     * 质检员
     */
    @Schema(description = "质检员")
    private String qualityBy;

    /**
     * 用框量
     */
    @Schema(description = "用框量")
    private String boxNum;

    @Schema(description = "表单明细")
    private List<DeliveryDtlUpdateReq> dtls;


}

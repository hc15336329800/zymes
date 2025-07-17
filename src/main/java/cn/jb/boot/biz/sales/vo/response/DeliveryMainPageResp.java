package cn.jb.boot.biz.sales.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DeliveryMainPageResp {
    /**
     * 主键
     */
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

    @Schema(description = "状态 00 待确认，01已确认")
    private String status;


    @Schema(description = "提交状态 00 待提交，01 已提交")
    private String commitStatus;
    @Schema(description = "司机")
    private String driver;


}

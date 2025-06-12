package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DeliveryMainPageReq {
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
    @Schema(description = "发货日期 yyyy-MM-dd")
    private LocalDate startDate;
    /**
     * 发货日期
     */
    @Schema(description = "发货日期 yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "状态 00:待确认,01:已确认")
    private String status;
    @Schema(description = "司机")
    private String driver;

}

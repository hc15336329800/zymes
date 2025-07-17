package cn.jb.boot.biz.sales.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DeliveryMainResp {
    @Schema(description = "id")
    private String id;


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
     * 司机
     */
    @Schema(description = "司机")
    private String driver;

    @Schema(description = "单据表")
    private List<DeliveryOrderDetailResp> list;

}

package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class DeliveryBatchAddReq {

    @Schema(description = "目的地")
    @NotNull(message = "目的地不能为空")
    private String destination;


    /**
     * 客户
     */
    @Schema(description = "客户")
    @NotNull(message = "客户不能为空")
    private String customer;


    /**
     * 发货日期
     */
    @Schema(description = "发货日期")
    @NotNull(message = "发货日期不能为空")
    private LocalDate deliveryDate;


    /**
     * 司机
     */
    @Schema(description = "司机")
    @NotNull(message = "司机不能为空")
    private String driver;


    @Schema(description = "发货单数据")
    @Valid
    @NotNull
    private List<DeliveryOrderAddReq> list;
}

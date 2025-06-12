package cn.jb.boot.biz.sales.vo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author user
 * @description
 * @date 2022年10月01日 15:08
 */
@Data
public class SaleOrderPageReq {

    /**
     * 销售单号
     */
    @Schema(description = "订单号")
    private String orderNo;

    /**
     * 客户名称
     */
    @Schema(description = "客户名称")
    private String custName;


    @Schema(description = "创建时间开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "创建时间结束时间 yyyy-MM-dd")
    private String endTime;

    @JsonIgnore
    private List<String> ids;

    @Schema(description = "是否下单标志 00:不可下单，01：可下单")
    private String canPlace;

    @Schema(description = "产品编码")
    private String itemNo;
}

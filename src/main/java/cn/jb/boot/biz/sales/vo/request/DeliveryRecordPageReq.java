package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 发货表表
 *
 * @author X
 * @Description
 * @Copyright Copyright (c) 2022
 * @since 2022-10-01 12:29:38
 */
@Data
public class DeliveryRecordPageReq {

    /**
     * 图纸号
     */
    @Schema(description = "产品编码")

    private String itemNo;
    /**
     * 接受客户
     */
    @Schema(description = "接收客户")
    private String custName;

    @Schema(description = "发货人")
    private String sendAcctName;

    @Schema(description = "发货日期 开始时间 yyyy-MM-dd")
    private String startDate;

    @Schema(description = "发货日期 结束时间 yyyy-MM-dd")
    private String endDate;

}

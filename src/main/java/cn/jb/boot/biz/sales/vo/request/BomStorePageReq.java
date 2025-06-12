package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BomStorePageReq {
    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    private String itemNo;

    /**
     * 出入库类型;01:出库，02:入库
     */
    @Schema(description = "出入库类型;01:出库，02:入库")
    private String bizType;

    /**
     * 状态;00:待确认，01:已确认
     */
    @Schema(description = "状态;00:待确认，01:已确认")
    private String storeStatus;


    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;
}

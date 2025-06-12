package cn.jb.boot.biz.production.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 生产任务单表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-04 17:38:37
 */
@Data
@Schema(name = "ProductionOrderPageRequest", description = "生产任务单 分页请求参数")
public class ProductionOrderPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String salesOrderNo;
    /**
     * BOM编码
     */
    @Schema(description = "产品编码")
    private List<String> itemNos;

    @Schema(description = "业务类型，01:销售单 02:加急单03:追加计划，04:月度单")
    private String bizType;
    @Schema(description = "订单明细状态;01:就绪，02:排程中,03:待生产,04:生产中,05:待发货,06:关闭,07:暂停")
    private String status;


    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;
}

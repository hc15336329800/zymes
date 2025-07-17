package cn.jb.boot.biz.order.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DistInfoResponse {
    @Schema(description = "主键ID")
    private String id;
    @Schema(description = "工序编码")
    private String procedureCode;
    @Schema(description = "工序名称")
    private String procedureName;
    @Schema(description = "产品编码")
    @DictTrans(type = DictType.BOM_NO, name = "bomNo")
    private String itemNo;
    @Schema(description = "工序总数")
    private BigDecimal totalCount;
    @Schema(description = "外协分配数量")
    private BigDecimal outerAllocCount;
    @Schema(description = "工人已分配数量")
    private BigDecimal workerAllocCount;
    @Schema(description = "已生产数量")
    private BigDecimal prodCount;
    @Schema(description = "待分配数量")
    private BigDecimal waitAllocCount;
    @Schema(description = "订单工序总数")
    private BigDecimal orgiTotalCount;
    @Schema(description = "中间件使用件数")
    private BigDecimal midCount;
    @Schema(description = "设备")
    @DictTrans(type = DictType.DEVICE, name = "deviceName")
    private String deviceId;

    @Schema(description = "id")
    private String workId;

    @Schema(description = "工单号")
    private String workOrderNo;

    @Schema(description = "设备")
    @DictTrans(type = DictType.DEVICE, name = "workDeviceName")
    private String workDeviceId;


    @Schema(description = "设备名称")
    private String deviceName;


    @Schema(description = "分配数量")
    private String workItemCount;

    @Schema(description = "报工数量")
    private String workReportCount;
}

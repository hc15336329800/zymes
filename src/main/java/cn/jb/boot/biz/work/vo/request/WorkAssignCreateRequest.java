package cn.jb.boot.biz.work.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 工单下达表表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Data
@Schema(name = "WorkAssignCreateRequest", description = "工单下达表 新增请求参数")
public class WorkAssignCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单Id
     */
    @Schema(description = "工单Id")
    @NotEmpty(message = "工单ID不能为空")
    private String workOrderId;

    /**
     * 下达数量
     */
    @Schema(description = "下达数量")
    @NotNull(message = "下达数量不能为空")
    private BigDecimal assignCount;

    /**
     * 物料名称
     */
    @Schema(description = "物料名称")
    private String itemNo;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 图纸号
     */
    @Schema(description = "图纸号")
    private String bomNo;

}

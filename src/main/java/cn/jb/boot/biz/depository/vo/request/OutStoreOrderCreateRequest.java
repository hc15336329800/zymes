package cn.jb.boot.biz.depository.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出库单表表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Data
@Schema(name = "OutStoreOrderCreateRequest", description = "出库单表 新增请求参数")
public class OutStoreOrderCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单表ID或外协任务Id
     */
    @Schema(description = "工单表ID或外协任务Id")
    private String bizId;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    private String itemNo;

    /**
     * 部门Id
     */
    @Schema(description = "部门Id")
    private String deptId;

    /**
     * 出库状态;出库状态 00：待出库，01：已出库
     */
    @Schema(description = "出库状态;出库状态 00：待出库，01：已出库")
    private String outStatus;

    /**
     * 领料时间
     */
    @Schema(description = "领料时间")
    private LocalDateTime outTime;

    /**
     * 领料人
     */
    @Schema(description = "领料人")
    private String outUser;

    /**
     * 下达数量
     */
    @Schema(description = "下达数量")
    private BigDecimal assignCount;

    /**
     * 用料编码
     */
    @Schema(description = "用料编码")
    private String useItemNo;

    /**
     * 预估数量
     */
    @Schema(description = "预估数量")
    private BigDecimal planCount;

    /**
     * 实际数量
     */
    @Schema(description = "实际数量")
    private BigDecimal realCount;

    /**
     * 审核状态
     */
    @Schema(description = "审核状态")
    private String reviewStatus;

    /**
     * 审核人
     */
    @Schema(description = "审核人")
    private String reviewBy;

}

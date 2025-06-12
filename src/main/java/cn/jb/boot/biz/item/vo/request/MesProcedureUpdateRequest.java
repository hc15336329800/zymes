package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 工序表表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 00:00:56
 */
@Data
@Schema(name = "MesProcedureUpdateRequest", description = "工序表 修改请求参数")
public class MesProcedureUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;

    /**
     * 产品编码
     */
    @Schema(description = "产品编码")
    private String itemNo;

    /**
     * 序号，执行顺序
     */
    @Schema(description = "序号，执行顺序")
    private Integer seqNo;

    /**
     * 工序编码
     */
    @Schema(description = "工序编码")
    private String procedureCode;

    /**
     * 工序名称
     */
    @Schema(description = "工序名称")
    private String procedureName;

    /**
     * 加工工时
     */
    @Schema(description = "加工工时")
    private BigDecimal hoursWork;

    /**
     * 工作车间Id
     */
    @Schema(description = "工作车间Id")
    private String deptId;

    /**
     * 设备Id
     */
    @Schema(description = "设备Id")
    private String deviceId;

    /**
     * 额定工时
     */
    @Schema(description = "额定工时")
    private BigDecimal hoursFixed;

    /**
     * 准备工时
     */
    @Schema(description = "准备工时")
    private BigDecimal hoursPrepare;

    /**
     * 简码
     */
    @Schema(description = "简码")
    private String shortCode;

}

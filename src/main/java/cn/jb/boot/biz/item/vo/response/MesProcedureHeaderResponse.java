package cn.jb.boot.biz.item.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 工序表表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 00:00:56
 */
@Data
@Schema(name = "MesProcedureInfoResponse", description = "工序表 详情返回信息")
public class MesProcedureHeaderResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    @Schema(description = "图纸号")
    private String bomNo;

    /**
     * 加工工时
     */
    @Schema(description = "加工工时")
    private BigDecimal totalHoursWork;
    /**
     * 额定工时
     */
    @Schema(description = "额定工时")
    private BigDecimal totalHoursFixed;


}

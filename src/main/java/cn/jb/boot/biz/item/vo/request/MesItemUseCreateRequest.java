package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品用料表表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-12-28 17:04:05
 */
@Data
@Schema(name = "MesItemUseCreateRequest", description = "产品用料表 新增请求参数")
public class MesItemUseCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "产品编码")
    @NotBlank(message = "产品编码不能为空")
    private String itemNo;
    @Schema(description = "用料编码")
    @NotBlank(message = "用料编码不能为空")
    private String useItemNo;
    @Schema(description = "固定用量")
    @NotNull(message = "固定用量不能为空")
    private BigDecimal fixedUse;
    @Schema(description = "辅助用量")
    private BigDecimal fixedUseAssist;
    @Schema(description = "辅助用量单位")
    private String itemMeasureAssist;


}

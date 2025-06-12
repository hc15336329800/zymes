package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author user
 * @description
 * @date 2022年10月01日 15:08
 */
@Data
public class DeliveryBatchAddInfos {

    /**
     * bom编号
     */
    @NotBlank(message = "图纸号不能为空")
    @Schema(description = "图纸号")
    private String bomNo;
    /**
     * 接收客户
     */
    @NotBlank(message = "接收客户不能为空")
    @Schema(description = "接收客户")
    private String custName;

    /**
     * 发货数量
     */
    @NotNull(message = "发货数量不能为空")
    @Schema(description = "发货数量")
    private BigDecimal applyCount;
    @Schema(description = "货车")
    private String truck;

}

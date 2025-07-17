package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author user
 * @description
 * @date 2022年10月01日 20:30
 */
@Data
public class PlaceOrderInfos {

    @NotBlank(message = "销售单id不能为空")
    @Schema(description = "销售单id")
    private String id;

    /**
     * 已下单数量
     */
    @NotNull(message = "下单数量不能为空")
    @Schema(description = "下单数量")
    private BigDecimal orderedNum;
}

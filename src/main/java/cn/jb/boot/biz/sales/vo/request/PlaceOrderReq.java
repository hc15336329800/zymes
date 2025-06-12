package cn.jb.boot.biz.sales.vo.request;

import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

/**
 * @author user
 * @description
 * @date 2022年10月01日 20:30
 */
@Data
public class PlaceOrderReq {

    @Schema(description = "承诺交期")
    @JsonFormat(pattern = DateUtil.DATE_PATTERN)
    private LocalDate deliverTime;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    @Valid
    @Schema(description = "销售单明细")
    private List<PlaceOrderInfos> list;
}

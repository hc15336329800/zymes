package cn.jb.boot.biz.order.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DistOuterReq {

    @Schema(description = "外协分配详情不能为空")
    private List<DistDtl> list;


    @Data
    public static class DistDtl {
        @Schema(description = "列表id")
        @NotNull(message = " id 不能为空")
        private String id;

        @Schema(description = "外协分配数量")
        @NotNull(message = "外协分配数量不为空")
        private BigDecimal count;
    }


}

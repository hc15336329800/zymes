package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ShortCodeReq {
    @Schema(description = "简码")
    @NotNull(message = "简码不能为空")
    public String shortCode;

    @Schema(description = "产品编码不能为空")
//    @NotEmpty(message = "产品编码不能为空")
    private String itemNo;

}

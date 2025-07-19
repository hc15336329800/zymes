package cn.jb.boot.biz.item.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)                // ← 新增：开启链式 setter
public class UseItemTreeResp {

    @Schema(description = "产品编码")
    private String itemNo;
    @Schema(description = "产品名称")
    private String itemName;
    @Schema(description = "父编码")
    private String parentCode;
    @Schema(description = "Id")
    private String usedId;
    @Schema(description = "用料明细")
    private List<UseItemTreeResp> children;

    private BigDecimal fixedUsed;

    private String bomNo;
    private String itemType;
}

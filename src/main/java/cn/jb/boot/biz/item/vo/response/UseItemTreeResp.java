package cn.jb.boot.biz.item.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
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

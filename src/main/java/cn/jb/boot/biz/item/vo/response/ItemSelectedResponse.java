package cn.jb.boot.biz.item.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ItemSelectedResponse {
    @Schema(description = "物料编码")
    private String itemNo;
    @Schema(description = "物料名称")
    private String itemName;
    @Schema(description = "图纸号")
    private String bomNo;

    public String getItemName() {
        if (StringUtils.isNotBlank(bomNo)) {
            return itemName + "(" + bomNo + ")";
        }
        return itemName + "(" + itemNo + ")";

    }
}

package cn.jb.boot.biz.item.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResp {
    @Schema(description = "产品编码")
    private String itemNo;
    @Schema(description = "产品名称")
    private String name;


}

package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ItemNoSelectedRequest {

    @Schema(description = "物料编码")
    private String itemNo;
}

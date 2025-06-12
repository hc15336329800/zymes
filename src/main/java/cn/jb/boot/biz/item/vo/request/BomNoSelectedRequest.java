package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BomNoSelectedRequest {
    @Schema(description = "图纸号")
    private String bomNo;
}

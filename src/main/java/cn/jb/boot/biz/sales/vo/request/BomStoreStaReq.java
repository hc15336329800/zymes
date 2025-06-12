package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BomStoreStaReq {

    @Schema(description = "年份")
    private String year;
}

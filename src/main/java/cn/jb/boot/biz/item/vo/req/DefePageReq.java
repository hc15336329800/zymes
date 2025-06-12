package cn.jb.boot.biz.item.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DefePageReq {

    @Schema(description = "图纸号")
    private String bomNo;

    @Schema(description = "业务类型 00 出库，01 入库")
    private String bizType;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;
    @Schema(description = "图纸号")
    private List<String> bomNos;

}

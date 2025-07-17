package cn.jb.boot.biz.item.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProcListResp {
    @Schema(description = "工序名称")
    private String procedureName;


}

package cn.jb.boot.biz.item.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ItemProcedureResponse {

    private String itemNo;

    @Schema(description = "工序名称")
    private String procedureName;

    private String procedureCode;

    private String Id;


}

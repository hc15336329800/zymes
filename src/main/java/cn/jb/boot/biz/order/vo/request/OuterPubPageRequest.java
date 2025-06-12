package cn.jb.boot.biz.order.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class OuterPubPageRequest {


    private String itemNo;

    @Schema(description = "工序名称")
    private List<String> procedureNames;

    private String status;
}

package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AuditReq {

    @Schema(description = "发货单ID")
    private List<String> ids;
}

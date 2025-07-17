package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ComIdsReq {
    @NotNull
    @Schema(description = "id 列表")
    private List<String> ids;
}

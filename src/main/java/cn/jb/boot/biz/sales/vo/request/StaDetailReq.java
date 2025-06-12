package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class StaDetailReq {

    @Schema(description = "时间 yyyy-MM")
    @NotBlank(message = "时间不能为空")
    private String time;
}

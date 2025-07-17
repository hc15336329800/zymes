package cn.jb.boot.system.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictListResponse {
    @Schema(description = "枚举值编码")
    private String code;
    @Schema(description = "枚举值名称")
    private String name;
}

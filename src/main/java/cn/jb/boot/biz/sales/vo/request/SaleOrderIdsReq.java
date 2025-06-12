package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author user
 * @description
 * @date 2022年10月01日 15:08
 */
@Data
public class SaleOrderIdsReq {

    @NotNull(message = "id集合不能为空")
    @Size(min = 1, message = "必须选中一个元素")
    @Schema(description = "id集合")
    private List<String> list;
}

package cn.jb.boot.biz.sales.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author user
 * @description
 * @date 2022年10月02日 13:11
 */
@Data
public class PlaceRefuseReq {

    /**
     * 审批原因
     */
    @Schema(description = "拒绝原因")
    private String approvalMsg;

    @NotNull(message = "id集合不能为空")
    @Schema(description = "id集合")
    private List<String> list;
}

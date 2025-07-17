package cn.jb.boot.biz.group.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分组明细表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:26
 */
@Data
@Schema(name = "GroupDtlUpdateRequest", description = "分组明细 修改请求参数")
public class GroupDtlUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;

    /**
     * 分组id
     */
    @Schema(description = "分组id")
    private String groupId;

    /**
     * 用户Id
     */
    @Schema(description = "用户Id")
    private String userId;

    /**
     * 合共占比
     */
    @Schema(description = "合共占比")
    private BigDecimal percentage;

}

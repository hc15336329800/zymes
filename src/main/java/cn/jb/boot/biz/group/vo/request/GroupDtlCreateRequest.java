package cn.jb.boot.biz.group.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分组明细表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Data
@Schema(name = "GroupDtlCreateRequest", description = "分组明细 新增请求参数")
public class GroupDtlCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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

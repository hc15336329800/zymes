package cn.jb.boot.biz.outer.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 外协任务表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Data
@Schema(name = "OuterTaskUpdateRequest", description = "外协任务 修改请求参数")
public class OuterTaskUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @NotBlank(message = "id不能为空")
    private String id;

    /**
     * 外协账号
     */
    @Schema(description = "外协账号")
    private String userId;

    /**
     * 工序分配id
     */
    @Schema(description = "工序分配id")
    private String allocId;

    /**
     * 外协分配表Id
     */
    @Schema(description = "外协分配表Id")
    private String paoId;

    /**
     * 外协数量
     */
    @Schema(description = "外协数量")
    private BigDecimal outerCount;

    /**
     * 待验收数量
     */
    @Schema(description = "待验收数量")
    private BigDecimal waitRealCount;

    /**
     * 正品数量
     */
    @Schema(description = "正品数量")
    private BigDecimal realCount;

    /**
     * 待验收次品数量
     */
    @Schema(description = "待验收次品数量")
    private String waitDeffCount;

    /**
     * 次品数量
     */
    @Schema(description = "次品数量")
    private BigDecimal deffCount;

}

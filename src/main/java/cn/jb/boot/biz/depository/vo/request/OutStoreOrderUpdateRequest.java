package cn.jb.boot.biz.depository.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出库单表表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-12 15:08:36
 */
@Data
@Schema(name = "OutStoreOrderUpdateRequest", description = "出库单表 修改请求参数")
public class OutStoreOrderUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;

    /**
     * 部门Id
     */
    @Schema(description = "部门Id")
    private String deptId;
    /**
     * 领料人
     */
    @Schema(description = "领料人")
    private String outUser;

    /**
     * 用料编码
     */
    @Schema(description = "用料编码")
    private String useItemNo;
    /**
     * 预估数量
     */
    @Schema(description = "预估数量")
    private BigDecimal planCount;
    /**
     * 实际数量
     */
    @Schema(description = "实际数量")
    private BigDecimal realCount;


}

package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品用料表表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 12:56:58
 */
@Data
@Schema(name = "MesItemUseUpdateRequest", description = "产品用料表 修改请求参数")
public class MesItemUseUpdateRequest extends MesItemUseCreateRequest {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;


}

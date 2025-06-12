package cn.jb.boot.biz.item.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 工序表表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-03 00:00:56
 */
@Data
@Schema(name = "MesProcedurePageRequest", description = "工序表 分页请求参数")
public class MesProcedurePageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "物料编码")
    @NotEmpty(message = "物料编码不能为空!")
    private String itemNo;
}

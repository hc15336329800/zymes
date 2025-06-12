package cn.jb.boot.biz.outer.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 外协任务表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-10 10:44:22
 */
@Data
@Schema(name = "OuterTaskCreateRequest", description = "外协任务 新增请求参数")
public class OuterTaskCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "id不能为空")
    private String id;

    private List<OuterTaskDtlRequest> list;

}

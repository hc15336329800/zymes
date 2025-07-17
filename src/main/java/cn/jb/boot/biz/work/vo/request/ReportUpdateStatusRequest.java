package cn.jb.boot.biz.work.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 工单报工表表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-09 14:06:38
 */
@Data
@Schema(name = "ReportUpdateStatusRequest", description = "工单报工表 修改请求参数")
public class ReportUpdateStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotNull(message = "id不能为空")
    private List<String> ids;
    @Schema(description = "主键")
    @NotEmpty(message = "审核状态")
    private String status;

    private String message;

}

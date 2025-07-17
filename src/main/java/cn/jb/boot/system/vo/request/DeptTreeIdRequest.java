package cn.jb.boot.system.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 校区信息表 新增请求参数
 *
 * @author xieyubin
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-09-06 11:22:14
 */
@Data
@Schema(description = "校区信息 新增请求参数")
public class DeptTreeIdRequest {

    /**
     * 父部门id
     */
    @Schema(description = "父部门id")
//    @NotBlank(message = "父部门id不能为空")
    private String paterId;
}

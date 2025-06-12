package cn.jb.boot.system.vo.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 部门信息表 新增请求参数
 *
 * @author xieyubin
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-09-06 11:22:14
 */
@Data
@Schema(name = "DeptInfoCreateRequest", description = "部门信息 新增请求参数")
public class DeptInfoCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 父部门id
     */
    @Schema(description = "父部门id")
    @NotBlank(message = "父Id不能为空")
    private String paterId;


    @Schema(description = "序号")
    @NotNull(message = "序号不能为空")
    private Integer seqNo;

    @NotEmpty(message = "部门类型不能为空")
    @Schema(description = "00:普通部门，01:车间")
    private String deptType;

    @NotEmpty(message = "负责人ID不能为空！")
    private String directorUid;

}

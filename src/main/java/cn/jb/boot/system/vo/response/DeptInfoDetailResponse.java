package cn.jb.boot.system.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeptInfoDetailResponse {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;


    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    private String deptName;


    /**
     * 父部门id
     */
    @Schema(description = "父部门id")
    private String paterId;

    @Schema(description = "序号")
    private Integer seqNo;

    @Schema(description = "负责人Id")
    private String directorUid;
    @NotEmpty(message = "部门类型不能为空")
    @Schema(description = "00:普通部门，01:车间")
    private String deptType;
}

package cn.jb.boot.biz.group.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 工人分组表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Data
@Schema(name = "GroupInfoUpdateRequest", description = "工人分组 修改请求参数")
public class GroupInfoUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;

    /**
     * 组名
     */
    @Schema(description = "组名")
    private String groupName;

    /**
     * 组报人id
     */
    @Schema(description = "组报人id")
    private String groupUid;

    /**
     * 车间id
     */
    @Schema(description = "车间id")
    private String deptId;

    private String remark;

}

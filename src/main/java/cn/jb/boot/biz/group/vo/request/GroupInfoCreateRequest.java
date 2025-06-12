package cn.jb.boot.biz.group.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 工人分组表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Data
@Schema(name = "GroupInfoCreateRequest", description = "工人分组 新增请求参数")
public class GroupInfoCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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

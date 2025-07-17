package cn.jb.boot.biz.group.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 工人分组表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Data
@Schema(name = "GroupInfoInfoResponse", description = "工人分组 详情返回信息")
public class GroupInfoInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
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

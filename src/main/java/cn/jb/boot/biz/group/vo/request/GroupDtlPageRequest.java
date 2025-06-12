package cn.jb.boot.biz.group.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分组明细表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Data
@Schema(name = "GroupDtlPageRequest", description = "分组明细 分页请求参数")
public class GroupDtlPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;

    private String groupId;
}

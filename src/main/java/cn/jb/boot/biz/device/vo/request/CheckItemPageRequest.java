package cn.jb.boot.biz.device.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 质检项目表 分页请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@Data
@Schema(name = "CheckItemPageRequest", description = "质检项目 分页请求参数")
public class CheckItemPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;


    private String checkType;

    private List<String> ids;
}

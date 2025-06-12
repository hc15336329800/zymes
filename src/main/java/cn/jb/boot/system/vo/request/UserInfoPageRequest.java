package cn.jb.boot.system.vo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息表表 分页请求参数
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-03-24 14:52:38
 */
@Data
@Schema(name = "UserInfoPageRequest", description = "用户信息表 分页请求参数")
public class UserInfoPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名称")
    private String nickName;

    @Schema(description = "用户状态")
    private String dataStatus;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "开始时间 yyyy-MM-dd")
    private String beginTime;

    @Schema(description = "结束时间 yyyy-MM-dd")
    private String endTime;

    @Schema(description = "部门Id")
    private String deptId;

    @JsonIgnore
    private List<String> deptIds;
}

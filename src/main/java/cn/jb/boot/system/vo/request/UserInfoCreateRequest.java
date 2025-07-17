package cn.jb.boot.system.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 用户信息表表 新增请求参数
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-03-24 14:52:38
 */
@Data
@Schema(name = "UserInfoCreateRequest", description = "用户信息表 新增请求参数")
public class UserInfoCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    @NotBlank(message = "用户账号不能为空")
    private String userName;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    @NotBlank(message = "用户昵称不能为空")
    private String nickName;


    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "部门ID不能为空")
    @Schema(description = "部门ID")
    private String deptId;


    @Schema(description = "角色集合")
    @NotNull(message = "角色不能为空")
    @Size(min = 1, max = 500, message = "角色最大")
    private List<String> roleIds;

    @Schema(description = "备注")
    private String remark;


    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}

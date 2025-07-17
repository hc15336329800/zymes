package cn.jb.boot.system.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 用户信息表表 修改请求参数
 *
 * @author xyb
 * @Description
 * @Copyright Copyright (c) 2023
 * @since 2023-03-24 14:52:38
 */
@Data
@Schema(name = "UserInfoUpdateRequest", description = "用户信息表 修改请求参数")
public class UserInfoUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @NotBlank(message = "id不能为空")
    private String id;
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
    private String mobile;

    @Schema(description = "部门ID")
    private String deptId;

    /**
     * 帐号状态;00正常 01停用
     */
    @Schema(description = "帐号状态;00正常 01停用")
    private String dataStatus;

    @Schema(description = "角色集合")
    private List<String> roleIds;

    @Schema(description = "备注")
    private String remark;

}

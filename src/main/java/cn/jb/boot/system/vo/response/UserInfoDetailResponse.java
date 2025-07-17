package cn.jb.boot.system.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


/**
 * 用户详细详细
 *
 * @Author: xyb
 * @Description:
 * @Date: 2023-04-11 下午 08:58
 **/
@Data
public class UserInfoDetailResponse {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String id;
    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
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


    /**
     * 帐号状态;00正常 01停用
     */
    @Schema(description = "帐号状态;00正常 01停用")
    private String dataStatus;

    @Schema(description = "角色集合")
    private List<String> roleIds;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "部门ID")
    private String deptId;
}

package cn.jb.boot.system.vo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色分页查询
 *
 * @Author: xyb
 * @Description:
 * @Date: 2023-04-11 下午 08:56
 **/
@Data
public class RoleInfoPageRequest {

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @JsonIgnore
    private String userId;

    /**
     * 用户角色
     */
    @JsonIgnore
    private String dataRole;
}

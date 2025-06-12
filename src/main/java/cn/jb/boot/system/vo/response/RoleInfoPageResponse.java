package cn.jb.boot.system.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色分页
 *
 * @Author: xyb
 * @Description:
 * @Date: 2023-04-11 下午 08:56
 **/
@Data
public class RoleInfoPageResponse {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;


    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    private String roleCode;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    private String roleDesc;

}

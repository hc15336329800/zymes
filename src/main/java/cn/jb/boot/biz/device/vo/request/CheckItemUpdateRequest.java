package cn.jb.boot.biz.device.vo.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 质检项目表 修改请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@Data
@Schema(name = "CheckItemUpdateRequest", description = "质检项目 修改请求参数")
public class CheckItemUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    @NotBlank(message = "id不能为空")
    private String id;

    /**
     * 点检类型;01:点检，02:保养
     */
    @Schema(description = "点检类型;01:点检，02:保养")
    private String checkType;

    /**
     * 项目内容
     */
    @Schema(description = "项目内容")
    private String checkContent;

    /**
     * 标准
     */
    @Schema(description = "标准")
    private String checkStandard;

    @TableField("remark")
    private String remark;

}

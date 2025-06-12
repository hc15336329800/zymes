package cn.jb.boot.biz.device.vo.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 质检项目表 新增请求参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@Data
@Schema(name = "CheckItemCreateRequest", description = "质检项目 新增请求参数")
public class CheckItemCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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

package cn.jb.boot.biz.device.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 质检项目表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@Data
@Schema(name = "CheckItemPageResponse", description = "质检项目 分页返回参数")
public class CheckItemPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 点检类型;01:点检，02:保养
     */
    @Schema(description = "点检类型;01:点检，02:保养")
    @DictTrans(type = DictType.CHECK_TYPE)
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

    @Schema(description = "备注")
    private String remark;

    @DictTrans(type = DictType.USER_INFO)
    private String createdBy;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
}

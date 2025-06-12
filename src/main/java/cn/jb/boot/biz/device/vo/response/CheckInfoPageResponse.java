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
 * 点检信息表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-01 20:04:08
 */
@Data
@Schema(name = "CheckInfoPageResponse", description = "点检信息 分页返回参数")
public class CheckInfoPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 计划名称
     */
    @Schema(description = "计划名称")
    private String name;

    /**
     * 类型;01:点检计划，02:保养计划
     */
    @Schema(description = "类型;01:点检计划，02:保养计划")
    @DictTrans(type = DictType.CHECK_TYPE)
    private String checkType;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;


    @Schema(description = "设备编号")
    private String deviceNo;
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 项目Id
     */
    @Schema(description = "项目Id")
    private String itemId;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 用户Id
     */
    @Schema(description = "用户Id")
    @DictTrans(type = DictType.USER_INFO)
    private String userId;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;


    private String remark;
}

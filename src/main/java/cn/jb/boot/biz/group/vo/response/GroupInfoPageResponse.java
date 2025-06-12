package cn.jb.boot.biz.group.vo.response;

import cn.hutool.core.lang.Dict;
import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工人分组表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Data
@Schema(name = "GroupInfoPageResponse", description = "工人分组 分页返回参数")
public class GroupInfoPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 组名
     */
    @Schema(description = "组名")
    private String groupName;

    /**
     * 组报人id
     */
    @Schema(description = "组报人id")
    @DictTrans(type = DictType.USER_INFO, name = "groupUserName")
    private String groupUid;

    /**
     * 车间id
     */
    @Schema(description = "车间id")
    @DictTrans(type = DictType.WORK_SHOP, name = "deptName")
    private String deptId;

    private String remark;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;
}

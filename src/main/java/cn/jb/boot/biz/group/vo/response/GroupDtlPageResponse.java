package cn.jb.boot.biz.group.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.util.DateUtil;
import cn.jb.boot.util.SnowFlake;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 分组明细表 分页返回参数
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Data
@Schema(name = "GroupDtlPageResponse", description = "分组明细 分页返回参数")
public class GroupDtlPageResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 分组id
     */
    @Schema(description = "分组id")
    private String groupId;

    /**
     * 用户Id
     */
    @Schema(description = "用户Id")
    @DictTrans(type = DictType.USER_INFO, name = "userName")
    private String userId;

    /**
     * 合共占比
     */
    @Schema(description = "合共占比")
    private BigDecimal percentage;

    @DictTrans(type = DictType.LEADER_TYPE)
    private String leaderType;

    /**
     * 最后登陆时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdTime;


}

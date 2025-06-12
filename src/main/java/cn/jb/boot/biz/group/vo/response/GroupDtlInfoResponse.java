package cn.jb.boot.biz.group.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分组明细表 详情返回信息
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-06 20:38:27
 */
@Data
@Schema(name = "GroupDtlInfoResponse", description = "分组明细 详情返回信息")
public class GroupDtlInfoResponse implements Serializable {

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
    @DictTrans(type = DictType.LEADER_TYPE)
    private String leaderType;

    /**
     * 合共占比
     */
    @Schema(description = "合共占比")
    private BigDecimal percentage;

}

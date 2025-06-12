package cn.jb.boot.framework.com.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TreeSelect {
    @Schema(description = "id")
    private String id;
    @Schema(description = "其他参数")
    private String params;
    @Schema(description = "名称")
    private String label;
    @Schema(description = "是否选中")
    private boolean checked = false;
    @Schema(description = "父id")
    private String parId;


    @Schema(description = "创建时间")
    private LocalDateTime createdTime;


    @JsonIgnore
    private int level;
    @Schema(description = "序号")
    private int seqNo;


    @Schema(description = "子节点数据")
    @JsonInclude(Include.NON_EMPTY)
    private List<? extends TreeSelect> children;


}

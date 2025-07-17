package cn.jb.boot.framework.com.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author YX
 * @Description 树形结构
 * @Date 2021/8/17 23:59
 */
@Data
public class TreeNodes implements Serializable {

    private static final long serialVersionUID = 3233220019317816976L;
    /**
     * 编码
     */
    @Schema(description = "编码")
    @JsonProperty(value = "value")
    private String code;

    /**
     * 名称
     */
    @Schema(description = "名称")
    @JsonProperty(value = "label")
    private String name;

    /**
     * 父编码
     */
//    @JsonIgnore
    @Schema(description = "父编码")
    private String parCode;

    @JsonIgnore
    private String menuName;

    /**
     * 层级
     */
    @JsonIgnore
    private int level;

    /**
     * 排序
     */
    @JsonIgnore
    private int seqNo;

    /**
     * 子节点
     */
    private List<? extends TreeNodes> children;

}

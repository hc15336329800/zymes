package cn.jb.boot.framework.com.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author YX
 * @Description 分页信息
 * @Date 2021/8/18 0:01
 */
@Data
public class PagingResponse {

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private long pages;
    /**
     * 当前页数
     */
    @JsonProperty("page_num")
    @Schema(description = "当前页数")
    private long pageNum;
    /**
     * 每页的数量
     */
    @JsonProperty("page_size")
    @Schema(description = "每页数量")
    private long pageSize;
    /**
     * 总数量
     */
    @JsonProperty("total_num")
    @Schema(description = "总数量")
    private long totalNum;
}

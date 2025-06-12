package cn.jb.boot.framework.com.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author YX @Date 2021/8/18 0:00
 * @Description 分页信息
 */
public class Paging {

    /**
     * 分页当前页
     */
    @JsonProperty("page_num")
    @Schema(description = "分页当前页")
    private Integer pageNum;

    /**
     * 分页大小
     */
    @JsonProperty("page_size")
    @Schema(description = "分页大小")
    private Integer pageSize;

    public int getPageNum() {
        if (pageNum == null) {
            pageNum = 1;
        }
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        if (pageSize == null) {
            pageSize = 10;
        }
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}

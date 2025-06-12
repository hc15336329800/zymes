package cn.jb.boot.framework.com.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;

/**
 * @author YX @Date 2021/8/18 0:00
 * @Description 请求基类
 */
@Data
public class BaseRequest<T> {

    @Schema(description = "分页对象")
    private Paging page;

    @Valid
    @Schema(description = "请求参数")
    private T params;
}

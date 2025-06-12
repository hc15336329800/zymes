package cn.jb.boot.system.controller;


import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.service.DictDataService;
import cn.jb.boot.system.vo.response.DictListResponse;
import cn.jb.boot.util.MsgUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 标准数据接口
 *
 * @Author: xyb
 * @Description:
 * @Date: 2022-11-21 上午 11:18
 **/
@RestController
@RequestMapping("/api/dict")
@Tag(name = "枚举值查询", description = "枚举值查询")
public class DictDataController {


    @Resource
    private DictDataService dictDataService;

    /**
     * 根据类型获取到对应的枚举值
     *
     * @param request 查询参数
     * @return
     */
    @PostMapping("/list")
    @Operation(summary = "获取枚举值", description = "根据类型获取到对应的枚举值，传入'JB_ROOT' 查询所有的枚举类型")
    public BaseResponse<List<DictListResponse>> dictList(@RequestBody @Valid BaseRequest<String> request) {
        String type = MsgUtil.params(request);
        List<DictListResponse> result = dictDataService.dictList(type);
        return MsgUtil.ok(result);
    }

}

package cn.jb.boot.biz.work.controller;

import cn.jb.boot.biz.work.service.WorkOrderRecordService;
import cn.jb.boot.biz.work.vo.request.WorkOrderRecordPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkOrderRecordPageResponse;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
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
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-08 18:34:33
 */
@RestController
@RequestMapping("/api/work/work_order_record")
@Tag(name = "work-order-record-controller", description = "工序分配记录接口")
public class WorkOrderRecordController {

    @Resource
    private WorkOrderRecordService service;


    /**
     * 分页查询信息
     *
     * @param request 查询参数
     * @return 分页记录
     */
    @PostMapping("/page_list")
    @Operation(summary = "查询工序分配记录分页列表")
    public BaseResponse<List<WorkOrderRecordPageResponse>> pageList(@RequestBody @Valid BaseRequest<WorkOrderRecordPageRequest> request) {
        WorkOrderRecordPageRequest params = MsgUtil.params(request);
        return service.pageInfo(request.getPage(), params);
    }
}

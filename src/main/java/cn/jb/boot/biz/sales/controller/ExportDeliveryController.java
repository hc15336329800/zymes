package cn.jb.boot.biz.sales.controller;

import cn.jb.boot.biz.sales.service.ExportDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/export/delivery")
@Tag(name = "发运单导出", description = "发运单导出")
public class ExportDeliveryController {


    @Resource
    ExportDeliveryService service;

    @GetMapping("/export")
    @Operation(summary = "发货明细导出")
    public void export(String id,
                       HttpServletResponse response) throws Exception {
        service.export(id, response);

    }

    @GetMapping("/exportMain")
    @Operation(summary = "发货明细导出")
    public void exportMain(String id,
                           HttpServletResponse response) throws Exception {
        service.exportMain(id, response);

    }
}

package cn.jb.boot.biz.work.controller;

import cn.jb.boot.biz.work.service.WorkerReportDtlService;
import cn.jb.boot.biz.work.service.impl.WorkerReportDtlServiceImpl;
import cn.jb.boot.biz.work.vo.request.WorkerReportDetailPageRequest;
import cn.jb.boot.biz.work.vo.request.WorkerReportDtlPageRequest;
import cn.jb.boot.biz.work.vo.request.WorkerReportDtlUpdateRequest;
import cn.jb.boot.biz.work.vo.response.WorkerReportDtlPageResponse;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 前端控制器
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-11 15:32:29
 */
@RestController
@RequestMapping("/api/work/worker_report_dtl")
@Tag(name = "worker-report-dtl-controller", description = "工人报工明细接口")
public class WorkerReportDtlController {

	@Resource
	private WorkerReportDtlService service;


	@Resource
	private WorkerReportDtlServiceImpl iservice;


	/**
	 * 分页查询信息
	 *
	 * @param request 查询参数
	 * @return 分页记录
	 */
	@PostMapping("/page_list")
	@Operation(summary = "查询工人报工明细分页列表")
	public BaseResponse<List<WorkerReportDtlPageResponse>> pageList(@RequestBody @Valid BaseRequest<WorkerReportDtlPageRequest> request) {
		WorkerReportDtlPageRequest params = MsgUtil.params(request);
		return service.pageInfo(request.getPage(), params);
	}


	//    工人报工工资明细
	//	加工件数 user_count 来自 t_worker_report_dtl（别名 twrd）表；单价 hours_fixed 来自 t_work_order（别名 two）表。
	//	user_count 属于 t_worker_report_dtl，而 hours_fixed 属于 t_work_order。
	@PostMapping("/detail_page_list")
	@Operation(summary = "查询工人报工明细分页列表")
	public BaseResponse<List<WorkerReportDtlPageResponse>> detailPageList(@RequestBody @Valid BaseRequest<WorkerReportDetailPageRequest> request) {
		WorkerReportDetailPageRequest params = MsgUtil.params(request);
		return service.detailPageList(request.getPage(), params);
	}


	// 修改  后加

	@PostMapping("/update")
	@Operation(summary = "修改报工明细（必须填写备注）")
	public BaseResponse<Boolean> update(@RequestBody @Valid WorkerReportDtlUpdateRequest request) {
		return service.updateWorkerReportDtl(request);
	}

	/**
	 * 下载工人报工工资明细（按条件导出 Excel）
	 *
	 * @param request  查询参数，结构同 detail_page_list
	 * @param response HttpServletResponse
	 */
	@PostMapping("/download_salary")
	@Operation(summary = "导出工人报工工资明细")
	public void downloadSalary(@RequestBody @Valid WorkerReportDetailPageRequest request, HttpServletResponse response) {
		iservice.downloadSalary(request, response);
	}


	/**
	 * 导出全部工人的工资明细压缩包（按时间过滤，工资 > 0）
	 */
	@PostMapping("/download_salary_all_zip")
	@Operation(summary = "导出全部工人的工资明细")
	public void downloadSalaryAllZip(@RequestBody @Valid WorkerReportDetailPageRequest request,
									 HttpServletResponse response) throws IOException {
		iservice.downloadAllSalaryZip(request, response);
	}


	/**
	 * 导出全部工人的工资明细  都输出到一张表中
	 */
	@PostMapping("/download_salary_all_table")
	@Operation(summary = "导出全部工人的工资明细（单表）")
	public void downloadSalaryAllTable(@RequestBody @Valid WorkerReportDetailPageRequest request,
									   HttpServletResponse response) {
		iservice.downloadAllSalary(request, response);
	}

	/**
	 * 新增：下载工资汇总
	 */
	@PostMapping("/download_salary_summary")
	@Operation(summary = "导出工人工资汇总")
	public void downloadSalarySummary(
			@RequestBody @Valid WorkerReportDetailPageRequest request,
			HttpServletResponse response) {
		service.downloadSalarySummary(request, response);
	}

	@PostMapping("/export_order")
	@Operation(summary = "导出发运单")
	public void exportOrder(@RequestBody @Valid BaseRequest<WorkerReportDtlPageRequest> request, HttpServletResponse response) {
		service.exportOrder(MsgUtil.params(request), response);
	}
}

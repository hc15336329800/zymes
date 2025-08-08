package cn.jb.boot.biz.work.service.impl;

import cn.hutool.poi.excel.ExcelWriter;
import cn.jb.boot.biz.work.dto.WorkerReportSalaryExportDTO;
import cn.jb.boot.biz.work.dto.WorkerReportSalarySummaryDTO;
import cn.jb.boot.biz.work.entity.WorkerReportDtl;
import cn.jb.boot.biz.work.mapper.WorkerReportDtlMapper;
import cn.jb.boot.biz.work.service.WorkerReportDtlService;
import cn.jb.boot.biz.work.vo.request.WorkerReportDetailPageRequest;
import cn.jb.boot.biz.work.vo.request.WorkerReportDtlPageRequest;
import cn.jb.boot.biz.work.vo.response.WorkerReportDtlPageResponse;
import cn.jb.boot.framework.com.request.Paging;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.util.ExcelUtil;
import cn.jb.boot.util.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import cn.hutool.poi.excel.ExcelWriter;
import cn.jb.boot.util.FileUtil;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 工人报工明细 服务实现类
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-01-11 15:32:29
 */
@Service
public class WorkerReportDtlServiceImpl extends ServiceImpl<WorkerReportDtlMapper, WorkerReportDtl> implements WorkerReportDtlService {

	@Resource
	private WorkerReportDtlMapper mapper;


	/**
	 * 导出全部工人报工工资明细（按时间范围，过滤工资为 0 或为空的记录）
	 */
	public void downloadAllSalary(WorkerReportDetailPageRequest params, HttpServletResponse response) {
		try {
			List<WorkerReportSalaryExportDTO> list = mapper.detailPageListAll(params).stream()
					.filter(dto -> {
						try {
							return dto.getWages() != null
									&& Double.parseDouble(dto.getWages()) > 0;
						} catch (Exception e) {
							return false;
						}
					})
					.collect(Collectors.toList());

			ExcelUtil.writeExcel(response, "工人工资明细", w -> {
				w.renameSheet(0, "工资明细");
				writeSalaryData(w, list);
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	// 查询当日工资
	@Override
	public BaseResponse<List<WorkerReportDtlPageResponse>> pageInfo(Paging page, WorkerReportDtlPageRequest params) {
		PageUtil<WorkerReportDtlPageResponse, WorkerReportDtlPageRequest> pu = (p, q) -> mapper.pageInfo(p, q);
		return pu.page(page, params);
	}

	@Override
	public BaseResponse<List<WorkerReportDtlPageResponse>> detailPageList(Paging page, WorkerReportDetailPageRequest params) {
		PageUtil<WorkerReportDtlPageResponse, WorkerReportDetailPageRequest> pu = (p, q) -> mapper.detailPageList(p, q);
		return pu.page(page, params);
	}




	/**
	 * 导出全部工资明细（按条件过滤工资<=0的记录，按人分文件打包 ZIP）
	 */
	public void downloadAllSalaryZip(WorkerReportDetailPageRequest params,
									 HttpServletResponse response) throws IOException {
		try {
			// 1. 查询并过滤工资 > 0 的记录
			List<WorkerReportSalaryExportDTO> data = mapper.detailPageListAll(params).stream()
					.filter(dto -> {
						try {
							return dto.getWages() != null
									&& Double.parseDouble(dto.getWages()) > 0;
						} catch (Exception e) {
							return false;
						}
					}).collect(Collectors.toList());

			// 2. 先在内存中构造 ZIP
			ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
			try (ZipOutputStream zos = new ZipOutputStream(zipBaos)) {
				Map<String, List<WorkerReportSalaryExportDTO>> group =
						data.stream().collect(Collectors.groupingBy(WorkerReportSalaryExportDTO::getUserName));

				for (Map.Entry<String, List<WorkerReportSalaryExportDTO>> entry : group.entrySet()) {
					ByteArrayOutputStream excelBaos = new ByteArrayOutputStream();
					try (ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true)) {
						writer.renameSheet(0, "工资明细");
						writeSalaryData(writer, entry.getValue());
						writer.flush(excelBaos, true);          // 写入 Excel 到内存
					}
					zos.putNextEntry(new ZipEntry(entry.getKey() + ".xlsx"));
					zos.write(excelBaos.toByteArray());
					zos.closeEntry();
				}
				zos.finish();
			}

			// 3. 输出到客户端
			FileUtil.download(response, "工人工资明细.zip", zipBaos.toByteArray(), null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 修改：按请求参数导出工资汇总
	 */
	@Override
	public void downloadSalarySummary(WorkerReportDetailPageRequest params,
									  HttpServletResponse response) {
		try {
			// 使用前端传入的 beginTime/endTime 等条件
			List<WorkerReportSalarySummaryDTO> list = mapper.summaryByUser(params);
			ExcelUtil.writeExcel(response, "工人工资汇总", writer -> {
				writer.renameSheet(0, "工资汇总");
				writeSalarySummaryData(writer, list);
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 私有：写入工资汇总数据（表头 + 数据）
	 */
	private void writeSalarySummaryData(ExcelWriter writer,
										List<WorkerReportSalarySummaryDTO> list) {
		writer.addHeaderAlias("userId", "工人ID");
		writer.addHeaderAlias("userName", "工人姓名");
		writer.addHeaderAlias("totalWages", "总工资");
		writer.write(list, true);
	}


	/**
	 * 导出工人报工工资明细（按条件查询并导出Excel）
	 *
	 * @param params   WorkerReportDetailPageRequest 查询条件
	 * @param response HttpServletResponse
	 */
	public void downloadSalary(WorkerReportDetailPageRequest params, HttpServletResponse response) {

		try {
			List<WorkerReportSalaryExportDTO> list = mapper.detailPageListAll(params);
			ExcelUtil.writeExcel(response, "工人工资明细", w -> {
				w.renameSheet(0, "工资明细");
				writeSalaryData(w, list);
			});
		} catch (Exception e) {
			e.printStackTrace(); // 控制台可见具体报错
			throw e; // 保持原样
		}


	}


	/**
	 * 写入工资明细数据
	 */
	private void writeSalaryData(ExcelWriter writer, List<WorkerReportSalaryExportDTO> list) {

		writer.addHeaderAlias("orderNo", "订单号");
//        writer.addHeaderAlias("bomNo", "图纸号");
		writer.addHeaderAlias("itemNo", "产品号");
		writer.addHeaderAlias("workOrderNo", "工单号");
		writer.addHeaderAlias("procedureName", "工序名称");

		writer.addHeaderAlias("userId", "工人ID");
		writer.addHeaderAlias("userName", "工人姓名");

		writer.addHeaderAlias("userCount", "加工件数");
		writer.addHeaderAlias("hoursFixed", "单价");
		writer.addHeaderAlias("wages", "工资");

//        writer.addHeaderAlias("createDate", "创建日期");
		writer.addHeaderAlias("createdTime", "报工时间");
		writer.write(list, true);

		// 新增加一行：工资总额（假设工资字段名为wages）
		double total = list.stream()
				.filter(x -> x.getWages() != null && !"".equals(x.getWages()))
				.mapToDouble(x -> {
					try {
						return Double.parseDouble(x.getWages());
					} catch (Exception e) {
						return 0;
					}
				}).sum();
		// K列（下标10）写合计，前面补10个空字符串
		List<Object> sumRow = new ArrayList<>();
		for (int i = 0; i < 9; i++) sumRow.add("");
//        sumRow.add("工资合计：" + String.format("%.2f", total) + "元");   //四拾伍入
		sumRow.add("工资合计：" + total + "元");

		writer.writeRow(sumRow);
	}


	@Override
	public void exportOrder(WorkerReportDtlPageRequest params, HttpServletResponse response) {
		List<WorkerReportDtlPageRequest> workerReportDtlList = null;
		ExcelUtil.writeExcel(response, "工资单", w -> {
			w.renameSheet(0, "工资单");
			writeOrderData(w, workerReportDtlList);
		});
	}

	private void writeOrderData(ExcelWriter writer, List<WorkerReportDtlPageRequest> list) {
		writer.addHeaderAlias("userId", "工人");
		writer.addHeaderAlias("groupId", "分组");
		writer.addHeaderAlias("bomNo", "单价");
		writer.addHeaderAlias("needNum", "工资");
		writer.write(list, true);
	}
}

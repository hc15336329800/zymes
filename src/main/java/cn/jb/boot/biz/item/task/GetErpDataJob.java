package cn.jb.boot.biz.item.task;

import cn.jb.boot.biz.agvcar.service.AgvManageInfoService;
import cn.jb.boot.biz.item.service.impl.MesToErpDataService;
import cn.jb.boot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ERP基础数据跨库同步  oracle数据库    V1.1
 */
@Component
@Slf4j
public class GetErpDataJob {

	@Resource
	private AgvManageInfoService agvService;
	@Resource
	private MesToErpDataService mesToErpDataService;

	@Resource
	private GetMesDataJob getMesDataJob; // 新增：注入 GetMesDataJob


	private volatile boolean running = false; // 防止重复执行标志位



	//===========================自动定时全同步==================================

	/**
	 *   整体自动同步方法
	 */
	//===========================自动定时全同步==================================

	/**
	 * 整体ERP同步方法
	 */
	@Scheduled(cron = "0 0/3 * * * ?")
	public void syncErpToMesAll() {
		if (running) {
			log.warn("======【ERP同步任务正在执行，跳过本次调度】======");
			System.out.println("======【ERP同步任务正在执行，跳过本次调度】======");
			return;
		}

		running = true; // 标记任务开始
		long totalStart = System.currentTimeMillis();
		String totalStartStr = DateUtil.formatDateTime(LocalDateTime.now());

		log.info("======【ERP同步任务开始】{}======", totalStartStr);
		System.out.println(String.format("======【ERP同步任务开始】%s======", totalStartStr));

		try {
			List<Task> tasks = Arrays.asList(
					new Task("ERP 物料", mesToErpDataService::syncItemStock),
					new Task("ERP 工序", mesToErpDataService::syncProcedure),
					new Task("ERP BOM依赖", mesToErpDataService::syncBomTree)
//					new Task("MES BOM依赖", () -> {
//						getMesDataJob.bom();
//						return 0;
//					}),


					//这个工序  按时时间  需要调整
//					new Task("MES 工序", () -> {
//						getMesDataJob.process();
//						return 0;
//					})
			);

			for (Task t : tasks) {
				String taskStartStr = DateUtil.formatDateTime(LocalDateTime.now());
				long taskStart = System.currentTimeMillis();

				log.info("【ERP任务开始】{} | 时间：{}", t.name, taskStartStr);
				System.out.println(String.format("【ERP任务开始】%s | 时间：%s", t.name, taskStartStr));

				try {
					int count = t.action.get();
					long duration = System.currentTimeMillis() - taskStart;

					if (count == 0 && t.name.startsWith("MES")) {
						log.info("【ERP任务结束】{} | 完成内部逻辑，无统计数量 | 耗时：{} ms", t.name, duration);
						System.out.println(String.format("【ERP任务结束】%s | 完成内部逻辑，无统计数量 | 耗时：%d ms", t.name, duration));
					} else {
						log.info("【ERP任务结束】{} | 数量：{} | 耗时：{} ms", t.name, count, duration);
						System.out.println(String.format("【ERP任务结束】%s | 数量：%d | 耗时：%d ms", t.name, count, duration));
					}

				} catch (Exception e) {
					long duration = System.currentTimeMillis() - taskStart;
					log.error("【ERP任务失败】{} | 耗时：{} ms | 错误：{}", t.name, duration, e.getMessage(), e);
					System.err.println(String.format("【ERP任务失败】%s | 耗时：%d ms | 错误：%s", t.name, duration, e.getMessage()));
				}
			}

			long totalDuration = System.currentTimeMillis() - totalStart;
			String totalEndStr = DateUtil.formatDateTime(LocalDateTime.now());
			log.info("======【ERP同步任务结束】{} | 总耗时：{} ms======", totalEndStr, totalDuration);
			System.out.println(String.format("======【ERP同步任务结束】%s | 总耗时：%d ms======", totalEndStr, totalDuration));

		} finally {
			running = false; // 保证任务结束后重置标志位
		}
	}


	/**
	 * 整体MES构建方法   (2025-03 k开始构建bom ,   工序构建为最近一小时)
	 */
	@Scheduled(cron = "0 0/6 * * * ?")
	public void syncErpToMesAll007() {
		if (running) {
			log.warn("======【MES构建任务正在执行，跳过本次调度】======");
			System.out.println("======【MES构建任务正在执行，跳过本次调度】======");
			return;
		}

		running = true; // 标记任务开始
		long totalStart = System.currentTimeMillis();
		String totalStartStr = DateUtil.formatDateTime(LocalDateTime.now());

		log.info("======【MES构建任务开始】{}======", totalStartStr);
		System.out.println(String.format("======【MES构建任务开始】%s======", totalStartStr));


		//mes工序  （工序构建为最近一小时）
		DateTimeFormatter onTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String st = "2025-03-05 11:50:00";  // 从新构建

		try {
			List<Task> tasks = Arrays.asList(

					new Task("MES BOM依赖", () -> {
						getMesDataJob.bom(st);
						return 0;
					}),

					new Task("MES 工序", () -> {
						getMesDataJob.process(onTime.toString());
						return 0;
					})
			);

			for (Task t : tasks) {
				String taskStartStr = DateUtil.formatDateTime(LocalDateTime.now());
				long taskStart = System.currentTimeMillis();

				log.info("【MES任务开始】{} | 时间：{}", t.name, taskStartStr);
				System.out.println(String.format("【MES任务开始】%s | 时间：%s", t.name, taskStartStr));

				try {
					int count = t.action.get();
					long duration = System.currentTimeMillis() - taskStart;

					if (count == 0 && t.name.startsWith("MES")) {
						log.info("【MES任务结束】{} | 完成内部逻辑，无统计数量 | 耗时：{} ms", t.name, duration);
						System.out.println(String.format("【MES任务结束】%s | 完成内部逻辑，无统计数量 | 耗时：%d ms", t.name, duration));
					} else {
						log.info("【MES任务结束】{} | 数量：{} | 耗时：{} ms", t.name, count, duration);
						System.out.println(String.format("【MES任务结束】%s | 数量：%d | 耗时：%d ms", t.name, count, duration));
					}

				} catch (Exception e) {
					long duration = System.currentTimeMillis() - taskStart;
					log.error("【MES任务失败】{} | 耗时：{} ms | 错误：{}", t.name, duration, e.getMessage(), e);
					System.err.println(String.format("【MES任务失败】%s | 耗时：%d ms | 错误：%s", t.name, duration, e.getMessage()));
				}
			}

			long totalDuration = System.currentTimeMillis() - totalStart;
			String totalEndStr = DateUtil.formatDateTime(LocalDateTime.now());
			log.info("======【MES构建任务结束】{} | 总耗时：{} ms======", totalEndStr, totalDuration);
			System.out.println(String.format("======【MES构建任务结束】%s | 总耗时：%d ms======", totalEndStr, totalDuration));

		} finally {
			running = false; // 保证任务结束后重置标志位
		}
	}




	//===========================辅助工具===================================


	// 任务工具辅助方法
	private static class Task {
		final String name;
		final Supplier<Integer> action;

		Task(String name, Supplier<Integer> action) {
			this.name = name;
			this.action = action;
		}
	}

	//===========================旧===================================

	private volatile String startTime = "2025-06-01 00:00:00";

	/**
	 * ERP基础数据同步  oracle数据库  原始
	 */
//	@Scheduled(cron = "0 0/30 * * * ?")
	public void synchronousFromErp() {

		long start = System.currentTimeMillis();
		log.info("开始加载BOM用料...");
		agvService.bomInfo();
		log.info("加载BOM用料完成...cost:{}", System.currentTimeMillis() - start);
		startTime = DateUtil.formatDateTime(LocalDateTime.now());

	}


}

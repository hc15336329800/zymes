package cn.jb.boot.biz.item.task;

import cn.jb.boot.biz.agvcar.service.AgvManageInfoService;
import cn.jb.boot.biz.item.service.MesItemStockService;
import cn.jb.boot.biz.item.service.impl.MesToErpDataService;
import cn.jb.boot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;


/**
 *   ERP基础数据跨库同步  oracle数据库    V1.1
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


	private volatile String startTime = "2025-06-01 00:00:00";

	private volatile String  STime = "2025-06-28 14:50:00";





	private volatile boolean running = false; // 防止重复执行标志位


	/**
	 *   主同步方法
	 *   外部ERP三同步+内部BOM和工序同步       V1.3
	 *  同步、逐个执行
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void syncErpToMes() {
		if (running) {
			log.warn("======【同步任务正在执行，跳过本次调度】======");
			System.out.println("======【同步任务正在执行，跳过本次调度】======");
			return;
		}

		running = true; // 标记任务开始
		long totalStart = System.currentTimeMillis();
		String totalStartStr = DateUtil.formatDateTime(LocalDateTime.now());

		log.info("======【同步任务开始】{}======", totalStartStr);
		System.out.println(String.format("======【同步任务开始】%s======", totalStartStr));

		try {
			List<Task> tasks = Arrays.asList(
					new Task("ERP 物料", mesToErpDataService::syncItemStock),
					new Task("ERP 工序", mesToErpDataService::syncProcedure),
					new Task("ERP BOM依赖", mesToErpDataService::syncBomTree),
					new Task("MES BOM依赖", () -> {
						getMesDataJob.bom();
						return 0;
					}),
					new Task("MES 工序", () -> {
						getMesDataJob.process();
						return 0;
					})
			);

			for (Task t : tasks) {
				String taskStartStr = DateUtil.formatDateTime(LocalDateTime.now());
				long taskStart = System.currentTimeMillis();

				log.info("【任务开始】{} | 时间：{}", t.name, taskStartStr);
				System.out.println(String.format("【任务开始】%s | 时间：%s", t.name, taskStartStr));

				try {
					int count = t.action.get();
					long duration = System.currentTimeMillis() - taskStart;

					if (count == 0 && t.name.startsWith("MES")) {
						log.info("【任务结束】{} | 完成内部逻辑，无统计数量 | 耗时：{} ms", t.name, duration);
						System.out.println(String.format("【任务结束】%s | 完成内部逻辑，无统计数量 | 耗时：%d ms", t.name, duration));
					} else {
						log.info("【任务结束】{} | 数量：{} | 耗时：{} ms", t.name, count, duration);
						System.out.println(String.format("【任务结束】%s | 数量：%d | 耗时：%d ms", t.name, count, duration));
					}

				} catch (Exception e) {
					long duration = System.currentTimeMillis() - taskStart;
					log.error("【任务失败】{} | 耗时：{} ms | 错误：{}", t.name, duration, e.getMessage(), e);
					System.err.println(String.format("【任务失败】%s | 耗时：%d ms | 错误：%s", t.name, duration, e.getMessage()));
				}
			}

			long totalDuration = System.currentTimeMillis() - totalStart;
			String totalEndStr = DateUtil.formatDateTime(LocalDateTime.now());
			log.info("======【同步任务结束】{} | 总耗时：{} ms======", totalEndStr, totalDuration);
			System.out.println(String.format("======【同步任务结束】%s | 总耗时：%d ms======", totalEndStr, totalDuration));

		} finally {
			running = false; // 保证任务结束后重置标志位
		}
	}


	/**
	 *   主同步方法
	 *   外部ERP三同步+内部BOM和工序同步       V1.2
	 *  同步、逐个执行
	 */
	public void syncErpToMesV12() {
		long totalStart = System.currentTimeMillis();
		String totalStartStr = DateUtil.formatDateTime(LocalDateTime.now());

		log.info("======【同步任务开始】{}======", totalStartStr);
		System.out.println(String.format("======【同步任务开始】%s======", totalStartStr));

		List<Task> tasks = Arrays.asList(
				new Task("ERP 物料", mesToErpDataService::syncItemStock),
				new Task("ERP 工序", mesToErpDataService::syncProcedure),
				new Task("ERP BOM依赖", mesToErpDataService::syncBomTree),
				new Task("MES BOM依赖", () -> {
					getMesDataJob.bom();
					return 0; // 占位返回值
				}),
				new Task("MES 工序", () -> {
					getMesDataJob.process();
					return 0; // 占位返回值
				})
		);

		for (Task t : tasks) {
			String taskStartStr = DateUtil.formatDateTime(LocalDateTime.now());
			long taskStart = System.currentTimeMillis();

			log.info("【任务开始】{} | 时间：{}", t.name, taskStartStr);
			System.out.println(String.format("【任务开始】%s | 时间：%s", t.name, taskStartStr));

			try {
				int count = t.action.get();
				long duration = System.currentTimeMillis() - taskStart;

				// 判断是否是占位返回值
				if (count == 0 && (t.name.startsWith("MES"))) {
					log.info("【任务结束】{} | 完成内部逻辑，无统计数量 | 耗时：{} ms", t.name, duration);
					System.out.println(String.format("【任务结束】%s | 完成内部逻辑，无统计数量 | 耗时：%d ms", t.name, duration));
				} else {
					log.info("【任务结束】{} | 数量：{} | 耗时：{} ms", t.name, count, duration);
					System.out.println(String.format("【任务结束】%s | 数量：%d | 耗时：%d ms", t.name, count, duration));
				}

			} catch (Exception e) {
				long duration = System.currentTimeMillis() - taskStart;
				log.error("【任务失败】{} | 耗时：{} ms | 错误：{}", t.name, duration, e.getMessage(), e);
				System.err.println(String.format("【任务失败】%s | 耗时：%d ms | 错误：%s", t.name, duration, e.getMessage()));
			}
		}

		long totalDuration = System.currentTimeMillis() - totalStart;
		String totalEndStr = DateUtil.formatDateTime(LocalDateTime.now());
		log.info("======【同步任务结束】{} | 总耗时：{} ms======", totalEndStr, totalDuration);
		System.out.println(String.format("======【同步任务结束】%s | 总耗时：%d ms======", totalEndStr, totalDuration));
	}




	/**
	 *  ERP三基础数据同步  oracle数据库    V1.1
	 *  同步、逐个执行
	 */
//	@Scheduled(cron = "0 0/10 * * * ?")
	public void syncErpToMesV11() {

		// 【修正点1：将三段同步逻辑统一到一个任务列表中，避免重复代码】 	@Scheduled(cron = "0 0/1 * * * ?")
		List<Task> tasks = Arrays.asList(
				new Task("物料",       mesToErpDataService::syncItemStock),
				new Task("BOM工序",    mesToErpDataService::syncProcedure),
	        	new Task("BOM用料",    mesToErpDataService::syncBomTree),
				// 新增 MES 同步任务
				new Task("MES 内部 BOM", () -> {
					getMesDataJob.bom();
					return 0; // 纯属占位，没有实际统计含义
				}),
				new Task("MES 内部工序", () -> {
					getMesDataJob.process();
					return 0;// 纯属占位，没有实际统计含义
				})
		);

 		for (Task t : tasks) {

			STime = DateUtil.formatDateTime(LocalDateTime.now());   //注意重置时间
			long startTime = System.currentTimeMillis(); // 【修正点2：为每个任务单独记录开始时间】
//			System.out.println("ERP拉取结果：" +  t.name);
//			log.info("开始加载{}...", t.name);
			System.out.println("【多源同步开始】"+t.name+STime);
			try {
				int count = t.action.get();              // 执行
				log.info("【多源同步】{}同步结束，同步数量：{} 条，耗时：{} ms",  t.name, count, System.currentTimeMillis() - startTime);
				Long tem =System.currentTimeMillis() - startTime;
				System.out.println("【多源同步结束】"+t.name+"同步结束，同步数量："+count+"条，耗时："+tem+" ms"+STime);

			} catch (Exception e) {
				log.error("{}同步失败：", t.name, e);    // 【修正点4：每个任务独立捕获异常，互不影响】
				System.err.println("【多源同步失败】"+ t.name+"同步失败："+e);
			}
		}

	}


	/**
	 *  ERP基础数据同步  oracle数据库    V1.0
	 */
	public void syncErpToMesV1() {

		long start = System.currentTimeMillis(); //当前时间


		log.info("开始加载物料...");
		System.out.println("Info: 物料多源同步开始（频率一分钟）======================================================");
		int  cc = mesToErpDataService.syncItemStock();
		log.info("加载物料...cost:{}", System.currentTimeMillis() - start);
		long  usertime1= System.currentTimeMillis() - start;
		System.out.println("Info:  物料多源同步结束, 同步数量："+cc +"条，耗时："+ usertime1+"毫秒");



		log.info("开始加载BOM用料...");
		System.out.println("Info: BOM用料多源同步开始（频率一分钟）===================================================");
		int  cc2 = mesToErpDataService.syncBomTree();
		log.info("加载BOM用料完成...cost:{}", System.currentTimeMillis() - start);
		long  usertime2= System.currentTimeMillis() - start;
		System.out.println("Info:  BOM用料多源同步结束, 同步数量："+cc2 +"条，耗时："+ usertime2+"毫秒");


		log.info("开始加载BOM工序...");
		System.out.println("Info: BOM工序多源同步开始（频率一分钟）==================================================");
		int  cc3 = mesToErpDataService.syncProcedure();
		log.info("加载BOM工序完成...cost:{}", System.currentTimeMillis() - start);
		long  usertime3= System.currentTimeMillis() - start;
		System.out.println("Info:  BOM工序多源同步结束, 同步数量："+cc3 +"条，耗时："+ usertime3+"毫秒");



//		startTime = DateUtil.formatDateTime(LocalDateTime.now());

	}


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

	/**
	 *  ERP基础数据同步  oracle数据库  原始
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

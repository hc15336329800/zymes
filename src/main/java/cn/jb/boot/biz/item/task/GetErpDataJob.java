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

@Component
@Slf4j
public class GetErpDataJob {


	@Resource
	private AgvManageInfoService agvService;

	@Resource
	private MesItemStockService mesItemStockService;
	@Resource
	private MesToErpDataService mesToErpDataService;

	private volatile String startTime = "2025-06-01 00:00:00";


	// 【新增内部静态类，简化任务定义且不破坏原结构】
	private static class Task {
		final String name;
		final Supplier<Integer> action;
		Task(String name, Supplier<Integer> action) {
			this.name = name;
			this.action = action;
		}
	}


	/**
	 *  ERP基础数据同步  oracle数据库    V1.1
	 *  同步、逐个执行
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	public void syncErpToMes() {

		// 【修正点1：将三段同步逻辑统一到一个任务列表中，避免重复代码】
		List<Task> tasks = Arrays.asList(
				new Task("物料",       mesToErpDataService::syncItemStock),
				new Task("BOM工序",    mesToErpDataService::syncProcedure),
	        	new Task("BOM用料",    mesToErpDataService::syncBomTree)
		);

		for (Task t : tasks) {
			long startTime = System.currentTimeMillis(); // 【修正点2：为每个任务单独记录开始时间】
			log.info("开始加载{}...", t.name);
			try {
				int count = t.action.get();              // 【修正点3：用方法引用调用，返回同步条数】
				log.info("{}同步结束，同步数量：{} 条，耗时：{} ms",
						t.name, count, System.currentTimeMillis() - startTime);
			} catch (Exception e) {
				log.error("{}同步失败：", t.name, e);    // 【修正点4：每个任务独立捕获异常，互不影响】
			}
		}

	}


	/**
	 *  ERP基础数据同步  oracle数据库    V1.0
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
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



	// ----------------------------------------------------------------------------------------------------------------------

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

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



	/**
	 *  ERP基础数据同步  oracle数据库   -- 物料同步
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	public void syncItemStock() {

		long start = System.currentTimeMillis(); //当前时间


		log.info("开始加载物料...");
		System.out.println("info: 物料多源同步开始（频率一分钟）");
		int  cc = mesToErpDataService.syncItemStock();
		log.info("加载物料...cost:{}", System.currentTimeMillis() - start);
		long  usertime1= System.currentTimeMillis() - start;
		System.out.println("info:  物料多源同步结束, 同步数量："+cc +"条，耗时："+ usertime1+"毫秒");



		log.info("开始加载BOM用料...");
		System.out.println("info: BOM用料多源同步开始（频率一分钟）");
		int  cc2 = mesToErpDataService.syncBomTree();
		log.info("加载BOM用料完成...cost:{}", System.currentTimeMillis() - start);
		long  usertime2= System.currentTimeMillis() - start;
		System.out.println("info:  BOM用料多源同步结束, 同步数量："+cc2 +"条，耗时："+ usertime2+"毫秒");


		log.info("开始加载BOM工序...");
		System.out.println("info: BOM工序多源同步开始（频率一分钟）");
		int  cc3 = mesToErpDataService.syncProcedure();
		log.info("加载BOM工序完成...cost:{}", System.currentTimeMillis() - start);
		long  usertime3= System.currentTimeMillis() - start;
		System.out.println("info:  BOM工序多源同步结束, 同步数量："+cc3 +"条，耗时："+ usertime3+"毫秒");



//		startTime = DateUtil.formatDateTime(LocalDateTime.now());

	}



	// ----------------------------------------------------------------------------------------------------------------------

	/**
	 *  ERP基础数据同步  oracle数据库
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

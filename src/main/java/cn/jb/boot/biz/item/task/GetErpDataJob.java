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
	private volatile String startTime = "2023-07-01 00:00:00";



	/**
	 *  ERP基础数据同步  oracle数据库   -- 物料同步
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	public void syncItemStock() {

		long start = System.currentTimeMillis();
		log.info("开始加载BOM用料...");

		System.out.println("info:  物料同步开始 ，频率一分钟");
		int  cc = mesToErpDataService.syncItemStock();

		System.out.println("info:  物料同步结束, 同步数量："+cc +"条");

		log.info("加载BOM用料完成...cost:{}", System.currentTimeMillis() - start);
		startTime = DateUtil.formatDateTime(LocalDateTime.now());

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

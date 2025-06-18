package cn.jb.boot.biz.item.service;

import cn.jb.boot.biz.item.entity.MesProcedure;
import cn.jb.boot.biz.item.entity.MidItemStock;
import cn.jb.boot.biz.item.mapper.MesProcedureAndMidItemStockMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MesProcedureAndMidItemStockService {

	@Autowired
	private MesProcedureAndMidItemStockMapper mesMapper;

	public void syncMissingProcedureToMid() {
		List<MesProcedure> missing = mesMapper.getMissingProcedures();
		if (!CollectionUtils.isEmpty(missing)) {
			List<MidItemStock> batch = missing.stream().map(proc -> {
				MidItemStock mis = new MidItemStock();
				mis.setId(UUID.randomUUID().toString().replaceAll("-", ""));
				mis.setItemNo(proc.getItemNo());
				mis.setProcedureCode(proc.getProcedureCode());
				mis.setProcedureName(proc.getProcedureName());
				mis.setSeqNo(proc.getSeqNo());
				mis.setLastFlag("00");
				mis.setCreatedTime(LocalDateTime.now());
				return mis;
			}).collect(Collectors.toList());
			mesMapper.insertBatchToMid(batch);
		}
	}
}

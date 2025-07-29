package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.LaserReportSummary;
import cn.jb.boot.biz.item.mapper.LaserReportSummaryMapper;
import cn.jb.boot.biz.item.service.LaserReportSummaryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LaserReportSummaryServiceImpl implements LaserReportSummaryService {

	@Resource
	private LaserReportSummaryMapper mapper;

	@Override
	public List<LaserReportSummary> listTop100() {
		LambdaQueryWrapper<LaserReportSummary> wrapper = new LambdaQueryWrapper<>();
		wrapper.last("limit 100");
		return mapper.selectList(wrapper);
	}
}

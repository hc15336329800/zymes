package cn.jb.boot.biz.item.service.impl;

import cn.jb.boot.biz.item.entity.OrderProgressSummary;
import cn.jb.boot.biz.item.mapper.OrderProgressSummaryMapper;
import cn.jb.boot.biz.item.service.OrderProgressSummaryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/** Service 实现 */
@Service
public class OrderProgressSummaryServiceImpl implements OrderProgressSummaryService {

	@Resource
	private OrderProgressSummaryMapper mapper;

	@Override
	public List<OrderProgressSummary> listTop100() {
		// 直接使用 MyBatis-Plus 查询，并限制返回 100 条
		return mapper.selectList(
				new LambdaQueryWrapper<OrderProgressSummary>().last("limit 100"));
	}
}

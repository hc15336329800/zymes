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
		// 构造查询条件，限制返回 100 条记录
		LambdaQueryWrapper<OrderProgressSummary> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.last("limit 100");

		// 执行查询
		List<OrderProgressSummary> resultList = mapper.selectList(queryWrapper);

		// 打印结果大小（或调试时查看是否为 null）
		System.out.println("OrderProgressSummary 查询结果数量：" + (resultList == null ? "null" : resultList.size()));

		// 返回结果
		return resultList;
	}
}

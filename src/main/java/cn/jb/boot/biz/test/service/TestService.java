package cn.jb.boot.biz.test.service;

import cn.jb.boot.biz.test.mapper.TestMapper;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.framework.com.response.PagingResponse;
import cn.jb.boot.system.entity.UserInfo;
import cn.jb.boot.system.vo.request.UserInfoPageRequest;
import cn.jb.boot.system.vo.request.UserInfoPageResponse;
import cn.jb.boot.util.MsgUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;                        // 导入 Collectors
import cn.jb.boot.util.PojoUtil;                            // Bean 拷贝工具
import org.apache.commons.lang3.StringUtils;               // 正确的 StringUtils


@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TestService {

	private final TestMapper testMapper;  //mp


	public BaseResponse<List<UserInfoPageResponse>> testPageList(	BaseRequest<UserInfoPageRequest> request)
	{
		// 1. 解包请求参数
		//		UserInfoPageRequest params = MsgUtil.params(request);
		// 新写法（即使 data 为 null 也会自动 new 一个空对象）
		UserInfoPageRequest params = MsgUtil.params(request, UserInfoPageRequest::new);


		// 2. 构造分页对象
		Page<UserInfo> page = new Page<>(
				request.getPage().getPageNum(),
				request.getPage().getPageSize()
		);

		// 3. 构造动态查询条件
		LambdaQueryWrapper<UserInfo> qw = new LambdaQueryWrapper<>();
		if (StringUtils.isNotBlank(params.getMobile())) {
			qw.like(UserInfo::getMobile, params.getMobile());
		}
		if (StringUtils.isNotBlank(params.getBeginTime())) {
			qw.ge(UserInfo::getCreatedTime, params.getBeginTime() + " 00:00:00");
		}
		if (StringUtils.isNotBlank(params.getEndTime())) {
			qw.le(UserInfo::getCreatedTime, params.getEndTime() + " 23:59:59");
		}
		qw.orderByDesc(UserInfo::getCreatedTime);

		// 4. 执行分页查询
		IPage<UserInfo> result = testMapper.selectPage(page, qw);


		// 5. 转换为 VO
		List<UserInfoPageResponse> list = result.getRecords().stream()
				.map(entity -> PojoUtil.copyBean(entity, UserInfoPageResponse.class))
				.collect(Collectors.toList());

		// 5. 转换为  多表 VO
		//		List<UserInfoPageResponse> list = result.getRecords().stream()
		//				.map(entity -> {
		//					UserInfoPageResponse vo = MsgUtil.copyBean(entity, UserInfoPageResponse.class);
		//					// 补充关联字段, 多表，备用禁止删除注释！
		//					//					vo.setDeptName( deptInfoService.getDeptName(entity.getDeptId()) );
		//					//					vo.setRoleNameStr(
		//					//							String.join(",", userRoleService.userRoles(entity.getId()))
		//					//					);
		//					return vo;
		//				})
		//				.collect(Collectors.toList());

		// 6. 构造并设置分页信息
		PagingResponse pg = new PagingResponse();
		pg.setPages(    result.getPages()   );
		pg.setPageNum(  result.getCurrent() );
		pg.setPageSize( result.getSize()    );
		pg.setTotalNum( result.getTotal()   );

		// 7. 返回
		BaseResponse<List<UserInfoPageResponse>> resp = MsgUtil.ok(list);
		resp.setPage(pg);
		return resp;
	}
}

package cn.jb.boot.biz.test.controller;

import cn.jb.boot.biz.test.service.TestService;
import cn.jb.boot.framework.com.request.BaseRequest;
import cn.jb.boot.framework.com.response.BaseResponse;
import cn.jb.boot.system.vo.request.UserInfoPageRequest;
import cn.jb.boot.system.vo.request.UserInfoPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/test")
@Tag(name = "测试", description = "测试分页查询用户")
@RequiredArgsConstructor
public class TestController {

	private final TestService testService;


	//查询列表
	@PostMapping("/testPageList")
	public BaseResponse<List<UserInfoPageResponse>> testPageList(@RequestBody @Valid BaseRequest<UserInfoPageRequest> request)
	{
		log.info("testPageList params: {}", request);
		return testService.testPageList(request);
	}





}

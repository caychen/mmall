package org.com.cay.mmall.controller.back;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.User;
import org.com.cay.mmall.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by caychen on 2018/7/4.
 */
@RestController
@RequestMapping("/manage/user")
@Api("后台用户（管理员）模块控制类")
public class UserManagerController {

	private final Logger logger = LoggerFactory.getLogger(UserManagerController.class);

	@Autowired
	private IUserService userService;

	/**
	 * 后台管理员登录
	 *
	 * @param username
	 * @param password
	 * @param session
	 * @return
	 */
	@PostMapping("/login.do")
	@ApiOperation(value = "后台用户（管理员）登录", notes = "根据用户名和密码登录，并把用户信息（密码置为空）放入session中")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "登录账户", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "password", value = "登录密码", required = true, dataType = "String", paramType = "query")
	})
	public ServerResponse<User> login(String username, String password, HttpSession session) {
		logger.info("后台管理员登录, username: {}, password: {}", username, password);
		ServerResponse<User> response = userService.login(username, password);
		if (response.isSuccess()) {
			User user = response.getData();
			if (user.getRole().intValue() == Constant.Role.ROLE_ADMIN) {
				//登录的是管理员
				session.setAttribute(Constant.CURRENT_USER, user);
				return response;
			}

			return ServerResponse.createByErrorMessage("非管理员无法登录！");
		}
		return response;
	}

}

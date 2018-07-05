package org.com.cay.mmall.controller.front;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ResponseCode;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.User;
import org.com.cay.mmall.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@Api("前台用户模块控制类")
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private IUserService userService;

	/**
	 * 用户登录
	 *
	 * @param username
	 * @param password
	 * @param session
	 * @return
	 */
	@PostMapping("/login.do")
	@ApiOperation(value = "用户登录", notes = "根据用户名和密码登录，并把用户信息（密码置为空）放入session中")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "登录账户", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "password", value = "登录密码", required = true, dataType = "String", paramType = "query")
	})
	public ServerResponse<User> login(String username, String password, HttpSession session) {
		logger.info("前台用户登录, username: {}, password: {}", username, password);
		ServerResponse<User> response = userService.login(username, password);
		if (response.isSuccess()) {
			session.setAttribute(Constant.CURRENT_USER, response.getData());
		}
		return response;
	}

	/**
	 * 用户登出
	 *
	 * @param session
	 * @return
	 */
	@GetMapping("/logout.do")
	@ApiOperation(value = "用户登出", notes = "将登录的用户进行注销")
	public ServerResponse<String> logout(HttpSession session) {
		logger.info("用户登出注销");
		session.removeAttribute(Constant.CURRENT_USER);
		return ServerResponse.createBySuccess();
	}

	/**
	 * 用户注册
	 *
	 * @param user
	 * @return
	 */
	@PostMapping("/register.do")
	@ApiOperation(value = "用户注册", notes = "用户填写表单，进行注册")
	public ServerResponse<String> register(User user) {
		logger.info("用户注册， user: {}", user);
		return userService.register(user);
	}

	/**
	 * 注册时检查是否合法
	 *
	 * @param data
	 * @param type
	 * @return
	 */
	@GetMapping("/check_valid.do")
	@ApiOperation(value = "注册时检查用户是否合法", notes = "其中type为username或者email，data为具体的值")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "data", value = "注册时username或者email的具体的值", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "type", value = "username或email", required = true, dataType = "String", paramType = "query")
	})
	public ServerResponse<String> checkValid(String data, String type) {
		logger.info("注册时检查是否合法, data: {}, type: {}", data, type);
		return userService.checkValid(data, type);
	}

	/**
	 * 获取当前登录的用户信息(只限于存在session中的信息)
	 *
	 * @param session
	 * @return
	 */
	@GetMapping("/get_user_info.do")
	@ApiOperation(value = "获取当前登录的用户信息")
	public ServerResponse<User> getUserInfo(HttpSession session) {
		logger.info("获取当前登录的用户信息(只限于存在session中的信息)");
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user != null) {
			return ServerResponse.createBySuccess(user);
		}
		return ServerResponse.createByErrorMessage("用户未登录!");
	}

	/**
	 * 获取用户对应的忘记密码的问题
	 *
	 * @param username
	 * @return
	 */
	@GetMapping("/forget_get_question.do")
	@ApiOperation(value = "获取用户对应的忘记密码的问题")
	@ApiImplicitParam(name = "username", value = "根据username获取忘记密码的问题", required = true, dataType = "String", paramType = "query")
	public ServerResponse<String> forgetGetQuestion(String username) {
		logger.info("获取用户对应的忘记密码的问题, username: {}", username);
		return userService.selectQuestion(username);
	}

	/**
	 * 校验忘记密码的问题答案
	 *
	 * @param username
	 * @param question
	 * @param answer
	 * @return
	 */
	@PostMapping("/forget_check_answer.do")
	@ApiOperation(value = "校验忘记密码的问题答案", notes = "正确回答忘记密码的问题后设置token有效期30分钟，如果回答正确后，长时间不设置新密码，token会失效，如果需要设置新密码，则需要重新回答问题")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "用户账号", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "question", value = "忘记密码的问题", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "answer", value = "忘记密码的答案", required = true, dataType = "String", paramType = "query")
	})
	public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
		logger.info("校验忘记密码的问题答案, username: {}, question: {}, answer: {}", username, question, answer);
		return userService.checkAnswer(username, question, answer);
	}

	/**
	 * 忘记密码的重置密码
	 *
	 * @param username
	 * @param passwordNew
	 * @param forgetToken
	 * @return
	 */
	@PutMapping("/forget_reset_password.do")
	@ApiOperation(value = "忘记密码的重置密码")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "用户账号", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "forgetToken", value = "正确回答校验问题答案时保存的token", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "passwordNew", value = "新密码", required = true, dataType = "String", paramType = "query")
	})
	public ServerResponse<String> forgetResetPassword(String username, String forgetToken, String passwordNew) {
		logger.info("忘记密码的重置密码, username: {}, forgetToken: {}, passwordNew: {}", username, forgetToken, passwordNew);
		return userService.forgetResetPassword(username, forgetToken, passwordNew);
	}

	/**
	 * 登录状态下的重置密码
	 *
	 * @param session
	 * @param passwordOld
	 * @param passwordNew
	 * @return
	 */
	@PutMapping("/reset_password.do")
	@ApiOperation(value = "登录状态下的重置密码")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "passwordOld", value = "旧密码", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "passwordNew", value = "新密码", required = true, dataType = "String", paramType = "query")
	})
	public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
		logger.info("登录状态下的重置密码, passwordOld: {}, passwordNew: {}", passwordOld, passwordNew);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorMessage("用户未登录!");
		}

		return userService.resetPassword(passwordOld, passwordNew, user);
	}

	/**
	 * 更新当前登录的用户信息
	 *
	 * @param session
	 * @param user
	 * @return
	 */
	@PutMapping("/update_user_info.do")
	@ApiOperation(value = "更新当前登录的用户信息")
	public ServerResponse<User> updateUserInfo(HttpSession session, User user) {
		logger.info("更新当前登录的用户信息, user: {}", user);
		User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);
		if (currentUser == null) {
			return ServerResponse.createByErrorMessage("用户未登录!");
		}

		user.setId(currentUser.getId());
		user.setUsername(currentUser.getUsername());

		ServerResponse<User> response = userService.updateUserInfo(user);
		if (response.isSuccess()) {
			response.getData().setUsername(currentUser.getUsername());
			session.setAttribute(Constant.CURRENT_USER, response.getData());
		}

		return response;
	}

	/**
	 * 获取当前登录用户的详细信息
	 *
	 * @param session
	 * @return
	 */
	@GetMapping("/get_user_detail_info.do")
	@ApiOperation(value = "获取当前登录用户的详细信息")
	public ServerResponse<User> getUserDetail(HttpSession session) {
		logger.info("获取当前登录用户的详细信息");
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录！");
		}

		return userService.getUserDetail(user.getId());
	}
}

package org.com.cay.mmall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.common.TokenCache;
import org.com.cay.mmall.dao.UserMapper;
import org.com.cay.mmall.entity.User;
import org.com.cay.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public ServerResponse<User> login(String username, String password) {
		int count = userMapper.checkUsername(username);
		if (count == 0) {
			return ServerResponse.createByErrorMessage("用户名不存在！");
		}

		password = new String(DigestUtils.md5DigestAsHex(password.getBytes()));

		User user = userMapper.login(username, password);
		if (user == null) {
			return ServerResponse.createByErrorMessage("密码错误！");
		}

		//返回前把密码置为空
		user.setPassword(StringUtils.EMPTY);

		return ServerResponse.createBySuccess("登录成功", user);
	}

	@Override
	public ServerResponse<String> register(User user) {
		ServerResponse<String> response = this.checkValid(user.getUsername(), Constant.USERNAME);
		if (!response.isSuccess()) {
			return response;
		}

		response = this.checkValid(user.getEmail(), Constant.EMAIL);
		if (!response.isSuccess()) {
			return response;
		}
		user.setRole(Constant.Role.ROLE_CUSTOMER);

		//密码md5
		user.setPassword(new String(DigestUtils.md5DigestAsHex(user.getPassword().getBytes())));

		int count = userMapper.insert(user);
		if (count == 0) {
			return ServerResponse.createByErrorMessage("注册失败！");
		}

		return ServerResponse.createBySuccessMessage("注册成功！");
	}

	@Override
	public ServerResponse<String> checkValid(String data, String type) {
		if (StringUtils.isNotBlank(type)) {
			//开始校验
			if (Constant.USERNAME.equals(type)) {
				int count = userMapper.checkUsername(data);
				if (count > 0) {
					return ServerResponse.createByErrorMessage("用户名已存在！");
				}
			}
			if (Constant.EMAIL.equals(type)) {
				int count = userMapper.checkEmail(data);
				if (count > 0) {
					return ServerResponse.createByErrorMessage("邮箱已被注册！");
				}
			}
		} else {
			return ServerResponse.createByErrorMessage("参数错误！");
		}
		return ServerResponse.createBySuccessMessage("校验成功！");
	}

	@Override
	public ServerResponse<String> selectQuestion(String username) {
		ServerResponse<String> response = this.checkValid(username, Constant.USERNAME);
		if (response.isSuccess()) {
			//用户不存在
			return ServerResponse.createByErrorMessage("用户不存在！");
		}

		String question = userMapper.selectQuestionByUsername(username);
		if (StringUtils.isNotBlank(question)) {
			return ServerResponse.createBySuccess(question);
		}

		return ServerResponse.createByErrorMessage("找回密码的问题是空的！");
	}

	@Override
	public ServerResponse<String> checkAnswer(String username, String question, String answer) {
		int count = userMapper.checkAnswer(username, question, answer);
		if (count > 0) {
			//正确
			//为了防止恶意用户来直接访问修改密码接口，在调用验证答案接口后采用token机制来验证身份
			String forgetToken = UUID.randomUUID().toString();
			TokenCache.setKey(Constant.TOKEN_PREFIX + username, forgetToken);
			return ServerResponse.createBySuccess(forgetToken);
		}

		return ServerResponse.createByErrorMessage("问题的答案回答错误！");
	}

	@Override
	public ServerResponse<String> forgetResetPassword(String username, String forgetToken, String passwordNew) {
		if (StringUtils.isBlank(forgetToken)) {
			return ServerResponse.createByErrorMessage("参数错误，token需要传递！");
		}

		ServerResponse<String> response = this.checkValid(username, Constant.USERNAME);
		if (response.isSuccess()) {
			//用户不存在
			return ServerResponse.createByErrorMessage("用户不存在！");
		}

		String tokenCache = TokenCache.getKey(Constant.TOKEN_PREFIX + username);
		if (StringUtils.isBlank(tokenCache)) {
			return ServerResponse.createByErrorMessage("token无效或过期！");
		}

		if (StringUtils.equals(tokenCache, forgetToken)) {
			//更新密码
			String newMd5Password = new String(DigestUtils.md5DigestAsHex(passwordNew.getBytes()));
			int count = userMapper.updatePasswordByUsername(username, newMd5Password);
			if (count > 0) {
				return ServerResponse.createBySuccessMessage("修改密码成功！");
			}
		} else {
			return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token！");
		}

		return ServerResponse.createByErrorMessage("修改密码失败！");
	}

	@Override
	public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
		int count = userMapper.checkPassword(DigestUtils.md5DigestAsHex(passwordOld.getBytes()), user.getId());
		if (count == 0) {
			return ServerResponse.createByErrorMessage("旧密码输入错误！");
		}
		user.setPassword(DigestUtils.md5DigestAsHex(passwordNew.getBytes()));
		count = userMapper.updateByPrimaryKeySelective(user);
		if (count > 0) {
			return ServerResponse.createBySuccessMessage("密码更新成功！");
		}
		return ServerResponse.createByErrorMessage("密码更新失败！");
	}

	@Override
	public ServerResponse<User> updateUserInfo(User user) {
		//username不能修改
		int count = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
		if (count > 0) {
			return ServerResponse.createByErrorMessage("邮箱已被注册！");
		}
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setEmail(user.getEmail());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());

		count = userMapper.updateByPrimaryKeySelective(updateUser);
		if (count > 0) {
			return ServerResponse.createBySuccess("更新个人信息成功！", updateUser);
		}
		return ServerResponse.createByErrorMessage("更新个人信息失败！");
	}

	@Override
	public ServerResponse<User> getUserDetail(Integer userId) {
		User user = userMapper.selectByPrimaryKey(userId);
		if (user == null) {
			return ServerResponse.createByErrorMessage("找不到当前用户！");
		}
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess(user);
	}

	@Override
	public ServerResponse checkAdminRole(User user) {
		if (user != null && user.getRole().intValue() == Constant.Role.ROLE_ADMIN) {
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}
}

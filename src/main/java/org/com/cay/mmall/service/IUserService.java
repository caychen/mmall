package org.com.cay.mmall.service;

import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.User;

public interface IUserService {

	ServerResponse<User> login(String username, String password);

	ServerResponse<String> register(User user);

	ServerResponse<String> checkValid(String data, String type);

	ServerResponse<String> selectQuestion(String username);

	ServerResponse<String> checkAnswer(String username, String question, String answer);

	ServerResponse<String> forgetResetPassword(String username, String forgetToken, String passwordNew);

	ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

	ServerResponse<User> updateUserInfo(User user);

	ServerResponse<User> getUserDetail(Integer id);

	ServerResponse checkAdminRole(User user);
}

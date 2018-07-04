package org.com.cay.mmall.service;

import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.User;

public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);
}

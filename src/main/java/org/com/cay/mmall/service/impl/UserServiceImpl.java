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
            return ServerResponse.createByErrorMessage("用户名不存在!");
        }

        password = new String(DigestUtils.md5DigestAsHex(password.getBytes()));

        User user = userMapper.login(username, password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误!");
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
            return ServerResponse.createByErrorMessage("注册失败!");
        }

        return ServerResponse.createBySuccessMessage("注册成功!");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //开始校验
            if (Constant.USERNAME.equals(type)) {
                int count = userMapper.checkUsername(str);
                if (count > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在!");
                }
            }
            if (Constant.EMAIL.equals(type)) {
                int count = userMapper.checkEmail(str);
                if (count > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已被注册!");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误!");
        }
        return ServerResponse.createBySuccessMessage("校验成功!");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> response = this.checkValid(username, Constant.USERNAME);
        if (response.isSuccess()) {
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在!");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorMessage("找回密码的问题是空的!");
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

        return ServerResponse.createByErrorMessage("问题的答案回答错误!");
    }
}

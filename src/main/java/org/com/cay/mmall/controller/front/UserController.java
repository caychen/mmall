package org.com.cay.mmall.controller.front;

import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.User;
import org.com.cay.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

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
    @ResponseBody
    @PostMapping("/login.do")
    public ServerResponse<User> login(String username, String password, HttpSession session) {

        ServerResponse<User> response = userService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Constant.CURRENT_USER, response.getData());
        }
        return response;
    }

    @GetMapping("/logout.do")
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Constant.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @PostMapping("/register.do")
    @ResponseBody
    public ServerResponse<String> register(User user){
        return userService.register(user);
    }

    @GetMapping("/check_valid.do")
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return userService.checkValid(str, type);
    }

    @GetMapping("/get_user_info.do")
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Constant.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录!")
    }

    @GetMapping("/forget_get_question.do")
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return userService.selectQuestion(username);
    }

    @GetMapping("/forget_check_answer.do")
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){
        return userService.checkAnswer(username, question, answer);
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){

    }

}

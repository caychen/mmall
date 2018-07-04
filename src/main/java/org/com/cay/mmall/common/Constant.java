package org.com.cay.mmall.common;

public class Constant {

    public static final String CURRENT_USER = "current_user";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public static final String TOKEN_PREFIX = "token_";

    public interface Role {
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }
}

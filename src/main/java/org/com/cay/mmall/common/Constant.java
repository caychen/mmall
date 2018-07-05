package org.com.cay.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Constant {

	public static final String CURRENT_USER = "current_user";

	public static final String EMAIL = "email";

	public static final String USERNAME = "username";

	public static final String TOKEN_PREFIX = "token_";

	public interface OrderBy{
		Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
	}

	public interface Role {
		int ROLE_CUSTOMER = 0;//普通用户
		int ROLE_ADMIN = 1;//管理员
	}

	public enum ProductStatusEnum{
		ON_SALE(1, "在线");

		private String value;
		private int code;

		public String getValue() {
			return value;
		}

		public int getCode() {
			return code;
		}

		ProductStatusEnum(int code, String value) {
			this.value = value;
			this.code = code;
		}
	}

	public interface Cart{
		int CHECKED = 1;
		int UN_CHECKED = 2;

		String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
		String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
	}
}

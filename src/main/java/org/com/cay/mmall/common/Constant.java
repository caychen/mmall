package org.com.cay.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Constant {

	public static final String CURRENT_USER = "current_user";

	public static final String EMAIL = "email";

	public static final String USERNAME = "username";

	public static final String TOKEN_PREFIX = "token_";

	public interface OrderBy {
		Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
	}

	public interface Role {
		int ROLE_CUSTOMER = 0;//普通用户
		int ROLE_ADMIN = 1;//管理员
	}

	public enum ProductStatusEnum {
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

	public interface Cart {
		int CHECKED = 1;
		int UN_CHECKED = 2;

		String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
		String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
	}

	public enum StatusEnum {
		CANCEL(0, "订单取消"),
		NO_PAY(10, "未支付"),
		PAID(20, "已支付"),
		SHIPPED(40, "已发货"),
		SUCCESS(50, "订单完成"),
		CLOSED(60, "订单关闭");

		private String value;
		private int code;

		StatusEnum(int code, String value) {
			this.value = value;
			this.code = code;
		}

		public String getValue() {
			return value;
		}

		public int getCode() {
			return code;
		}

		public static StatusEnum codeOf(int code) {
			for (StatusEnum e : StatusEnum.values()) {
				if (e.getCode() == code) {
					return e;
				}
			}
			throw new RuntimeException("没有找到对应的枚举");
		}
	}

	public interface AlipayCallback {
		String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
		String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
		String TRADE_STATUS_TRADE_CLOSED = "TRADE_CLOSED";
		String TRADE_STATUS_TRADE_FINISHED = "TRADE_FINISHED";

		String RESPONSE_SUCCESS = "success";
		String RESPONSE_FAILED = "failed";
	}

	public enum PayPlatformEnum {
		ALIPAY(1, "支付宝"),
		WECHATPAY(2, "微信");

		private String value;
		private int code;

		PayPlatformEnum(int code, String value) {
			this.value = value;
			this.code = code;
		}

		public String getValue() {
			return value;
		}

		public int getCode() {
			return code;
		}
	}

	public interface PayCode {
		int PAY_SUCCESS = 10000;
	}

	public enum PaymentTypeEnum {
		ONLINE_PAY(1, "在线支付");

		private String value;
		private int code;

		PaymentTypeEnum(int code, String value) {
			this.value = value;
			this.code = code;
		}

		public String getValue() {
			return value;
		}

		public int getCode() {
			return code;
		}

		public static PaymentTypeEnum codeOf(int code) {
			for (PaymentTypeEnum e : PaymentTypeEnum.values()) {
				if (e.getCode() == code) {
					return e;
				}
			}
			throw new RuntimeException("没有找到对应的枚举");
		}
	}
}

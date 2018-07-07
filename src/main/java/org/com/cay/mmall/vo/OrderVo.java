package org.com.cay.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Caychen on 2018/7/7.
 */
@Data
public class OrderVo {

	private Long orderNo;

	private BigDecimal payment;

	private Integer paymentType;

	private String paymentTypeDesc;

	private Integer postage;

	private Integer status;

	private String statusDesc;

	private String paymentTime;

	private String sendTime;

	private String endTime;

	private String closeTime;

	private String createTime;

}

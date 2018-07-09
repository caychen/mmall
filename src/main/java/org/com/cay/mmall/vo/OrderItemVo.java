package org.com.cay.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Caychen on 2018/7/8.
 */
@Data
public class OrderItemVo {

	private Long orderNo;

	private Integer productId;

	private String productName;

	private String productImage;

	private BigDecimal currentUnitPrice;

	private Integer quantity;

	private BigDecimal totalPrice;

	private String createTime;
}

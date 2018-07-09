package org.com.cay.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Caychen on 2018/7/8.
 */
@Data
public class OrderProductVo {

	private List<OrderItemVo> orderItemVoList;

	private BigDecimal productTotalPrice;

	private String imageHost;

}

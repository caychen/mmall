package org.com.cay.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Caychen on 2018/7/5.
 */
@Data
public class CartVo {

	private List<CartProductVo> cartProductVoList;

	private BigDecimal cartTotalPrice;

	private Boolean allChecked;

	private String imageHost;
}

package org.com.cay.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Caychen on 2018/7/5.
 */
//结合商品和购物车的对象
@Data
public class CartProductVo {

	private Integer id;

	private Integer userId;

	private Integer productId;

	private Integer quantity;//购物车中此商品的数量

	private String productName;

	private String productSubtitle;

	private String productMainImage;

	private BigDecimal productPrice;

	private Integer productStatus;

	private BigDecimal productTotalPrice;

	private Integer productStock;

	private Integer productChecked;//此商品是否勾选

	private String limitQuantity;//限制数量的一个返回结果
}

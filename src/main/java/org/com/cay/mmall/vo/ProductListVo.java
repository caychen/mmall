package org.com.cay.mmall.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Caychen on 2018/7/5.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductListVo {
	private Integer id;

	private Integer categoryId;

	private String name;

	private String subtitle;

	private String mainImage;

	private BigDecimal price;

	private Integer status;

	private String imageHost;

}

package org.com.cay.mmall.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Caychen on 2018/7/5.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailVo {
	private Integer id;

	private Integer categoryId;

	private String name;

	private String subtitle;

	private String mainImage;

	private String subImages;

	private String detail;

	private BigDecimal price;

	private Integer stock;

	private Integer status;

	private String createTime;

	private String updateTime;

	//图片服务器主机（前缀）
	private String imageHost;
	private Integer parentCategoryId;
}

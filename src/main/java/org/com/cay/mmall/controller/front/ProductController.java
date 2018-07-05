package org.com.cay.mmall.controller.front;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.service.IProductService;
import org.com.cay.mmall.vo.ProductDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Caychen on 2018/7/5.
 */
@RestController
@RequestMapping("/product")
@Api("前台商品控制类")
public class ProductController {

	private final Logger logger = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private IProductService productService;

	/**
	 * 前台商品详情
	 *
	 * @param productId
	 * @return
	 */
	@GetMapping("/detail.do")
	@ApiOperation(value = "前台商品详情")
	@ApiImplicitParam(name = "productId", value = "商品id", required = true, dataType = "int", paramType = "query")
	public ServerResponse<ProductDetailVo> detail(Integer productId) {
		logger.info("前台商品详情, productId: {}", productId);
		return productService.getProductDetail(productId);
	}

	/**
	 * 前台商品查询
	 *
	 * @param keyword
	 * @param categoryId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/list.do")
	@ApiOperation(value = "前台商品查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "keyword", value = "关键字", dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "categoryId", value = "分类id", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "页码", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "orderBy", value = "排序", dataType = "String", paramType = "query")
	})
	public ServerResponse<PageInfo> list(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "categoryId", required = false) Integer categoryId,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(value = "orderBy", required = false, defaultValue = "") String orderBy) {

		logger.info("前台商品查询, keyword: {}, categoryId: {}, pageNum: {}, pageSize: {}", keyword, categoryId, pageNum, pageSize);
		return productService.getProductByCondition(keyword, categoryId, pageNum, pageSize, orderBy);
	}

}

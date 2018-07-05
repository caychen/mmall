package org.com.cay.mmall.controller.front;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ResponseCode;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.User;
import org.com.cay.mmall.service.ICartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by Caychen on 2018/7/5.
 */
@RestController
@RequestMapping("/cart")
@Api("前台购物车控制类")
public class CartController {

	private final Logger logger = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private ICartService cartService;

	/**
	 * 查询购物车中商品
	 *
	 * @param session
	 * @return
	 */
	@GetMapping("/list.do")
	@ApiOperation(value = "查询购物车中商品")
	public ServerResponse list(HttpSession session) {
		logger.info("查询购物车中商品");
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return cartService.list(user.getId());
	}

	/**
	 * 向购物车中添加商品
	 *
	 * @param session
	 * @param count
	 * @param productId
	 * @return
	 */
	@PostMapping("/add.do")
	@ApiOperation(value = "向购物车中添加商品")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "count", value = "商品个数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "productId", value = "商品id", required = true, dataType = "int", paramType = "query")
	})
	public ServerResponse add(HttpSession session, Integer count, Integer productId) {
		logger.info("向购物车中添加商品, count: {}, productId: {}", count, productId);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return cartService.add(user.getId(), count, productId);
	}

	/**
	 * 更新购物车中商品
	 *
	 * @param session
	 * @param count
	 * @param productId
	 * @return
	 */
	@PutMapping("/update.do")
	@ApiOperation(value = "更新购物车中商品")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "count", value = "商品个数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "productId", value = "商品id", required = true, dataType = "int", paramType = "query")
	})
	public ServerResponse update(HttpSession session, Integer count, Integer productId) {
		logger.info("更新购物车中商品, count: {}, productId: {}", count, productId);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return cartService.update(user.getId(), count, productId);
	}

	/**
	 * 删除购物车中商品
	 *
	 * @param session
	 * @param productIds
	 * @return
	 */
	@DeleteMapping("/delete_product.do")
	@ApiOperation(value = "删除购物车中商品")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "productIds", value = "多个商品id，逗号分隔", required = true, dataType = "String", paramType = "query")
	})
	public ServerResponse deleteProduct(HttpSession session, String productIds) {
		logger.info("删除购物车中商品, productIds: {}", productIds);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return cartService.deleteProduct(user.getId(), productIds);
	}

	/**
	 * 全选购物车中商品
	 *
	 * @param session
	 * @return
	 */
	@PutMapping("/select_all.do")
	@ApiOperation(value = "全选购物车中商品")
	public ServerResponse selectAll(HttpSession session) {
		logger.info("全选购物车中商品");
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return cartService.selectOrUnSelect(user.getId(), null, Constant.Cart.CHECKED);
	}

	/**
	 * 全不选购物车中商品
	 *
	 * @param session
	 * @return
	 */
	@PutMapping("/un_select_all.do")
	@ApiOperation(value = "全不选购物车中商品")
	public ServerResponse unselectAll(HttpSession session) {
		logger.info("全选购物车中商品");
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return cartService.selectOrUnSelect(user.getId(), null, Constant.Cart.UN_CHECKED);
	}

	/**
	 * 勾选购物车中某件商品
	 *
	 * @param session
	 * @param productId
	 * @return
	 */
	@PutMapping("/select.do")
	@ApiOperation(value = "勾选购物车中某件商品")
	@ApiImplicitParam(name = "productId", value = "商品id", required = true, dataType = "int", paramType = "query")
	public ServerResponse select(HttpSession session, Integer productId) {
		logger.info("勾选购物车中某件商品，productId: {}", productId);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return cartService.selectOrUnSelect(user.getId(), productId, Constant.Cart.CHECKED);
	}

	/**
	 * 取消勾选购物车中某件商品
	 *
	 * @param session
	 * @param productId
	 * @return
	 */
	@PutMapping("/un_select.do")
	@ApiOperation(value = "取消勾选购物车中某件商品")
	@ApiImplicitParam(name = "productId", value = "商品id", required = true, dataType = "int", paramType = "query")
	public ServerResponse unselect(HttpSession session, Integer productId) {
		logger.info("取消勾选购物车中某件商品，productId: {}", productId);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return cartService.selectOrUnSelect(user.getId(), productId, Constant.Cart.UN_CHECKED);
	}

	/**
	 * 查询购物车中商品总数
	 *
	 * @param session
	 * @return
	 */
	@GetMapping("/cart_product_count")
	@ApiOperation(value = "查询购物车中商品总数")
	public ServerResponse<Integer> getCartProductCount(HttpSession session) {
		logger.info("查询购物车中商品总数");
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createBySuccess(0);
		}

		return cartService.getCartProductCount(user.getId());
	}

}

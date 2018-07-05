package org.com.cay.mmall.controller.front;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ResponseCode;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.Shipping;
import org.com.cay.mmall.entity.User;
import org.com.cay.mmall.service.IShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by Caychen on 2018/7/5.
 */
@RestController
@RequestMapping("/shipping")
@Api("前台收货地址控制类")
public class ShippingController {

	private final Logger logger = LoggerFactory.getLogger(ShippingController.class);

	@Autowired
	private IShippingService shippingService;

	/**
	 * 新增收货地址
	 *
	 * @param session
	 * @param shipping
	 * @return
	 */
	@PostMapping("/add.do")
	@ApiOperation(value = "新增收货地址")
	public ServerResponse add(HttpSession session, Shipping shipping) {
		logger.info("新增收货地址，shipping: {}", shipping);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return shippingService.add(user.getId(), shipping);
	}

	/**
	 * 删除收货地址
	 *
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@DeleteMapping("/delete.do")
	@ApiOperation(value = "删除收货地址")
	@ApiImplicitParam(name = "shippingId", value = "需要删除的收货地址id", required = true, dataType = "int", paramType = "query")
	public ServerResponse delete(HttpSession session, Integer shippingId) {
		logger.info("删除收货地址，shippingId: {}", shippingId);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return shippingService.delete(user.getId(), shippingId);
	}

	/**
	 * 修改收货地址
	 *
	 * @param session
	 * @param shipping
	 * @return
	 */
	@PutMapping("/update.do")
	@ApiOperation(value = "修改收货地址")
	public ServerResponse update(HttpSession session, Shipping shipping) {
		logger.info("修改收货地址，shipping: {}", shipping);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return shippingService.update(user.getId(), shipping);
	}

	/**
	 * 查询收货地址
	 *
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@GetMapping("/select.do")
	@ApiOperation(value = "查询收货地址")
	@ApiImplicitParam(name = "shippingId", value = "需要查询的收货地址id", required = true, dataType = "int", paramType = "query")
	public ServerResponse<Shipping> select(HttpSession session, Integer shippingId) {
		logger.info("查询收货地址，shippingId: {}", shippingId);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return shippingService.select(user.getId(), shippingId);
	}

	/**
	 * 查询全部的收货地址，带分页
	 *
	 * @param pageNum
	 * @param pageSize
	 * @param session
	 * @return
	 */
	@GetMapping("/list.do")
	@ApiOperation(value = "查询全部的收货地址，带分页")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "页码", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", dataType = "int", paramType = "query")
	})
	public ServerResponse<PageInfo> list(
			@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
			HttpSession session) {
		logger.info("查询全部的收货地址(带分页)，pageNum: {}, pageSize: {}", pageNum, pageSize);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		return shippingService.list(user.getId(), pageNum, pageSize);
	}
}

package org.com.cay.mmall.controller.back;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ResponseCode;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.User;
import org.com.cay.mmall.service.IOrderService;
import org.com.cay.mmall.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by Caychen on 2018/7/8.
 */
@RestController
@RequestMapping("/manage/order")
public class OrderManageController {

	private final Logger logger = LoggerFactory.getLogger(OrderManageController.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private IOrderService orderService;

	/**
	 * 后台管理订单列表
	 *
	 * @param session
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/list.do")
	@ApiOperation(value = "后台管理订单列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页号", required = false, defaultValue = "1", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页记录条数", required = false, defaultValue = "10", dataType = "int", paramType = "query")
	})
	public ServerResponse<PageInfo> orderList(HttpSession session,
	                                          @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
	                                          @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

		logger.info("后台管理订单列表, pageNum: {}, pageSize: {}", pageNum, pageSize);

		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return orderService.manageOrderList(pageNum, pageSize);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	/**
	 * 后台管理订单详情
	 *
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@GetMapping("/detail.do")
	@ApiOperation(value = "后台管理订单详情")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, dataType = "long", paramType = "query")
	})
	public ServerResponse orderDetail(HttpSession session, Long orderNo) {

		logger.info("后台管理订单详情, orderNo: {}", orderNo);

		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return orderService.manageOrderDetail(orderNo);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	/**
	 * 后台管理订单查询
	 *
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@GetMapping("/search.do")
	@ApiOperation(value = "后台管理订单查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, dataType = "long", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页号", required = false, defaultValue = "1", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页记录条数", required = false, defaultValue = "10", dataType = "int", paramType = "query")
	})
	public ServerResponse orderListSearch(HttpSession session,
	                                      Long orderNo,
	                                      @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
	                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

		logger.info("后台管理订单查询, orderNo: {}", orderNo);

		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return orderService.manageOrderSearch(orderNo, pageNum, pageSize);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	/**
	 * 订单发货
	 *
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@PutMapping("/send_goods.do")
	@ApiOperation(value = "订单发货")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, dataType = "long", paramType = "query")
	})
	public ServerResponse orderSendGoods(HttpSession session, Long orderNo) {

		logger.info("订单发货, orderNo: {}", orderNo);

		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return orderService.manageOrderSendGoods(orderNo);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
}

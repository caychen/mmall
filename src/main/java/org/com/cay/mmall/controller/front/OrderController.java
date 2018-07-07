package org.com.cay.mmall.controller.front;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ResponseCode;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.User;
import org.com.cay.mmall.service.IOrderService;
import org.com.cay.mmall.utils.AlipayUtil;
import org.com.cay.mmall.utils.TaskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Caychen on 2018/7/6.
 */
@RestController
@RequestMapping("/order")
@Api("前台订单控制类")
public class OrderController {

	private final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private IOrderService orderService;

	/**
	 * 预支付订单，生成二维码
	 *
	 * @param session
	 * @param request
	 * @param orderNo
	 * @return
	 */
	@PostMapping("/prePay.do")
	@ApiOperation(value = "预支付订单，生成二维码")
	@ApiImplicitParam(name = "orderNo", value = "订单号", required = true, dataType = "Long", paramType = "query")
	public ServerResponse prePay(HttpSession session, HttpServletRequest request, Long orderNo) {
		logger.info("预支付订单，生成二维码，orderNo: {}", orderNo);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		//用于返回二维码
		String path = request.getSession().getServletContext().getRealPath("/upload");

		ServerResponse response = orderService.prePay(orderNo, user.getId(), path);
		if (response.isSuccess()) {
			//开启线程调用支付宝查询接口
			startThreadForQueryOrder(orderNo);
		}

		return response;
	}

	private void startThreadForQueryOrder(Long orderNo) {
		ScheduledFuture<?> future = TaskUtil.getExecutorService().scheduleAtFixedRate(() -> {
			try {
				AlipayTradeQueryResponse tradeQueryResponse = AlipayUtil.alipayTradeQuery(orderNo);
				Map map = new Gson().fromJson(tradeQueryResponse.getBody(), Map.class);
				Map responseMap = (Map) map.get("alipay_trade_query_response");
				int code = Integer.parseInt(responseMap.get("code").toString());

				if (code != Constant.PayCode.PAY_SUCCESS) {
					return;
				}
				String tradeStatus = (String) responseMap.get("trade_status");

				if (tradeStatus.equals(Constant.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS)) {
					//交易成功
					//线程停止
					Future future1 = TaskUtil.getFutureMap().remove(orderNo);
					future1.cancel(true);

					//更新数据库
					orderService.alipayCallback(responseMap);

				} else if (tradeStatus.equals(Constant.AlipayCallback.TRADE_STATUS_TRADE_CLOSED)) {
					//交易关闭
					//线程停止
					Future future1 = TaskUtil.getFutureMap().remove(orderNo);
					future1.cancel(true);
					//只更新订单状态
					orderService.updateStatusByOrderNo(orderNo, Constant.StatusEnum.CLOSED.getCode());

				} else if (tradeStatus.equals(Constant.AlipayCallback.TRADE_STATUS_WAIT_BUYER_PAY)) {
					//等待买家付款
					//循环等待
				}

			} catch (AlipayApiException e) {
				e.printStackTrace();
			}

		}, 1, 5, TimeUnit.SECONDS);

		TaskUtil.getFutureMap().put(orderNo, future);
	}


	/**
	 * 支付宝回调函数：用户扫描二维码支付后，支付宝后台推送给商户
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("/alipay_callback.do")
	@ApiOperation(value = "支付宝回调函数")
	public Object alipayCallback(HttpServletRequest request) {
		Map<String, String> params = Maps.newHashMap();

		Map<String, String[]> map = request.getParameterMap();
		logger.info("支付宝回调，sign: {}, trade_status: {}, 参数: {}", map.get("sign"), map.get("trade_status"), map.toString());

		for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
			String name = iter.next();
			String[] values = map.get(name);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < values.length; ++i) {
				sb.append(values[i]).append(",");
			}

			String valueStr = sb.substring(0, sb.length() - 1);
			params.put(name, valueStr);
		}

		//非常重要，验证回调函数的正确性，是不是支付宝发起的，并且还要避免重复通知
		map.remove("sign_type");

		try {
			boolean rsaCheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
			if (!rsaCheckV2) {
				return ServerResponse.createByErrorMessage("非法请求，验证不通知！");
			}

			//
			ServerResponse response = orderService.alipayCallback(params);
			if (response.isSuccess()) {
				return Constant.AlipayCallback.RESPONSE_SUCCESS;
			}

		} catch (AlipayApiException e) {
			logger.error("支付宝回调异常：", e);
			e.printStackTrace();
		}
		return Constant.AlipayCallback.RESPONSE_FAILED;
	}


	/**
	 * 查询订单状态
	 *
	 * @param session
	 * @param orderNo
	 * @return
	 */
	@GetMapping("/query_order_pay_status.do")
	@ApiOperation(value = "查询订单状态", notes = "凡是订单状态为已支付的（包括发货等）都返回true，其余返回false")
	@ApiImplicitParam(name = "orderNo", value = "订单号", required = true, dataType = "Long", paramType = "query")
	public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {
		logger.info("查询订单状态，orderNo: {}", orderNo);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		ServerResponse response = orderService.queryOrderPayStatus(user.getId(), orderNo);
		if (response.isSuccess()) {
			return ServerResponse.createBySuccess(true);
		}
		return ServerResponse.createBySuccess(false);
	}

	/**
	 * 创建订单
	 *
	 * @param session
	 * @param shippingId
	 * @return
	 */
	@PostMapping("/create.do")
	@ApiOperation(value = "创建订单")
	@ApiImplicitParam(name = "shippingId", value = "发货地址的id", required = true, dataType = "int", paramType = "query")
	public ServerResponse create(HttpSession session, Integer shippingId){
		logger.info("创建订单, shippingId: {}", shippingId);

		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}

		return orderService.createOrder(user.getId(), shippingId);
	}
}

package org.com.cay.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.dao.*;
import org.com.cay.mmall.entity.*;
import org.com.cay.mmall.service.IOrderService;
import org.com.cay.mmall.utils.BigDecimalUtil;
import org.com.cay.mmall.utils.DateTimeUtil;
import org.com.cay.mmall.utils.FTPUtil;
import org.com.cay.mmall.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Caychen on 2018/7/6.
 */
@Service
public class OrderServiceImpl implements IOrderService {

	private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderItemMapper orderItemMapper;

	@Autowired
	private PayInfoMapper payInfoMapper;

	@Autowired
	private CartMapper cartMapper;

	@Autowired
	private ProductMapper productMapper;

	@Override
	public ServerResponse prePay(Long orderNo, Integer userId, String path) {
		Map<String, String> resultMap = Maps.newHashMap();

		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if (order == null) {
			return ServerResponse.createByErrorMessage("该用户没有此订单：" + orderNo);
		}

		resultMap.put("orderNo", String.valueOf(orderNo));

		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
		// 需保证商户系统端不能重复，建议通过数据库sequence生成，
		String outTradeNo = orderNo.toString();

		// (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
		String subject = new StringBuilder().append("happymmall扫码支付，订单号：").append(orderNo).toString();

		// (必填) 订单总金额，单位为元，不能超过1亿元
		// 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
		String totalAmount = order.getPayment().toString();

		// (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
		// 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
		String undiscountableAmount = "0";

		// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
		// 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
		String sellerId = "";

		// 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
		String body = new StringBuilder().append("订单").append(orderNo).append("购买商品共").append(totalAmount).append("元").toString();

		// 商户操作员编号，添加此参数可以为商户操作员做销售统计
		String operatorId = "test_operator_id";

		// (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		String storeId = "test_store_id";

		// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
		ExtendParams extendParams = new ExtendParams();
		extendParams.setSysServiceProviderId("2088100200300400500");

		// 支付超时，定义为120分钟
		String timeoutExpress = "120m";

		// 商品明细列表，需填写购买商品详细信息，
		List<GoodsDetail> goodsDetailList = Lists.newArrayList();

		List<OrderItem> orderItemList = orderItemMapper.getByOrderNoAndUserId(userId, orderNo);
		orderItemList.stream().forEach(orderItem -> {
			// 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
			GoodsDetail goods = GoodsDetail.newInstance(
					orderItem.getProductId().toString(),
					orderItem.getProductName(),
					BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100)).longValue(),
					orderItem.getQuantity());
			// 创建好一个商品后添加至商品明细列表
			goodsDetailList.add(goods);
		});

		// 创建扫码支付请求builder，设置请求参数
		AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
				.setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
				.setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
				.setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
				.setTimeoutExpress(timeoutExpress)
				.setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
				.setGoodsDetailList(goodsDetailList);

		/** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
		 *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
		 */
		Configs.init("zfbinfo.properties");

		/** 使用Configs提供的默认参数
		 *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
		 */
		//默认使用utf-8
		AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

		AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
		switch (result.getTradeStatus()) {
			case SUCCESS:
				logger.info("支付宝预下单成功: )");

				AlipayTradePrecreateResponse response = result.getResponse();
				dumpResponse(response);

				//创建二维码
				File folder = new File(path);
				if (!folder.exists()) {
					folder.setWritable(true);
					folder.mkdirs();
				}

				// 需要修改为运行机器上的路径
				String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
				logger.info("qrPath:" + qrPath);

				String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
				ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

				File targetFile = new File(path, qrFileName);
				try {
					//上传二维码到ftp服务器上
					FTPUtil.uploadFile(Lists.newArrayList(targetFile));
				} catch (IOException e) {
					logger.error("上传二维码异常：", e);
					e.printStackTrace();
				}

				//二维码的访问路径
				String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
				resultMap.put("qrUrl", qrUrl);
				return ServerResponse.createBySuccess(resultMap);

			case FAILED:
				logger.error("支付宝预下单失败!!!");
				return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

			case UNKNOWN:
				logger.error("系统异常，预下单状态未知!!!");
				return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

			default:
				logger.error("不支持的交易状态，交易返回异常!!!");
				return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
		}

	}

	@Override
	public ServerResponse alipayCallback(Map<String, String> params) {
		//原支付请求的商户订单号
		Long orderNo = Long.parseLong(params.get("out_trade_no"));

		//支付宝交易凭证号
		String tradeNo = params.get("trade_no");

		//交易状态
		String tradeStatus = params.get("trade_status");

		Order order = orderMapper.selectByOrderNo(orderNo);
		if (order == null) {
			return ServerResponse.createByErrorMessage("订单不存在，忽略!!!");
		}

		if (order.getStatus() >= Constant.StatusEnum.PAID.getCode()) {
			return ServerResponse.createBySuccess("支付宝重复调用!!!");
		}

		//交易完成
		if (Constant.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
			order.setStatus(Constant.StatusEnum.PAID.getCode());
			order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment") != null ? params.get("gmt_payment") : params.get("send_pay_date")));
			orderMapper.updateByPrimaryKeySelective(order);
		}

		PayInfo payInfo = new PayInfo();
		payInfo.setUserId(order.getUserId());
		payInfo.setOrderNo(orderNo);
		payInfo.setPayPlatform(Constant.PayPlatformEnum.ALIPAY.getCode());
		payInfo.setPlatformNumber(tradeNo);
		payInfo.setPlatformStatus(tradeStatus);

		payInfoMapper.insert(payInfo);
		return ServerResponse.createBySuccess();
	}

	@Override
	public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
		Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
		if (order == null) {
			return ServerResponse.createByErrorMessage("该用户没有此订单：" + orderNo);
		}
		if (order.getStatus() >= Constant.StatusEnum.PAID.getCode()) {
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}

	@Override
	public ServerResponse updateStatusByOrderNo(Long orderNo, int statusCode) {
		int count = orderMapper.updateStatusByOrderNo(orderNo, statusCode);
		if (count > 0) {
			return ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}

	@Override
	public ServerResponse createOrder(Integer userId, Integer shippingId) {
		//从购物车中获取已勾选的cart数据
		List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

		//计算订单总价
		ServerResponse orderItemListResponse = getCartOrderItem(userId, cartList);
		if (!orderItemListResponse.isSuccess()) {
			return orderItemListResponse;
		}

		List<OrderItem> orderItemList = (List<OrderItem>) orderItemListResponse.getData();

		if(CollectionUtils.isEmpty(orderItemList)){
			return ServerResponse.createByErrorMessage("购物车为空！");
		}

		//获取订单总金额
		BigDecimal payment = getOrderTotalPrice(orderItemList);

		//生成订单
		Order order = assembleOrder(userId, shippingId, payment);
		if (order == null){
			return ServerResponse.createByErrorMessage("生成订单失败！");
		}

		orderItemList.stream().forEach(orderItem -> orderItem.setOrderNo(order.getOrderNo()));

		//批量插入orderItem
		orderItemMapper.batchInsert(orderItemList);

		//生成成功, 减少商品库存
		reductProductStock(orderItemList);

		//清空购物车
		cleanCart(cartList);
		return null;
	}

	private void cleanCart(List<Cart> cartList) {
		cartList.stream().forEach(cart -> {
			cartMapper.deleteByPrimaryKey(cart.getId());
		});
	}

	/**
	 * 减少商品库存
	 *
	 * @param orderItemList
	 */
	private void reductProductStock(List<OrderItem> orderItemList) {
		orderItemList.stream().forEach(orderItem -> {
			Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());

			product.setStock(product.getStock() - orderItem.getQuantity());
			productMapper.updateByPrimaryKeySelective(product);
		});
	}

	private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
		Long orderNo = generatorOrderNo();
		Order order = new Order();

		order.setOrderNo(orderNo);
		order.setStatus(Constant.StatusEnum.NO_PAY.getCode());
		order.setPaymentType(Constant.PaymentTypeEnum.ONLINE_PAY.getCode());
		order.setPostage(0);//暂时设置为全包邮
		order.setPayment(payment);
		order.setUserId(userId);
		order.setShippingId(shippingId);

		int count = orderMapper.insert(order);
		if (count > 0) {
			return order;
		}
		return null;
	}

	/**
	 * 订单号生成(时间戳 + 随机数)
	 *
	 * @return
	 */
	private Long generatorOrderNo() {
		long currentTime = System.currentTimeMillis();
		return currentTime + new Random().nextInt(100);
	}

	private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
		BigDecimal payment = new BigDecimal("0");
		for (OrderItem orderItem : orderItemList) {
			payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
		}

		return payment;
	}

	// 简单打印应答
	private void dumpResponse(AlipayResponse response) {
		if (response != null) {
			logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
			if (StringUtils.isNotEmpty(response.getSubCode())) {
				logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
						response.getSubMsg()));
			}
			logger.info("body:" + response.getBody());
		}
	}

	/**
	 * 获取订单明细
	 *
	 * @param userId
	 * @param cartList
	 * @return
	 */
	private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
		List<OrderItem> orderItemList = Lists.newArrayList();

		if (CollectionUtils.isEmpty(orderItemList)) {
			return ServerResponse.createByErrorMessage("购物车为空！");
		}

		for (Cart cart : cartList) {
			OrderItem orderItem = new OrderItem();

			Product product = productMapper.selectByPrimaryKey(cart.getProductId());
			if (product.getStatus() != Constant.ProductStatusEnum.ON_SALE.getCode()) {
				return ServerResponse.createByErrorMessage("商品" + product.getName() + "不是在线售卖状态！");
			}

			//校验库存
			if (cart.getQuantity() > product.getStock()) {
				return ServerResponse.createByErrorMessage("商品" + product.getName() + "库存不足！");
			}

			orderItem.setUserId(userId);
			orderItem.setProductId(product.getId());
			orderItem.setProductName(product.getName());
			orderItem.setProductImage(product.getMainImage());
			orderItem.setCurrentUnitPrice(product.getPrice());
			orderItem.setQuantity(cart.getQuantity());
			orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity()));
			orderItemList.add(orderItem);
		}

		return ServerResponse.createBySuccess(orderItemList);
	}
}

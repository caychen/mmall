package org.com.cay.mmall.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by Caychen on 2018/7/6.
 */
public class AlipayUtil {

	private static AlipayClient alipayClient = new DefaultAlipayClient(
			PropertiesUtil.getProperty("open_api_domain"),
			PropertiesUtil.getProperty("appid"),
			PropertiesUtil.getProperty("private_key"),
			"json",
			"utf-8",
			PropertiesUtil.getProperty("alipay_public_key"),
			"RSA2"); //获得初始化的AlipayClient

	public static AlipayTradeQueryResponse alipayTradeQuery(Long orderNo) throws AlipayApiException {
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
		request.setBizContent("{" +
				"    \"out_trade_no\":\"" + orderNo + "\"," +
				"    \"trade_no\":\"\"}"); //设置业务参数
		AlipayTradeQueryResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
		return response;
	}

	public static void main(String[] args) throws AlipayApiException {
		String body = AlipayUtil.alipayTradeQuery(1491830695216L).getBody();
		Gson gson = new Gson();
		Map map = gson.fromJson(body, Map.class);
		System.out.println(map.get("alipay_trade_query_response"));

	}
}

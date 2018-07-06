package org.com.cay.mmall.service;

import org.com.cay.mmall.common.ServerResponse;

import java.util.Map;

/**
 * Created by Caychen on 2018/7/6.
 */
public interface IOrderService {

	ServerResponse pay(Long orderNo, Integer userId, String path);

	ServerResponse alipayCallback(Map<String, String> params);

	ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

	ServerResponse updateStatusByOrderNo(Long orderNo, int statusCode);
}

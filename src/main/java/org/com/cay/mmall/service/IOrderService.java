package org.com.cay.mmall.service;

import com.github.pagehelper.PageInfo;
import org.com.cay.mmall.common.ServerResponse;

import java.util.Map;

/**
 * Created by Caychen on 2018/7/6.
 */
public interface IOrderService {

	ServerResponse prePay(Long orderNo, Integer userId, String path);

	ServerResponse alipayCallback(Map<String, String> params);

	ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

	ServerResponse updateStatusByOrderNo(Long orderNo, int statusCode);

	ServerResponse<Object> createOrder(Integer userId, Integer shippingId);

	ServerResponse cancelOrder(Integer userId, Long orderNo);

	ServerResponse getOrderCartProduct(Integer userId);

	ServerResponse getOrderDetail(Integer userId, Long orderNo);

	ServerResponse<PageInfo> getOrderList(Integer userId, Integer pageNum, Integer pageSize);

	ServerResponse<PageInfo> manageOrderList(Integer pageNum, Integer pageSize);

	ServerResponse manageOrderDetail(Long orderNo);

	ServerResponse manageOrderSearch(Long orderNo, Integer pageNum, Integer pageSize);

	ServerResponse manageOrderSendGoods(Long orderNo);
}

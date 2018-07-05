package org.com.cay.mmall.service;

import com.github.pagehelper.PageInfo;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.Shipping;

/**
 * Created by Caychen on 2018/7/5.
 */
public interface IShippingService {

	ServerResponse add(Integer userId, Shipping shipping);

	ServerResponse delete(Integer userId, Integer shippingId);

	ServerResponse update(Integer userId, Shipping shipping);

	ServerResponse<Shipping> select(Integer userId, Integer shippingId);

	ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);
}

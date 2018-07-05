package org.com.cay.mmall.service;

import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.vo.CartVo;

/**
 * Created by Caychen on 2018/7/5.
 */
public interface ICartService {

	ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId);

	ServerResponse<CartVo> update(Integer userId, Integer count, Integer productId);

	ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

	ServerResponse<CartVo> list(Integer userId);

	ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

	ServerResponse<Integer> getCartProductCount(Integer userId);
}

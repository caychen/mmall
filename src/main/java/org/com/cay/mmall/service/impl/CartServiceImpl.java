package org.com.cay.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ResponseCode;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.dao.CartMapper;
import org.com.cay.mmall.dao.ProductMapper;
import org.com.cay.mmall.entity.Cart;
import org.com.cay.mmall.entity.Product;
import org.com.cay.mmall.service.ICartService;
import org.com.cay.mmall.utils.BigDecimalUtil;
import org.com.cay.mmall.utils.PropertiesUtil;
import org.com.cay.mmall.vo.CartProductVo;
import org.com.cay.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Caychen on 2018/7/5.
 */
@Service
public class CartServiceImpl implements ICartService {

	@Autowired
	private CartMapper cartMapper;

	@Autowired
	private ProductMapper productMapper;

	@Override
	public ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId) {
		if (productId == null || count == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}

		Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
		if (cart == null) {
			//不存在，则新增
			cart = new Cart();
			cart.setQuantity(count);
			cart.setUserId(userId);
			cart.setProductId(productId);
			cart.setChecked(Constant.Cart.CHECKED);

			cartMapper.insert(cart);
		} else {
			//已存在，则修改
			count += cart.getQuantity();
			cart.setQuantity(count);
			cartMapper.updateByPrimaryKey(cart);
		}

		return this.list(userId);
	}

	@Override
	public ServerResponse<CartVo> update(Integer userId, Integer count, Integer productId) {
		if (productId == null || count == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}

		Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
		if (cart != null) {
			cart.setQuantity(count);
		}
		cartMapper.updateByPrimaryKeySelective(cart);
		return this.list(userId);
	}

	@Override
	public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
		List<String> productIdList = Splitter.on(",").splitToList(productIds);
		if (CollectionUtils.isEmpty(productIdList)) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}

		cartMapper.deleteByUserIdProductIds(userId, productIdList);
		return this.list(userId);
	}

	@Override
	public ServerResponse<CartVo> list(Integer userId) {
		CartVo cartVo = getCartVoLimit(userId);
		return ServerResponse.createBySuccess(cartVo);
	}

	@Override
	public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
		cartMapper.checkedOrUnchecked(userId, productId, checked);
		return this.list(userId);
	}

	@Override
	public ServerResponse<Integer> getCartProductCount(Integer userId) {
		if (userId == null) {
			return ServerResponse.createBySuccess(0);
		}
		return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
	}

	private CartVo getCartVoLimit(Integer userId) {
		CartVo cartVo = new CartVo();

		//根据userId获取购物车列表
		List<Cart> cartList = cartMapper.selectCartByUserId(userId);

		List<CartProductVo> cartProductVoList = Lists.newArrayList();

		BigDecimal cartTotalPrice = new BigDecimal("0.00");

		for (Cart cart : cartList) {
			CartProductVo cartProductVo = new CartProductVo();
			cartProductVo.setId(cart.getId());
			cartProductVo.setUserId(userId);
			cartProductVo.setProductId(cart.getProductId());

			Product product = productMapper.selectByPrimaryKey(cart.getProductId());
			if (product != null) {
				cartProductVo.setProductMainImage(product.getMainImage());
				cartProductVo.setProductName(product.getName());
				cartProductVo.setProductSubtitle(product.getSubtitle());
				cartProductVo.setProductStatus(product.getStatus());
				cartProductVo.setProductPrice(product.getPrice());
				cartProductVo.setProductStock(product.getStock());

				//判断库存
				int buyLimitCount = 0;
				if (product.getStock() > cart.getQuantity()) {
					buyLimitCount = cart.getQuantity();
					cartProductVo.setLimitQuantity(Constant.Cart.LIMIT_NUM_SUCCESS);
				} else {
					//当购物车中此商品数据大于库存时，需要更新购物车中此商品的数量
					buyLimitCount = product.getStock();
					cartProductVo.setLimitQuantity(Constant.Cart.LIMIT_NUM_FAIL);

					//更新购物车中有效库存
					Cart cartForQuantity = new Cart();
					cartForQuantity.setId(cart.getId());
					cartForQuantity.setQuantity(buyLimitCount);
					cartMapper.updateByPrimaryKeySelective(cartForQuantity);
				}

				cartProductVo.setQuantity(buyLimitCount);

				//计算该商品的总价
				cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
				cartProductVo.setProductChecked(cart.getChecked());
			}

			if (cart.getChecked() == Constant.Cart.CHECKED) {
				//如果已经勾选，则增加到购物车总价中
				cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
			}

			cartProductVoList.add(cartProductVo);
		}

		cartVo.setCartTotalPrice(cartTotalPrice);
		cartVo.setCartProductVoList(cartProductVoList);
		cartVo.setAllChecked(getAllCheckedStatus(userId));
		cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		return cartVo;
	}

	private boolean getAllCheckedStatus(Integer userId) {
		if (userId == null) {
			return false;
		}

		return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
	}
}

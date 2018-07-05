package org.com.cay.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.dao.ShippingMapper;
import org.com.cay.mmall.entity.Shipping;
import org.com.cay.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Caychen on 2018/7/5.
 */
@Service
public class ShippingServiceImpl implements IShippingService {

	@Autowired
	private ShippingMapper shippingMapper;

	@Override
	public ServerResponse add(Integer userId, Shipping shipping) {
		shipping.setUserId(userId);
		int count = shippingMapper.insert(shipping);
		if (count > 0) {
			Map result = Maps.newHashMap();
			result.put("shippingId", shipping.getId());
			return ServerResponse.createBySuccess("新建地址成功！", result);
		}
		return ServerResponse.createByErrorMessage("新建地址失败！");
	}

	@Override
	public ServerResponse delete(Integer userId, Integer shippingId) {
		int count = shippingMapper.deleteByShippingIdUserId(userId, shippingId);
		if (count > 0) {
			return ServerResponse.createBySuccess("删除地址成功！");
		}
		return ServerResponse.createByErrorMessage("删除地址失败！");
	}

	@Override
	public ServerResponse update(Integer userId, Shipping shipping) {
		shipping.setUserId(userId);
		int count = shippingMapper.updateShipping(shipping);
		if (count > 0) {
			return ServerResponse.createBySuccess("修改地址成功！");
		}
		return ServerResponse.createByErrorMessage("修改地址失败！");
	}

	@Override
	public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
		Shipping shipping = shippingMapper.selectByShippingIdUserId(userId, shippingId);
		if (shipping == null) {
			return ServerResponse.createByErrorMessage("无法查询到该收货地址！");
		}
		return ServerResponse.createBySuccess("查询收货地址成功！", shipping);
	}

	@Override
	public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Shipping> shippingList = shippingMapper.selectByUserId(userId);

		PageInfo pageInfo = new PageInfo(shippingList);

		return ServerResponse.createBySuccess(pageInfo);
	}
}

package org.com.cay.mmall.dao;

import org.com.cay.mmall.entity.OrderItem;

public interface OrderItemMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(OrderItem record);

	int insertSelective(OrderItem record);

	OrderItem selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(OrderItem record);

	int updateByPrimaryKey(OrderItem record);
}
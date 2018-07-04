package org.com.cay.mmall.dao;

import org.com.cay.mmall.entity.Shipping;

public interface ShippingMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Shipping record);

	int insertSelective(Shipping record);

	Shipping selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Shipping record);

	int updateByPrimaryKey(Shipping record);
}
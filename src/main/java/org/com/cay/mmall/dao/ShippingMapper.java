package org.com.cay.mmall.dao;

import org.apache.ibatis.annotations.Param;
import org.com.cay.mmall.entity.Shipping;

import java.util.List;

public interface ShippingMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Shipping record);

	int insertSelective(Shipping record);

	Shipping selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Shipping record);

	int updateByPrimaryKey(Shipping record);

	int deleteByShippingIdUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

	int updateShipping(Shipping shipping);

	Shipping selectByShippingIdUserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

	List<Shipping> selectByUserId(Integer userId);
}
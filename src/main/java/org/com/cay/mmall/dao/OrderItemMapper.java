package org.com.cay.mmall.dao;

import org.apache.ibatis.annotations.Param;
import org.com.cay.mmall.entity.OrderItem;

import java.util.List;

public interface OrderItemMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(OrderItem record);

	int insertSelective(OrderItem record);

	OrderItem selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(OrderItem record);

	int updateByPrimaryKey(OrderItem record);

	List<OrderItem> getByOrderNoAndUserId(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

	void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);
}
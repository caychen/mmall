package org.com.cay.mmall.dao;

import org.apache.ibatis.annotations.Param;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.Order;

public interface OrderMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Order record);

	int insertSelective(Order record);

	Order selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Order record);

	int updateByPrimaryKey(Order record);

	Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

	Order selectByOrderNo(Long orderNo);

	int updateStatusByOrderNo(@Param("orderNo") Long orderNo, @Param("statusCode") int statusCode);
}
package org.com.cay.mmall.dao;

import org.apache.ibatis.annotations.Param;
import org.com.cay.mmall.entity.Cart;

import java.util.List;

public interface CartMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Cart record);

	int insertSelective(Cart record);

	Cart selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Cart record);

	int updateByPrimaryKey(Cart record);

	Cart selectCartByUserIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

	List<Cart> selectCartByUserId(Integer userId);

	int selectCartProductCheckedStatusByUserId(Integer userId);

	int deleteByUserIdProductIds(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

	int checkedOrUnchecked(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);

	int selectCartProductCount(Integer userId);

	List<Cart> selectCheckedCartByUserId(Integer userId);
}
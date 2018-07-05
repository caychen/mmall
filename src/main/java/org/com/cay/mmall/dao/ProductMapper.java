package org.com.cay.mmall.dao;

import org.apache.ibatis.annotations.Param;
import org.com.cay.mmall.entity.Product;

import java.util.List;

public interface ProductMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Product record);

	int insertSelective(Product record);

	Product selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Product record);

	int updateByPrimaryKey(Product record);

	List<Product> selectList();

	List<Product> selectByNameAndId(@Param("productId") Integer productId, @Param("productName") String productName);

	List<Product> selectByCondition(@Param("productName")String productName, @Param("categoryIdList")List<Integer> categoryIdList);
}
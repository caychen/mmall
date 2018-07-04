package org.com.cay.mmall.dao;

import org.com.cay.mmall.entity.PayInfo;

public interface PayInfoMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(PayInfo record);

	int insertSelective(PayInfo record);

	PayInfo selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(PayInfo record);

	int updateByPrimaryKey(PayInfo record);
}
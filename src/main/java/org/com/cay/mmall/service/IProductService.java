package org.com.cay.mmall.service;

import com.github.pagehelper.PageInfo;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.Product;
import org.com.cay.mmall.vo.ProductDetailVo;

/**
 * Created by Caychen on 2018/7/5.
 */
public interface IProductService {

	ServerResponse saveOrUpdateProduct(Product product);

	ServerResponse setSaleStatus(Integer productId, Integer status);

	ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

	ServerResponse getProductList(Integer pageNum, Integer pageSize);

	ServerResponse searchProduct(Integer productId, String productName, Integer pageNum, Integer pageSize);

	ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

	ServerResponse<PageInfo> getProductByCondition(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy);
}

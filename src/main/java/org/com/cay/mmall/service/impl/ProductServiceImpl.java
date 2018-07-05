package org.com.cay.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ResponseCode;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.dao.CategoryMapper;
import org.com.cay.mmall.dao.ProductMapper;
import org.com.cay.mmall.entity.Category;
import org.com.cay.mmall.entity.Product;
import org.com.cay.mmall.service.ICategoryService;
import org.com.cay.mmall.service.IProductService;
import org.com.cay.mmall.utils.DateTimeUtil;
import org.com.cay.mmall.utils.PropertiesUtil;
import org.com.cay.mmall.vo.ProductDetailVo;
import org.com.cay.mmall.vo.ProductListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Caychen on 2018/7/5.
 */
@Service
public class ProductServiceImpl implements IProductService {

	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private CategoryMapper categoryMapper;

	@Autowired
	private ICategoryService categoryService;

	@Override
	public ServerResponse saveOrUpdateProduct(Product product) {
		if (product != null) {
			if (StringUtils.isNoneBlank(product.getSubImages())) {
				String[] subImageArray = product.getSubImages().split(",");
				if (subImageArray.length > 0) {
					//设置主图
					product.setMainImage(subImageArray[0]);
				}
			}

			if (product.getId() != null) {
				//更新
				int count = productMapper.updateByPrimaryKey(product);
				if (count > 0) {
					return ServerResponse.createBySuccess("更新商品成功！");
				}
				return ServerResponse.createBySuccess("更新商品失败！");
			} else {
				//新增
				int count = productMapper.insert(product);
				if (count > 0) {
					return ServerResponse.createBySuccess("新增商品成功！");
				}
				return ServerResponse.createBySuccess("新增商品失败！");
			}
		}
		return ServerResponse.createByErrorMessage("新增或更新商品参数不正确！");
	}

	@Override
	public ServerResponse setSaleStatus(Integer productId, Integer status) {
		if (productId == null || status == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}

		Product p = new Product();
		p.setId(productId);
		p.setStatus(status);

		int count = productMapper.updateByPrimaryKeySelective(p);
		if (count > 0) {
			return ServerResponse.createBySuccess("修改商品销售状态成功！");
		}
		return ServerResponse.createByErrorMessage("修改商品销售状态失败！");
	}

	@Override
	public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
		if (productId == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product == null) {
			return ServerResponse.createByErrorMessage("商品已下架或者删除不存在！");
		}

		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServerResponse.createBySuccess(productDetailVo);
	}

	@Override
	public ServerResponse getProductList(Integer pageNum, Integer pageSize) {
		//分页开始
		PageHelper.startPage(pageNum, pageSize);
		List<Product> productList = productMapper.selectList();

		//封装vo
		List<ProductListVo> productListVoList = Lists.newArrayList();
		productList.stream().forEach(product -> productListVoList.add(assembleProductListVo(product)));

		//分页结束
		PageInfo pageInfo = new PageInfo(productList);
		pageInfo.setList(productListVoList);

		return ServerResponse.createBySuccess(pageInfo);
	}

	@Override
	public ServerResponse searchProduct(Integer productId, String productName, Integer pageNum, Integer pageSize) {

		//分页开始
		PageHelper.startPage(pageNum, pageSize);
		if (StringUtils.isNoneBlank(productName)) {
			productName = new StringBuilder("%").append(productName).append("%").toString();
		}
		List<Product> productList = productMapper.selectByNameAndId(productId, productName);

		//封装vo
		List<ProductListVo> productListVoList = Lists.newArrayList();
		productList.stream().forEach(product -> productListVoList.add(assembleProductListVo(product)));

		//分页结束
		PageInfo pageInfo = new PageInfo(productList);
		pageInfo.setList(productListVoList);

		return ServerResponse.createBySuccess(pageInfo);
	}

	@Override
	public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
		if (productId == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product == null) {
			return ServerResponse.createByErrorMessage("商品已下架或者删除不存在！");
		}
		if(product.getStatus() != Constant.ProductStatusEnum.ON_SALE.getCode()){
			//不是在售状态
			return ServerResponse.createByErrorMessage("商品已下架或者删除不存在！");
		}

		ProductDetailVo productDetailVo = assembleProductDetailVo(product);
		return ServerResponse.createBySuccess(productDetailVo);
	}

	@Override
	public ServerResponse<PageInfo> getProductByCondition(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
		if(StringUtils.isBlank(keyword) && categoryId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}

		List<Integer> categoryIdList = Lists.newArrayList();
		if(categoryId != null){
			Category category = categoryMapper.selectByPrimaryKey(categoryId);
			if(category == null && StringUtils.isBlank(keyword)){
				//没有该分类，并且还没有关键字，这个时候返回一个空集合，不报错
				PageHelper.startPage(pageNum, pageSize);
				List<ProductListVo> productListVoList = Lists.newArrayList();
				PageInfo pageInfo = new PageInfo(productListVoList);

				return ServerResponse.createBySuccess(pageInfo);
			}

			categoryIdList = categoryService.selectCategoryAndChildrenById(categoryId).getData();
		}
		if(StringUtils.isNotBlank(keyword)){
			keyword = new StringBuilder("%").append(keyword).append("%").toString();
		}

		PageHelper.startPage(pageNum, pageSize);
		//排序处理
		if(StringUtils.isNotBlank(orderBy)){
			if(Constant.OrderBy.PRICE_ASC_DESC.contains(orderBy)){
				String[] orderByArray = orderBy.split("_");
				PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
			}
		}

		List<Product> productList = productMapper.selectByCondition(keyword, categoryIdList);
		List<ProductListVo> productListVoList = Lists.newArrayList();
		productList.stream().forEach(product -> productListVoList.add(assembleProductListVo(product)));

		PageInfo pageInfo = new PageInfo(productList);
		pageInfo.setList(productListVoList);
		return ServerResponse.createBySuccess(pageInfo);
	}

	private ProductDetailVo assembleProductDetailVo(Product product) {
		ProductDetailVo productDetailVo = new ProductDetailVo();

		//复制属性
		BeanUtils.copyProperties(product, productDetailVo, "createTime", "updateTime");

		//imageHost
		productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));

		//parentCategoryId
		Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
		productDetailVo.setParentCategoryId(category == null ? 0 : category.getParentId());

		//createTime
		productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));

		//updateTime
		productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

		return productDetailVo;
	}

	private ProductListVo assembleProductListVo(Product product) {
		ProductListVo productListVo = new ProductListVo();
		BeanUtils.copyProperties(product, productListVo);

		productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));

		return productListVo;
	}
}

package org.com.cay.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.dao.CategoryMapper;
import org.com.cay.mmall.entity.Category;
import org.com.cay.mmall.service.ICategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by caychen on 2018/7/4.
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

	private final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

	@Autowired
	private CategoryMapper categoryMapper;

	@Override
	public ServerResponse addCategory(String categoryName, Integer parentId) {
		if (parentId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.createByErrorMessage("添加品类参数错误！");
		}

		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);//可用状态

		int count = categoryMapper.insert(category);
		if (count > 0) {
			return ServerResponse.createBySuccessMessage("添加品类成功！");
		}

		return ServerResponse.createByErrorMessage("添加品类失败！");
	}

	@Override
	public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
		if (categoryId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.createByErrorMessage("更新品类参数错误！");
		}

		Category category = new Category();
		category.setName(categoryName);
		category.setId(categoryId);
		int count = categoryMapper.updateByPrimaryKeySelective(category);
		if (count > 0) {
			return ServerResponse.createBySuccessMessage("更新品类成功！");
		}

		return ServerResponse.createByErrorMessage("更新品类失败！");
	}

	@Override
	public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
		List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		if(CollectionUtils.isEmpty(categories)){
			logger.info("未找到当前分类的子分类！");
		}
		return ServerResponse.createBySuccess(categories);
	}

	@Override
	public ServerResponse selectCategoryAndChildrenById(Integer categoryId) {
		Set<Category> categorySet = Sets.newHashSet();
		findChildCategory(categorySet, categoryId);


		List<Integer> categoryIdList = Lists.newArrayList();
		if(categoryId != null){
			categoryIdList = categorySet.stream().map(category -> category.getId()).collect(Collectors.toList());
		}
		return ServerResponse.createBySuccess(categoryIdList);
	}

	//递归算法
	private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		if(category != null){
			categorySet.add(category);
		}

		//查找子节点
		List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
		categoryList.stream().forEach(category1 -> findChildCategory(categorySet, category1.getId()));

		return categorySet;
	}
}

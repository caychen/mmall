package org.com.cay.mmall.service;

import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.Category;

import java.util.List;

/**
 * Created by caychen on 2018/7/4.
 */
public interface ICategoryService {

	ServerResponse addCategory(String categoryName, Integer parentId);

	ServerResponse updateCategoryName(Integer categoryId, String categoryName);

	ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

	ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}

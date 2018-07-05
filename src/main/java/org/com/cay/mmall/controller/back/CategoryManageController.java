package org.com.cay.mmall.controller.back;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ResponseCode;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.User;
import org.com.cay.mmall.service.ICategoryService;
import org.com.cay.mmall.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by caychen on 2018/7/4.
 */
@RestController
@RequestMapping("/manage/category")
@Api(value = "后台管理的品类模块控制类")
public class CategoryManageController {

	private final Logger logger = LoggerFactory.getLogger(CategoryManageController.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private ICategoryService categoryService;

	/**
	 * 新增品类分类
	 *
	 * @param session
	 * @param categoryName
	 * @param parentId
	 * @return
	 */
	@PostMapping("/add_category.do")
	@ApiOperation(value = "新增品类分类")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryName", value = "品类名称", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "parentId", value = "父节点id", required = false, defaultValue = "0", dataType = "int", paramType = "query")
	})
	public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
		logger.info("新增品类分类, categoryName: {}, parentId: {}", categoryName, parentId);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return categoryService.addCategory(categoryName, parentId);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	/**
	 * 更新品类名称
	 *
	 * @param session
	 * @param categoryId
	 * @param categoryName
	 * @return
	 */
	@PutMapping("/set_category_name.do")
	@ApiOperation(value = "更新品类名称")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryId", value = "节点id", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryName", value = "品类名称", required = true, dataType = "String", paramType = "query")
	})
	public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
		logger.info("更新品类名称, categoryId: {}, categoryName: {}", categoryId, categoryName);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return categoryService.updateCategoryName(categoryId, categoryName);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	/**
	 * 获取当前节点的子节点（平级，无需递归）
	 *
	 * @param session
	 * @param categoryId
	 * @return
	 */
	@GetMapping("/get_category_parallel.do")
	@ApiOperation(value = "获取当前节点的子节点（平级，无需递归）")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryId", value = "节点id", required = false, defaultValue = "0", dataType = "int", paramType = "query"),
	})
	public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
		logger.info("获取当前节点的子节点（平级，无需递归）, categoryId: {}", categoryId);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑，查询子节点的category信息，平级不递归
			return categoryService.getChildrenParallelCategory(categoryId);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	@GetMapping("/get_category_deep.do")
	@ApiOperation(value = "获取当前节点的子节点（需递归）")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryId", value = "节点id", required = false, defaultValue = "0", dataType = "int", paramType = "query"),
	})
	public ServerResponse getDeepCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
		logger.info("获取当前节点的子节点（需递归）, categoryId: {}", categoryId);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑，查询子节点的category信息，需递归
			//查询当前节点的id和递归子节点的id
			return categoryService.selectCategoryAndChildrenById(categoryId);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}
}

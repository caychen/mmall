package org.com.cay.mmall.controller.back;

import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.com.cay.mmall.common.Constant;
import org.com.cay.mmall.common.ResponseCode;
import org.com.cay.mmall.common.ServerResponse;
import org.com.cay.mmall.entity.Product;
import org.com.cay.mmall.entity.User;
import org.com.cay.mmall.service.IFileService;
import org.com.cay.mmall.service.IProductService;
import org.com.cay.mmall.service.IUserService;
import org.com.cay.mmall.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Caychen on 2018/7/5.
 */
@RestController
@RequestMapping("/manage/product")
@Api("后台管理的商品模块控制类")
public class ProductManageController {

	private final Logger logger = LoggerFactory.getLogger(ProductManageController.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IFileService fileService;

	/**
	 * 后台新增商品
	 *
	 * @param session
	 * @param product
	 * @return
	 */
	@PostMapping("/save.do")
	@ApiOperation(value = "后台新增商品")
	public ServerResponse productSave(HttpSession session, Product product) {
		logger.info("后台新增商品，product: {}", product);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return productService.saveOrUpdateProduct(product);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	/**
	 * 后台更新商品销售状态
	 *
	 * @param session
	 * @param productId
	 * @param status
	 * @return
	 */
	@PutMapping("/set_sale_status.do")
	@ApiOperation(value = "后台更新商品销售状态")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "productId", value = "商品id号", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "status", value = "商品状态", required = true, dataType = "int", paramType = "query")
	})
	public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
		logger.info("后台更新商品销售状态，productId: {}, status: {}", productId, status);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return productService.setSaleStatus(productId, status);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	@GetMapping("/detail.do")
	@ApiOperation(value = "后台商品详情")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "productId", value = "商品id号", required = true, dataType = "int", paramType = "query")
	})
	public ServerResponse getDetail(HttpSession session, Integer productId) {
		logger.info("后台商品详情，productId: {}", productId);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return productService.manageProductDetail(productId);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	@GetMapping("/list.do")
	@ApiOperation(value = "后台商品列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页号", required = false, defaultValue = "1", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页记录条数", required = false, defaultValue = "10", dataType = "int", paramType = "query")
	})
	public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
		logger.info("后台商品列表，pageNum: {}, pageSize: {}", pageNum, pageSize);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return productService.getProductList(pageNum, pageSize);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	/**
	 * 后台商品列表查询
	 *
	 * @param session
	 * @param productName
	 * @param productId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/search.do")
	@ApiOperation(value = "后台商品列表查询")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageNum", value = "当前页号", required = false, defaultValue = "1", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页记录条数", required = false, defaultValue = "10", dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "productId", value = "商品id", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "productName", value = "商品名称", required = false, dataType = "String", paramType = "query")
	})
	public ServerResponse productSearch(HttpSession session,
	                                    String productName, Integer productId,
	                                    @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
		logger.info("后台商品列表查询，productName, {}, productId: {}, pageNum: {}, pageSize: {}", productName, productId, pageNum, pageSize);
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			return productService.searchProduct(productId, productName, pageNum, pageSize);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	@PostMapping("/upload.do")
	@ApiOperation(value = "后台商品图片上传")
	public ServerResponse upload(HttpSession session, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {
		logger.info("后台商品图片上传");
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要先登录!");
		}

		//检验是否是管理员
		ServerResponse response = userService.checkAdminRole(user);
		if (response.isSuccess()) {
			//增加处理逻辑
			//获取真实路径
			String path = request.getSession().getServletContext().getRealPath("/upload");

			//上传图片，得到上传后的图片文件名
			String targetFileName = fileService.upload(file, path);

			//拼接url
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

			Map fileMap = Maps.newHashMap();
			fileMap.put("uri", targetFileName);
			fileMap.put("url", url);
			return ServerResponse.createBySuccess(fileMap);
		}

		return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");
	}

	@PostMapping("/richtext_upload.do")
	@ApiOperation(value = "后台商品中的富文本图片上传")
	public Map richtextUpload(HttpSession session, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
		logger.info("后台商品中的富文本图片上传");
		Map resultMap = Maps.newHashMap();
		User user = (User) session.getAttribute(Constant.CURRENT_USER);
		if (user == null) {
			resultMap.put("status", false);
			resultMap.put("msg", "请登录管理员");
			return resultMap;
		}

		//检验是否是管理员
		ServerResponse serverResponse = userService.checkAdminRole(user);
		if (serverResponse.isSuccess()) {
			//富文本中对于返回值需要符合要求，此处使用simditor编辑器
			/**
			 * {
			 *  "status": true/false
			 *  "msg": "error message" #optional
			 *  "file_path": "[real_file_path]"
			 * }
			 */

			//增加处理逻辑
			//获取真实路径
			String path = request.getSession().getServletContext().getRealPath("/upload");

			//上传图片，得到上传后的图片文件名
			String targetFileName = fileService.upload(file, path);

			if (StringUtils.isBlank(targetFileName)) {
				resultMap.put("status", false);
				resultMap.put("msg", "上传失败");
				return resultMap;
			}

			//拼接url
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

			resultMap.put("status", true);
			resultMap.put("msg", "上传成功");
			resultMap.put("file_path", url);

			response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
			return resultMap;
		} else {
			resultMap.put("status", false);
			resultMap.put("msg", "无权限操作");
			return resultMap;
		}

	}
}

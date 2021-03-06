package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.common.ResponseCode;
import com.mmall.common.RoleEnum;
import com.mmall.domain.User;
import com.mmall.service.ICategoryService;
import com.mmall.util.SessionUtil;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
	
	@Autowired
	private ICategoryService categoryService;
	@Autowired
	private SessionUtil sessionUtil;

	private ServerResponse checkLoginAndRole(HttpServletRequest request ){
		User user = sessionUtil.checkLogin(request);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(),"请登录后操作");
		}else if(!Objects.equals(user.getRole() , RoleEnum.ADMIN.getCode())){
			return ServerResponse.Failure("没有权限");
		}
		return ServerResponse.Success();
	}

	@RequestMapping("add_category.do")
	@ResponseBody
	public ServerResponse addCategory(HttpServletRequest request , String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
		ServerResponse check = checkLoginAndRole(request);
		if (check.isSuccess()){
			return categoryService.addCategory(categoryName, parentId);
		}else{
			return check;
		}
	}

	@RequestMapping("set_category_name.do")
	@ResponseBody
	public ServerResponse setCategoryName(HttpServletRequest request , Integer categoryId, String categoryName) {
		ServerResponse check = checkLoginAndRole(request);
		if (check.isSuccess()){
			return categoryService.updateCategoryName(categoryId, categoryName);
		}else{
			return check;
		}
	}

	@RequestMapping("get_category.do")
	@ResponseBody
	public ServerResponse getChildrenParallelCategory(HttpServletRequest request , @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
		ServerResponse check = checkLoginAndRole(request);
		if (check.isSuccess()){
			return categoryService.getChildrenParallelCategory(categoryId);
		}else{
			return check;
		}
	}

	@RequestMapping("get_deep_category.do")
	@ResponseBody
	public ServerResponse getCategoryAndDeepChildrenCategory(HttpServletRequest request , @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
		ServerResponse check = checkLoginAndRole(request);
		if (check.isSuccess()){
			return categoryService.selectCategoryAndChildrenById(categoryId);
		}else{
			return check;
		}
	}
}

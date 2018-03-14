package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.RoleEnum;
import com.mmall.common.ServerResponse;
import com.mmall.domain.Product;
import com.mmall.domain.User;
import com.mmall.service.IProductService;
import com.mmall.util.FileUtil;
import com.mmall.util.PropertiesUtil;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

	@Autowired
	private IProductService productService;

	private ServerResponse checkLoginAndRole(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null){
			return ServerResponse.Failure(ResponseCode.NEED_LOGIN.getCode(),"请登录后操作");
		}else if(!Objects.equals(user.getRole() , RoleEnum.ADMIN.getCode())){
			return ServerResponse.Failure("没有权限");
		}
		return ServerResponse.Success();
	}

	@RequestMapping("save.do")
	@ResponseBody
	public ServerResponse productSave(HttpSession session, @Valid Product product , BindingResult bindingResult){
		if (bindingResult.hasErrors()){
			return ServerResponse.Failure(bindingResult.getFieldError().toString());
		}
		ServerResponse loginRet = checkLoginAndRole(session);
		if (!loginRet.isSuccess()){
			return loginRet;
		}else{
			return productService.saveOrUpdateProduct(product);
		}
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse productList(HttpSession session,
									  @RequestParam(value = "pageNum" , defaultValue = "1") int pageNum ,
									  @RequestParam(value = "pageSize" , defaultValue = "10") int pageSize){
		ServerResponse loginRet = checkLoginAndRole(session);
		if (!loginRet.isSuccess()){
			return loginRet;
		}else{
			return productService.getProductList(pageNum , pageSize);
		}
	}

	@RequestMapping("search.do")
	@ResponseBody
	public ServerResponse productSearch(HttpSession session,
									  @RequestParam("productName") String productName,
									  @RequestParam(value = "productId" , required = false) Integer productId,
									  @RequestParam(value = "pageNum" , defaultValue = "1") int pageNum ,
									  @RequestParam(value = "pageSize" , defaultValue = "10") int pageSize){
		ServerResponse loginRet = checkLoginAndRole(session);
		if (!loginRet.isSuccess()){
			return loginRet;
		}else{
			return productService.getProductList(pageNum , pageSize);
		}
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse productDetail(HttpSession session,
										@RequestParam(value = "productId" , required = true) Integer productId){
		ServerResponse loginRet = checkLoginAndRole(session);
		if (!loginRet.isSuccess()){
			return loginRet;
		}else{
			return productService.getProductDetail(productId);
		}
	}

	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServerResponse productSetSaleStatus(HttpSession session,
										@RequestParam(value = "productId" , required = true) Integer productId,
										@RequestParam(value = "status") Integer status){
		ServerResponse loginRet = checkLoginAndRole(session);
		if (!loginRet.isSuccess()){
			return loginRet;
		}else{
			return productService.setSaleStatus(productId , status);
		}
	}

	@RequestMapping("upload.do")
	@ResponseBody
	public ServerResponse productUpload(HttpSession session,
										@RequestParam("upload_file") MultipartFile file){
		ServerResponse loginRet = checkLoginAndRole(session);
		if (!loginRet.isSuccess()){
			return loginRet;
		}else{
			String path = session.getServletContext().getRealPath("upload");
			String uploadFileName = FileUtil.upload(file, path);
			if (StringUtils.isNotBlank(uploadFileName)){
				String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + uploadFileName;

				Map fileMap = Maps.newHashMap();
				fileMap.put("uri",uploadFileName);
				fileMap.put("url",url);
				return ServerResponse.Success(fileMap);
			}else{
				return ServerResponse.Failure("上传失败");
			}
		}
	}

	@RequestMapping("richtext_img_upload.do")
	@ResponseBody
	public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
		//富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
		Map resultMap = Maps.newHashMap();
		ServerResponse loginRet = checkLoginAndRole(session);
		if (!loginRet.isSuccess()) {
			resultMap.put("success",false);
			resultMap.put("msg",loginRet.getMsg());
			return resultMap;
		} else {
			String path = request.getSession().getServletContext().getRealPath("upload");
			String targetFileName = FileUtil.upload(file, path);
			if (StringUtils.isBlank(targetFileName)) {
				resultMap.put("success",false);
				resultMap.put("msg","上传失败");
				return resultMap;
			}
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
			resultMap.put("success",true);
			resultMap.put("msg","上传成功");
			resultMap.put("file_path",url);
			response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
			return resultMap;
		}
	}
}

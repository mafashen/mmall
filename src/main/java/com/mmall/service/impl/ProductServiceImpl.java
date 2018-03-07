package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.StatusEnum;
import com.mmall.dao.ProductMapper;
import com.mmall.domain.Category;
import com.mmall.domain.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import java.util.ArrayList;
import java.util.List;
import jodd.bean.BeanCopy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements IProductService{

	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private ICategoryService categoryService;


	@Override
	public ServerResponse saveOrUpdateProduct(Product product) {
		//处理主图
		if (product.getSubImages() != null && !product.getSubImages().isEmpty()){
			String[] images = product.getSubImages().split(",");
			if (images != null && StringUtils.isNotBlank(images[0]))
			product.setMainImage(images[0]);
		}
		if (product.getId() != null){
			int update = productMapper.updateByPrimaryKeySelective(product);
			if (update > 0){
				return ServerResponse.Success("更新商品成功");
			}
		}else {
			int insert = productMapper.insert(product);
			if (insert > 0){
				return ServerResponse.Success("新增商品成功");
			}
		}
		return ServerResponse.Failure("新增或更新商品失败");
	}

	@Override
	public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
		if (productId == null || status == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Product product = new Product();
		product.setId(productId);
		product.setStatus(status);
		int update = productMapper.updateByPrimaryKeySelective(product);
		if (update > 0){
			return ServerResponse.Success("商品上下架成功");
		}
		return ServerResponse.Failure("商品上下架失败");
	}

	@Override
	public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
		if(productId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product == null){
			return ServerResponse.Failure("商品不存在或者已经下架");
		}
		return ServerResponse.Success(convert2DetailVO(product));
	}

	@Override
	public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum , pageSize);
		List<Product> products = productMapper.selectProductList();
		List<ProductListVo> voList = new ArrayList<>();
		for (Product product : products) {
			voList.add(convert2ListVO(product));
		}
		PageInfo pageInfo = new PageInfo(products);
		pageInfo.setList(voList);
		return ServerResponse.Success(pageInfo);
	}

	@Override
	public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
		if (StringUtils.isBlank(productName) && productId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		PageHelper.startPage(pageNum , pageSize);
		List<Product> products =  productMapper.selectByNameOrId("%"+productName+"%" , productId);
		List<ProductListVo> voList = new ArrayList<>();
		for (Product product : products) {
			voList.add(convert2ListVO(product));
		}
		PageInfo pageInfo = new PageInfo(products);
		pageInfo.setList(voList);
		return ServerResponse.Success(pageInfo);
	}

	@Override
	public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
		if(productId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if (product == null || product.getStatus() != StatusEnum.ONLINE.getCode()){
			return ServerResponse.Failure("商品不存在或者已经下架");
		}
		return ServerResponse.Success(convert2DetailVO(product));
	}

	@Override
	public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
		if (StringUtils.isBlank(keyword) && categoryId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		List<Integer> cats = new ArrayList<>();
		if (categoryId != null){
			ServerResponse<Category> catRet = categoryService.selectCategoryById(categoryId);
			//没有关键字查询，但是类目不存在，返回null结果
			if ((!catRet.isSuccess() || catRet.getData() == null ) && StringUtils.isBlank(keyword)){
				PageHelper.startPage(pageNum,pageSize);
				List<ProductListVo> productListVoList = Lists.newArrayList();
				PageInfo pageInfo = new PageInfo(productListVoList);
				return ServerResponse.Success(pageInfo);
			}
			ServerResponse<List<Integer>> childRet = categoryService.selectCategoryAndChildrenById(catRet.getData().getId());
			if (childRet.isSuccess()){
				cats = childRet.getData();
			}
		}
		if (StringUtils.isNotBlank(keyword)){
			keyword = "%" + keyword + "%";
		}else{
			keyword = null;
		}
		PageHelper.startPage(pageNum , pageSize);
		//排序处理
		if (StringUtils.isNotBlank(orderBy)){
			if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
				String[] orderByArray = orderBy.split("_");
				PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
			}
		}
		List<Product> products = productMapper.selectByNameAndCatId(keyword , cats);
		List<ProductListVo> productListVoList = Lists.newArrayList();
		for(Product product : products){
			ProductListVo productListVo = convert2ListVO(product);
			productListVoList.add(productListVo);
		}

		PageInfo pageInfo = new PageInfo(products);
		pageInfo.setList(productListVoList);
		return ServerResponse.Success(pageInfo);
	}

	private ProductDetailVo convert2DetailVO(Product domain){
		ProductDetailVo vo = new ProductDetailVo();
		new BeanCopy(domain , vo).copy();
		if (vo.getStatus() != null){
			vo.setStatusStr(StatusEnum.idOf(vo.getStatus()).getValue());
		}
		//设置类目信息
		ServerResponse<Category> ret = categoryService.selectCategoryById(domain.getCategoryId());
		if (ret.isSuccess()){
			vo.setCategoryStr(ret.getData().getName());
			vo.setParentCategoryId(ret.getData().getParentId());
			ServerResponse<Category> parent = categoryService.selectCategoryById(domain.getCategoryId());
			if (parent.isSuccess()){
				vo.setParentCategoryStr(parent.getData().getName());
			}
		}
		//设置图片ftp服务器域名前缀
		vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
		return vo;
	}

	private ProductListVo convert2ListVO(Product product){
		ProductListVo vo = new ProductListVo();
		new BeanCopy(product , vo).copy();
		if (vo.getStatus() != null){
			vo.setStatusStr(StatusEnum.idOf(vo.getStatus()).getValue());
		}
		//设置类目信息
		ServerResponse<Category> ret = categoryService.selectCategoryById(product.getCategoryId());
		if (ret.isSuccess()){
			vo.setCategoryStr(ret.getData().getName());
		}
		//设置图片ftp服务器域名前缀
		vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
		return vo;
	}
}

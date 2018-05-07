package com.mmall.service.impl;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.domain.Category;
import com.mmall.service.ICategoryService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl implements ICategoryService {

	@Autowired
	private CategoryMapper categoryMapper;


	@Override
	public ServerResponse addCategory(String categoryName, Integer parentId) {
		if (StringUtils.isBlank(categoryName) || parentId == null || parentId < 0){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Category category = new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);
		int insert = categoryMapper.insert(category);
		if (insert > 0){
			return ServerResponse.Success("增加类目成功");
		}
		return ServerResponse.Failure("增加类目失败");
	}

	@Override
	public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
		if (categoryId == null || StringUtils.isBlank(categoryName)){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Category cat = new Category();
		cat.setId(categoryId);
		cat.setName(categoryName);
		int update = categoryMapper.updateByPrimaryKeySelective(cat);
		if (update > 0){
			return ServerResponse.Success("更新类目成功");
		}
		return ServerResponse.Failure("更新类目失败");
	}

	@Override
	public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
		if (categoryId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		List<Category> categories = categoryMapper.getChildren(categoryId);
		if (categories == null || categories.isEmpty()){
			return ServerResponse.Failure("当前类目下没有子类目");
		}
		return ServerResponse.Success(categories);
	}

	@Override
	public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
		if (categoryId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Set<Integer> catIds = new HashSet<>();
		findChildCat(categoryId, catIds);
		List<Integer> catIdList = new ArrayList<>();
		for (Integer catId : catIds) {
			catIdList.add(catId);
		}
		return ServerResponse.Success(catIdList);
	}

	private void findChildCat(Integer categoryId, Set<Integer> catIds) {
		//先找出自身
		Category selfCat = categoryMapper.selectByPrimaryKey(categoryId);
		if (selfCat != null){
			catIds.add(categoryId);
		}
		//递归找出子节点
		List<Category> children = categoryMapper.getChildren(categoryId);
		if (children != null && !children.isEmpty()){
			for (Category child : children) {
				findChildCat(child.getId() , catIds);
			}
		}
	}

	public ServerResponse<Category> selectCategoryById(Integer categoryId){
		if (categoryId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		if (category != null){
			return ServerResponse.Success(category);
		}
		return ServerResponse.Failure("类目不存在");
	}

	@Override
	public ServerResponse<Category> getFullCategory(Integer catId){
		Category category = new Category();
		ServerResponse<List<Category>> ret = getChildrenParallelCategory(catId);
		if (ret.isSuccess() && ret.getData() != null){
			List<Category> data = ret.getData();
			for (Category cat : data) {
				findChildCat(cat);
			}
			category.setChildren(data);
		}
		return ServerResponse.Success(category);
	}

	private void findChildCat(Category category) {

		//递归找出子节点
		List<Category> children = categoryMapper.getChildren(category.getId());
		if (children != null && !children.isEmpty()){
			category.setChildren(children);
			for (Category child : children) {
				findChildCat(child);
			}
		}
	}

	@Override
	public ServerResponse<List<Category>> getCategoryTree(){
		List<Category> firstCats = categoryMapper.getChildren(0);
		if (CollectionUtils.isNotEmpty(firstCats)){
			for (Category firstCat : firstCats) {
				List<Category> children = categoryMapper.getChildren(firstCat.getId());
				firstCat.setChildren(children);
			}
		}
		return ServerResponse.Success(firstCats);
	}
}

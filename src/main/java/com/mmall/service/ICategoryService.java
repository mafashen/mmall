package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.domain.Category;
import java.util.List;

public interface ICategoryService {
	
	ServerResponse addCategory(String categoryName, Integer parentId);

	ServerResponse updateCategoryName(Integer categoryId, String categoryName);

	ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

	ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);

	ServerResponse<Category> selectCategoryById(Integer categoryId);

	ServerResponse<Category> getFullCategory(Integer catId);

	ServerResponse<List<Category>> getCategoryTree();
}

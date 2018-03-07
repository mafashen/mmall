package com.mmall.dao;

import com.mmall.domain.Product;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectProductList();

    List<Product> selectByNameOrId(@Param("name") String productName, @Param("id") Integer productId);

    List<Product> selectByNameAndCatId(@Param("name") String keyword, @Param("catIds") List<Integer> cats);
}
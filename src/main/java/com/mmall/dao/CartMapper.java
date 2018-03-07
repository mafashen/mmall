package com.mmall.dao;

import com.mmall.domain.Cart;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdProductId(@Param("userId") Integer userId , @Param("productId") Integer ProductId );

    int setChecked(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);

    int countByUserId(@Param("userId") Integer userId);

    List<Cart> selectByUserId(@Param("userId") Integer userId);

    List<Cart> selectByUserIdAndChecked(@Param("userId") Integer userId);

    int deleteByUserIdProductId(@Param("userId") Integer userId , @Param("productIds") List<Integer> productIds);

}
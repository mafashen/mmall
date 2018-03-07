package com.mmall.dao;

import com.mmall.domain.Shipping;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ShippingMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(@Param("userId")Integer userId , @Param("id") Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUserIdAndKey(@Param("userId") Integer userId , @Param("id") Integer id);

    List<Shipping> selectByUserId(@Param("userId") Integer userId);

}
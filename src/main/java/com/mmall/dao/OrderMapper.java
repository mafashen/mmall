package com.mmall.dao;

import com.mmall.domain.Order;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.aspectj.weaver.ast.Or;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId , @Param("orderNo") Long orderNo);

    Order selectByOrderNo(Long orderNo);

	List<Order> selectByUserId(Integer userId);

    List<Order> selectAllOrders();
}
package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.domain.Shipping;
import com.mmall.service.IShippingService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ShippingServiceImpl implements IShippingService {

	@Autowired
	private ShippingMapper shippingMapper;


	@Override
	public ServerResponse add(Integer userId, Shipping shipping) {
		if (userId == null || shipping == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		shipping.setUserId(userId);
		int insert = shippingMapper.insert(shipping);
		if (insert > 0){
			Map result = new HashMap();
			result.put("shippingId" , shipping.getId());
			return ServerResponse.Success("新增收货地址成功" , result);
		}
		return ServerResponse.Failure("新增收货地址失败");
	}

	@Override
	public ServerResponse<String> del(Integer userId, Integer shippingId) {
		if (userId == null || shippingId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		int del = shippingMapper.deleteByUserIdAndKey(userId, shippingId);
		if (del > 0){
			return ServerResponse.Success("删除收货地址成功");
		}
		return ServerResponse.Failure("删除收货地址失败");
	}

	@Override
	public ServerResponse update(Integer userId, Shipping shipping) {
		if (userId == null || shipping == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		shipping.setUserId(userId);
		int update = shippingMapper.updateByPrimaryKeySelective(shipping);
		if (update > 0){
			return ServerResponse.Success("更新收货地址成功");
		}
		return ServerResponse.Failure("更新收货地址失败");
	}

	@Override
	public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
		if (userId == null || shippingId == null){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		Shipping shipping = shippingMapper.selectByPrimaryKey(userId, shippingId);
		if (shipping != null){
			return ServerResponse.Success(shipping);
		}
		return ServerResponse.Failure("无法查询到该地址");
	}

	@Override
	public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
		if (userId == null ){
			return ServerResponse.Failure(ResponseCode.PARAM_ERROR.getCode() , ResponseCode.PARAM_ERROR.getMsg());
		}
		PageHelper.startPage(pageNum, pageSize);
		List<Shipping> shippings = shippingMapper.selectByUserId(userId);
		PageInfo pageInfo = new PageInfo(shippings);
		return ServerResponse.Success(pageInfo);
	}
}

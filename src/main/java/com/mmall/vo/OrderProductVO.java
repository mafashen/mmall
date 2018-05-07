package com.mmall.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderProductVO implements Serializable{

	private List<OrderItemVO> orderItemVoList;
	private BigDecimal productTotalPrice;
	private String imageHost;
}

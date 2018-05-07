package com.mmall.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemVO implements Serializable{

	private Long orderNo;

	private Integer productId;

	private String productName;
	private String productImage;

	private BigDecimal currentUnitPrice;

	private Integer quantity;

	private BigDecimal totalPrice;

	private String createTime;
}

package com.mmall.vo;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemVO {

	private Long orderNo;

	private Integer productId;

	private String productName;
	private String productImage;

	private BigDecimal currentUnitPrice;

	private Integer quantity;

	private BigDecimal totalPrice;

	private String createTime;
}

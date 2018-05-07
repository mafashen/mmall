package com.mmall.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartVO implements Serializable{

	private List<CartProductVO> cartProductVoList;
	private BigDecimal cartTotalPrice;
	private Boolean allChecked;//是否已经都勾选
	private String imageHost;
}

package com.mmall.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductListVo implements Serializable{

	private Integer categoryId;
	private String categoryStr;

	private String name;
	private String subtitle;
	private String mainImage;
	private BigDecimal price;

	private Integer status;
	private String statusStr;

	private String imageHost;

}

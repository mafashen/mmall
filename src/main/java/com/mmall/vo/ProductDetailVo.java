package com.mmall.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailVo implements Serializable{

	private Integer categoryId;

	private String categoryStr;

	private Integer parentCategoryId;

	private String parentCategoryStr;

	private String name;

	private String subtitle;

	private String mainImage;

	private String subImages;

	private String imageHost;

	private String detail;

	private BigDecimal price;

	private Integer stock = 0;

	private Integer status = 1;

	private String statusStr ;

	private Date createTime;

	private Date updateTime;

}

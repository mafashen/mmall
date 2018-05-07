package com.mmall.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShippingVO implements Serializable{

	private String receiverName;

	private String receiverPhone;

	private String receiverMobile;

	private String receiverProvince;

	private String receiverCity;

	private String receiverDistrict;

	private String receiverAddress;

	private String receiverZip;
}

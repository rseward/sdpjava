package com.bestbuy.sdp.order.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RetrieveOrderVO implements Serializable{

	private String SdpOrderId;
	private String orderStatus;
	private String keyCode;
	private String confirmationCode;
	private String sdpId;
	private String sdpCustomerId;
	private String businessKeyType;
	private String businessKey;
	public String getSdpOrderId() {
		return SdpOrderId;
	}
	public void setSdpOrderId(String sdpOrderId) {
		SdpOrderId = sdpOrderId;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
	public String getConfirmationCode() {
		return confirmationCode;
	}
	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}
	public String getSdpId() {
		return sdpId;
	}
	public void setSdpId(String sdpId) {
		this.sdpId = sdpId;
	}
	public String getSdpCustomerId() {
		return sdpCustomerId;
	}
	public void setSdpCustomerId(String sdpCustomerId) {
		this.sdpCustomerId = sdpCustomerId;
	}
	public String getBusinessKeyType() {
		return businessKeyType;
	}
	public void setBusinessKeyType(String businessKeyType) {
		this.businessKeyType = businessKeyType;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	
}

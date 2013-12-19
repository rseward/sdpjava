package com.bestbuy.sdp.provisioning;

import java.sql.Clob;

public class VendorProvisioningRequest {
	private int rowNum;
	private String vendorID;
	private String vendorKey;
	private int csmID;
	private String sdpID;
	private String requestType;
	private String status;
	private int retryCount;
	private Clob msgXML;

	public int getRowNum() { return this.rowNum; }
	public void setRowNum(int rowNum) { this.rowNum = rowNum; }
	
	public String getVendorID() { return this.vendorID; }
	public void setVendorID(String vendorID) { this.vendorID = vendorID; }
	
	public String getVendorKey() { return this.vendorKey; }
	public void setVendorKey(String vendorKey) { this.vendorKey = vendorKey; }
	
	public int getCsmID() { return this.csmID; }
	public void setCsmID(int csmID) { this.csmID = csmID; }
	
	public String getSdpID() { return this.sdpID; }
	public void setSdpID(String sdpID) { this.sdpID = sdpID; }
	
	public String getRequestType() { return this.requestType; }
	public void setRequestType(String requestType) { this.requestType = requestType; }
	
	public String getStatus() { return this.status; }
	public void setStatus(String status) { this.status = status; }
	
	public int getRetryCount() { return this.retryCount; }
	public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
	
	public Clob getMsgXML() { return this.msgXML; }
	public void setMsgXML(Clob msgXML) { this.msgXML = msgXML; }
	
	protected VendorProvisioningRequest() { }

	public String toString() {
		StringBuffer sb = new StringBuffer("VPS Row [SDP ID = ");
		sb.append(this.getSdpID());
		sb.append("] [Vendor ID = ");
		sb.append(this.getVendorID());
		sb.append("] [Vendor Key = ");
		sb.append(this.getVendorKey());
		sb.append("]");
		return sb.toString();
	}
}

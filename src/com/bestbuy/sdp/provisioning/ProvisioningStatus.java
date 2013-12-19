package com.bestbuy.sdp.provisioning;

public class ProvisioningStatus {

	public static final ProvisioningStatus ACTIVATION_SUCCESS = new ProvisioningStatus("Activation_Success");
	public static final ProvisioningStatus ACTIVATION_FAILURE = new ProvisioningStatus("Activation_Failure");
	public static final ProvisioningStatus CANCELLATION_SUCCESS = new ProvisioningStatus("Cancellation_Success");
	public static final ProvisioningStatus CANCELLATION_FAILURE = new ProvisioningStatus("Cancellation_Failure");
	public static final ProvisioningStatus RENEWAL_SUCCESS = new ProvisioningStatus("Renew_Success");
	public static final ProvisioningStatus RENEWAL_FAILURE = new ProvisioningStatus("Renew_Failure");
	public static final ProvisioningStatus UPDATE_SUCCESS = new ProvisioningStatus("Update_Success");
	public static final ProvisioningStatus UPDATE_FAILURE = new ProvisioningStatus("Update_Failure");
	
	
	private String name;
	
	private ProvisioningStatus(String name){
		this.name = name;
	}
	
	public String getName(){ return this.name; }
	
	public static final ProvisioningStatus valueOf(String opName, boolean success){
		VendorOperation op = VendorOperation.get(opName);
		
		if(op == VendorOperation.ACTIVATE){
			return success ? ACTIVATION_SUCCESS : ACTIVATION_FAILURE;
		} else if (op == VendorOperation.CANCEL){
			return success ? CANCELLATION_SUCCESS : CANCELLATION_FAILURE;
		} else if (op == VendorOperation.RENEW){
			return success ? RENEWAL_SUCCESS : RENEWAL_FAILURE;					
		} 
		else if (op == VendorOperation.UPDATESTATUS){
			return success ? UPDATE_SUCCESS : UPDATE_FAILURE;					
		} 
		else {
			throw new IllegalArgumentException("Unable to match operation name=["+opName+"] to valid operation.");
		}
	}
	
	public static final String toString(String opName, boolean success){
		return valueOf(opName, success).toString();
	}
	
	public String toString(){
		return this.getName();
	}
}

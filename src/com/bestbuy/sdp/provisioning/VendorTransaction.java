package com.bestbuy.sdp.provisioning;

public class VendorTransaction {
	public static String getTransactionID(String vendorID, String operationType) {
		String transactionID = vendorID + operationType
				+ java.lang.System.currentTimeMillis();
		return transactionID;
	}
}

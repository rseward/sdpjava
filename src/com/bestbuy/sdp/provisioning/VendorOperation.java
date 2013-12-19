package com.bestbuy.sdp.provisioning;

public class VendorOperation {
	public static final VendorOperation ACTIVATE;
	public static final VendorOperation CANCEL;
	public static final VendorOperation RENEW;
	public static final VendorOperation UPDATESTATUS;
	public static final VendorOperation SEND_EMAIL;
	public static final VendorOperation REACTIVATE;
	public static final VendorOperation CA_INTEGRATION;
	public static final VendorOperation OPENC_ACTIVATE;
	public static final VendorOperation OPENC_CANCEL;
	public static final VendorOperation OPENC_UPDATE;
	public static final VendorOperation GENERATEUNIQUEID;
	

	private String name;
	private String dbName;

	static {
		ACTIVATE = new VendorOperation("activate", "ACT");
		CANCEL = new VendorOperation("cancel", "CNL");
		RENEW = new VendorOperation("renew", "RNW");
		UPDATESTATUS = new VendorOperation("updateStatus", "UPD");
		SEND_EMAIL = new VendorOperation("sendEmail", "SEM");
		REACTIVATE = new VendorOperation("reactivate", "RACT");
		CA_INTEGRATION = new VendorOperation("modifyCA", "CA");
		OPENC_ACTIVATE = new VendorOperation("opencActivate", "OACT");
		OPENC_CANCEL = new VendorOperation("opencCancel", "OCNL");
		OPENC_UPDATE = new VendorOperation("opencUpdate", "OUPD");
		GENERATEUNIQUEID = new VendorOperation("generateUniqueId", "GUID");
	}

	// Suppress default Constructor
	private VendorOperation() {
	}

	private VendorOperation(String name, String dbName) {
		this.name = name;
		this.dbName = dbName;
	}

	public String getName() {
		return this.name;
	}

	public String getShortName() {
		return this.dbName;
	}

	public static final VendorOperation get(String s) {
		if (s == null || (s.length() == 0))
			throw new IllegalArgumentException(
					"Invalid VendorOperation: input was null or zero-length");

		s = s.toUpperCase();

		if("Activate".equalsIgnoreCase(s)) {
			return ACTIVATE;
		} else if("cancel".equalsIgnoreCase(s)) {
			return CANCEL;
		} else if("renew".equalsIgnoreCase(s)) {
			return RENEW;
		} else if("updateStatus".equalsIgnoreCase(s)) {
			return UPDATESTATUS;
		} else if("sendEmail".equalsIgnoreCase(s)) {
			return SEND_EMAIL;
		} else if("reactivate".equalsIgnoreCase(s)) {
			return REACTIVATE;
		} else if("modifyCA".equalsIgnoreCase(s)) {
			return CA_INTEGRATION;
		} else if("opencActivate".equalsIgnoreCase(s)) {
			return OPENC_ACTIVATE;
		} else if("opencCancel".equalsIgnoreCase(s)) {
			return OPENC_CANCEL;
		} else if("opencUpdate".equalsIgnoreCase(s)) {
			return OPENC_UPDATE;
		}else if("generateUniqueId".equalsIgnoreCase(s)) {
			return GENERATEUNIQUEID;
		}else
			throw new IllegalArgumentException(
					"No VendorOperation match found for input: " + s);
	}
}

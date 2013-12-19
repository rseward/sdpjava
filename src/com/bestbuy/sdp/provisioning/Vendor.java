package com.bestbuy.sdp.provisioning;

public class Vendor {
	private String id;

	public Vendor(String id) {
		this.id = id;
	}

	public String getID() {
		return this.id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String toString() {
		return this.id;
	}
}

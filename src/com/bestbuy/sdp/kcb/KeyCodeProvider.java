package com.bestbuy.sdp.kcb;

import java.sql.SQLException;

import com.accenture.common.ex.ApplicationException;

public interface KeyCodeProvider 
{
	/**
	 * Retrieves (or generates) a KeyCode and associates it with the input
	 * data in the KeyCode Bank.
	 * 
	 * @param vendorID SDP-assigned vendor identification code
	 * @param SKU Best Buy-assigned product SKU (or equivalent) code
	 * @param orderID unique (per vendor) order identification code
	 * @return
	 * @throws ApplicationException 
	 */
	public KeyCode reserve(String vendorID, String SKU, String orderID) throws ApplicationException,SQLException;

	/**
	 * Retrieves (or generates) a KeyCode and associates it with the input
	 * data in the KeyCode Bank.
	 * 
	 * @param vendorID SDP-assigned vendor identification code
	 * @param SKU Best Buy-assigned product SKU (or equivalent) code
	 * @param orderID unique (per vendor) order identification code
	 * @return
	 * @throws ApplicationException 
	 */
	public KeyCode reserveByMasterItemId(String vendorID, String masterItemId, String orderID, String status) throws ApplicationException,SQLException;

	/**
	 * Updates the input KeyCode in the KeyCode Bank to match its specified
	 * values.  The update(s) will be based on the values of the VendorID and
	 * OrderID of the KeyCode instance.
	 * <p>
	 * For instance, if KeyCode.getVendorID()=VND, KeyCode.getOrderID() = 100, 
	 * and KeyCode,value() = 444, the KeyCode Bank will update the existing Code with 
	 * vendorID=VND and orderID=100 such that the KeyCode value becomes 444.</p> 
	 * @param keyCode
	 * @return
	 * @throws ApplicationException 
	 */
	public boolean update(KeyCode keyCode) throws ApplicationException,SQLException;

	/**
	 * Frees a previously reserved KeyCode back into the KeyCode Bank pool. 
	 * @param keyCode
	 * @throws ApplicationException 
	 */
	public void release(KeyCode keyCode) throws ApplicationException,SQLException;
}

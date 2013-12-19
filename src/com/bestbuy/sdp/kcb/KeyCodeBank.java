package com.bestbuy.sdp.kcb;

import java.sql.*;
import java.util.*;

import oracle.jdbc.driver.OracleTypes;

import com.accenture.common.ex.ApplicationException;

public class KeyCodeBank
{	
	private static final String NAME = "NAME";
	private static final String VALUE = "VALUE";
	private static final String VENDORID = "VENDORID";
	private static final String KEYCODEACTUAL = "KEYCODEACTUAL";
	private static final String ORDERID = "ORDERID";
	private static final String SKU = "SKU";
	private static final String KEYCODE = "KEYCODE";
	private static final String STATUS = "STATUS";
	

	private Connection conn;
	
	public KeyCodeBank(Connection conn){
		this.conn = conn;		
	}
	
	public void reset(){

	}
	
	public void close() throws SQLException{
		this.conn.close();
	}
	
	public KeyCode reserve(KeyCode keyCode) throws ApplicationException,SQLException
	{
		CallableStatement stmt = null;
		ResultSet rs = null;
		try{
			stmt = this.conn.prepareCall("{ call keyCodeBank.reserve(?,?,?,?,?)}");
			
			stmt.setString(1,keyCode.getVendorID());
			stmt.setString(2,keyCode.getSKU());
			stmt.setString(3,keyCode.getOrderID());
			stmt.registerOutParameter(4, OracleTypes.VARCHAR);
			stmt.registerOutParameter(5, OracleTypes.CURSOR);
			
			stmt.executeQuery();
			// Extract the KeyCode value
			keyCode.setValue(stmt.getString(4));
			// Extract the KeyCode attributes (if any)
			rs = (ResultSet)stmt.getObject(5);
			if(rs != null){
				while(rs.next())
					keyCode.setProperty(rs.getString(1),rs.getString(2));
			}
		}
		//Commented by Krapa: Adding sqlexception inside service result in KeyCodeProviderService class
//		catch (SQLException sqlex){
//			throw new ApplicationException("Encountered SQLException during execution of KCB reserve procedure.  Tried to reserve with: "+keyCode.toString(),1,sqlex);
//		} 
		finally {
			// Clean-up
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			} catch (Exception ex) {/*ignore*/ }
		}
		return keyCode;
	}
	

	public KeyCode reserveByMasterItemId(KeyCode keyCode) throws ApplicationException,SQLException
	{
		CallableStatement stmt = null;
		ResultSet rs = null;
		try{
			stmt = this.conn.prepareCall("{ call keyCodeBank.reserveByMasterItemId(?,?,?,?,?,?)}");
			
			stmt.setString(1,keyCode.getVendorID());
			stmt.setString(2,keyCode.getMasterItemId());
			stmt.setString(3,keyCode.getOrderID());
			stmt.setString(4,keyCode.getStatus().toString());
			stmt.registerOutParameter(5, OracleTypes.VARCHAR);
			stmt.registerOutParameter(6, OracleTypes.CURSOR);
			
			stmt.executeQuery();
			// Extract the KeyCode value
			keyCode.setValue(stmt.getString(5));
			// Extract the KeyCode attributes (if any)
			rs = (ResultSet)stmt.getObject(6);
			if(rs != null){
				while(rs.next())
					keyCode.setProperty(rs.getString(1),rs.getString(2));
			}
		}
		//Commented by Krapa: Adding sqlexception inside service result in KeyCodeProviderService class
//		catch (SQLException sqlex){
//			throw new ApplicationException("Encountered SQLException during execution of KCB reserve procedure.  Tried to reserve with: "+keyCode.toString(),1,sqlex);
//		} 
		finally {
			// Clean-up
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			} catch (Exception ex) {/*ignore*/ }
		}
		return keyCode;
	}
	
	public boolean update(KeyCode keyCode) throws ApplicationException,SQLException
	{
		if(keyCode == null)
			throw new IllegalArgumentException("Invalid KeyCode: input was null");
		if(keyCode.getVendorID() == null || (keyCode.getVendorID().length() == 0))
			throw new IllegalArgumentException("Invalid vendorID: input was null or zero-length");
		if(keyCode.getKeyCodeActual() == null || (keyCode.getKeyCodeActual().length() == 0))
			throw new IllegalArgumentException("Invalid keyCodeActual: input was null or zero-length");
		if(keyCode.getOrderID() == null || (keyCode.getOrderID().length() == 0))
			throw new IllegalArgumentException("Invalid orderID: input was null or zero-length");
		if(keyCode.getStatus() == null || (keyCode.getStatus().toString().length() == 0))
			throw new IllegalArgumentException("Invalid status: input was null or zero-length");
						
		CallableStatement proc = null;
		int statusFlag = 0;
		try
		{
			proc = this.conn.prepareCall("{call keyCodeBank.markKeyCodeActivated(?,?,?,?,?) }");
			proc.setString(1,keyCode.getVendorID());
			proc.setString(2,keyCode.getOrderID());
			proc.setString(3,keyCode.getKeyCodeActual());			
			proc.setString(4,keyCode.getStatus().toString());
			proc.registerOutParameter(5, OracleTypes.INTEGER);
			proc.executeQuery();
			statusFlag = proc.getInt(5);
		}
		//Commented by Krapa: Adding sqlexception inside service result in KeyCodeProviderService class
//		catch (SQLException sqlex) 
//		{
//			throw new ApplicationException("Caught SQLException during execution of KCB markKeyCodeActivated procedure :"
//					+keyCode.toString() +" Status :"+ keyCode.getStatus().toString(),1,sqlex);
//		} 
		finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) { }
		}
		// Status is '1' if the value did NOT change
		if(statusFlag == 1)
			return false;
		else
			return true;
	}
	
	public void release(KeyCode keyCode) throws ApplicationException,SQLException
	{
		if(keyCode == null)
			throw new IllegalArgumentException("Invalid KeyCode: input was null");
		
		CallableStatement proc = null;
		try {
			// Check for Order ID - if present, release accordingly
			if(keyCode.getOrderID() != null && keyCode.getOrderID().length() > 0){
				proc = this.conn.prepareCall("{call keyCodeBank.releaseByOrderID(?) }");
				proc.setString(1,keyCode.getOrderID());
			// Otherwise, check for the KeyCode value and release accordingly
			}else if(keyCode.getValue() != null && keyCode.getValue().length() > 0){
				proc = this.conn.prepareCall("{call keyCodeBank.releaseByKeyCode(?) }");
				proc.setString(1,keyCode.getValue());
			}else{
				throw new IllegalArgumentException("Invalid KeyCode: either order ID or keyCode value is required, but both were null or zero-length");
			}
			proc.execute();
		}
		//Commented by Krapa: Adding sqlexception error code inside service result in KeyCodeProviderService class
//		catch (SQLException sqlex){
//			throw new ApplicationException("Caught SQLException during execution of KCB release procedure",1,sqlex);
//		} 
		finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) { /*ignore*/ }
		}
	}
	
	public KeyCode get(String vendorID, String keyCode) throws ApplicationException
	{
		if (vendorID == null || (vendorID.length() == 0))
			throw new IllegalArgumentException("Invalid vendorID: input was null or zero-length");
		if(keyCode == null || (keyCode.length() == 0))
			throw new IllegalArgumentException("Invalid keyCode: input was null or zero-length");
		
		CallableStatement proc = null;
		KeyCode keyCodeObj = new KeyCode();
		try {
			proc = this.conn.prepareCall("{call keyCodeBank.getKeyCode(?,?,?) }");
			proc.setString(1,vendorID);
			proc.setString(2,keyCode);
			proc.registerOutParameter(3, OracleTypes.CURSOR);
			proc.execute();
			ResultSet rs = (ResultSet) proc.getObject(3);
			if ( rs == null)
				return keyCodeObj;
			else{
				while(rs.next()){
					keyCodeObj.setValue(rs.getString(KEYCODE));
					keyCodeObj.setVendorID(rs.getString(VENDORID));				
					keyCodeObj.setKeyCodeActual(rs.getString(KEYCODEACTUAL));
					keyCodeObj.setOrderID(rs.getString(ORDERID));
					keyCodeObj.setSKU(rs.getString(SKU));
					keyCodeObj.setStatus(KeyCode.Status.getInstance(rs.getString(STATUS)));
					// TODO: This won't work properly if there is more than one property (or zero)
					keyCodeObj.setProperty(rs.getString(NAME), rs.getString(VALUE));
				}
			}
		}catch (SQLException sqlex) {
			throw new ApplicationException("Caught SQLException during execution of KCB getKeyCode procedure",1,sqlex);
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) { /*ignore*/ }
		}
		return keyCodeObj;
	}
	
	public KeyCode getByOrderID(String orderID) 
		throws ApplicationException
	{
		if (orderID == null || orderID.length() == 0)
			throw new IllegalArgumentException("Invalid orderID: input was null or zero-length");
		
		CallableStatement proc = null;
		KeyCode keyCodeObj = new KeyCode();
		try{
			proc = this.conn.prepareCall("{call keyCodeBank.getKeyCodeByOrderID(?,?) }");
			proc.setString(1,orderID);
			proc.registerOutParameter(2, OracleTypes.CURSOR);
			proc.executeQuery();
			ResultSet rs = (ResultSet) proc.getObject(2);
			
			if ( rs == null)
				return keyCodeObj;
			while(rs.next())
			{
				keyCodeObj.setValue(rs.getString(KEYCODE));	
				keyCodeObj.setKeyCodeActual(rs.getString(KEYCODEACTUAL));
				keyCodeObj.setOrderID(rs.getString(ORDERID));
				keyCodeObj.setSKU(rs.getString(SKU));
				keyCodeObj.setStatus(KeyCode.Status.getInstance(rs.getString(STATUS)));
				// TODO: This is won't work properly if there is more than one property (or zero)
				keyCodeObj.setProperty(rs.getString(NAME), rs.getString(VALUE));
			}
		}
		catch (SQLException sqlex) 
		{
			throw new ApplicationException("Caught SQLException during execution of KCB getKeyCodeByOrderID procedure",1,sqlex);			
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) {/*ignore*/}
		}
		return keyCodeObj;
	}
	
	public void load(KeyCode keyCode) throws SQLException, ApplicationException
	{
		if(keyCode == null)
			throw new IllegalArgumentException("Invalid KeyCode: input was null");
		
		Collection<KeyCode> keyCodes = new HashSet<KeyCode>(1);
		keyCodes.add(keyCode);
		
		if(load(keyCodes).size() > 0)
			throw new ApplicationException("Unable to load keyCode - SQLException occurred",1);
	}
	
	public Collection<KeyCode> load(Collection<KeyCode> keysToLoad)	throws SQLException
	{
		if(keysToLoad == null || keysToLoad.size() == 0)
			return new HashSet<KeyCode>(0);
		
		PreparedStatement s;
		ResultSet rs;
		/* Acquire the set of existing vendor IDs from the database */
		Set<String> vendorSet = null;
		s  = this.conn.prepareStatement("SELECT VENDORID FROM KCB_VENDOR");
		rs = s.executeQuery();

		vendorSet = new HashSet<String>(0);
		// Loop through the vendors, adding each to the vendor set		
		while(rs != null && rs.next())
			vendorSet.add(rs.getString(1));
		s.close();
		rs.close();
		
		/* Now, acquire the set of product IDs and their associated SKUs from the database */
		Map<String, Integer> productMap = this.getProductMap();
		
		Collection<KeyCode> failedKeyCodes = new ArrayList<KeyCode>(0);
		Set<Integer> usedProductIDs = new HashSet<Integer>(0);
		
		KeyCode keyCode = null;
		int productID = 0;
		/* Loop through the KeyCode collection inserting the KeyCodes */
		CallableStatement proc = conn.prepareCall("{call keyCodeBank.load(?,?,?,?) }");
		Iterator<KeyCode> keyIter = keysToLoad.iterator();
		while(keyIter.hasNext()){
			keyCode = keyIter.next();
			// If the vendor doesn't exist, add it to the failures and skip it
			if(!vendorSet.contains(keyCode.getVendorID())){
				failedKeyCodes.add(keyCode);
				//throw new IllegalArgumentException("Unable to perform load - found unrecognized vendor '"+keyCode.getVendorID()+"'");
			}
			// If the product doesn't exist, add it to the failures and skip it
			if(!productMap.containsKey(keyCode.getSKU())){
				failedKeyCodes.add(keyCode);
				//throw new IllegalArgumentException("Unable to perform load - found unrecognized SKU '"+keyCode.getSKU()+"'");
			}else{
				productID = productMap.get(keyCode.getSKU());
				// Mark the productID as loaded (used to recalculate base load sizes later)
				usedProductIDs.add(Integer.valueOf(productID));
				conn.commit();
				
				// Prepare and call the load procedure
				proc.setInt(1, productID);
				proc.setString(2, keyCode.getValue());
				proc.setString(3, keyCode.getSerialNumber());
				proc.registerOutParameter(4, OracleTypes.NUMBER);
				try{
					proc.execute();
				}catch(SQLException sqlex){
					// Insertion failed; add to the failure set
					failedKeyCodes.add(keyCode);
				}
			}
		}
		proc.close();
		
		// Now recalculate the base load size for each loaded product ID
		Iterator<Integer> prodIter = usedProductIDs.iterator();
		while(prodIter.hasNext())
			resetBaseLoad(prodIter.next().intValue());
		
		return failedKeyCodes;
	}
	
	/**
	 * Adds a new entry to the <code>KCB_Product</code> database table.
	 * <p>
	 * <b>Note: </b> This method was created to support automatically adding products found in KCB
	 * load files.  That feature was subsequently disabled to help prevent erroneous file loads,
	 * but the method remains in case of future usefulness.</p>
	 * 
	 * @param vendorID
	 * @param sku
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	private int addProduct(String vendorID, String sku, String name) throws SQLException
	{
		CallableStatement proc = null;
		proc = this.conn.prepareCall("{call keyCodeBank.addProduct(?,?,?,?) }");
		proc.setString(1, vendorID);
		proc.setString(2, sku);
		proc.setString(3, name);
		proc.registerOutParameter(4, OracleTypes.NUMBER);
		proc.execute();
		
		int id = proc.getInt(4);
		proc.close();
		
		return id;
	}
	
	/**
	 * Adds a new entry to the <code>KCB_Vendor</code> database table.
	 * <p>
	 * <b>Note: </b> This method was created to support automatically adding vendors found in KCB
	 * load files.  That feature was subsequently disabled to help prevent erroneous file loads,
	 * but the method remains in case of future usefulness.</p>
	 * @param vendorID
	 * @param name
	 * @throws SQLException
	 */
	private void addVendor(String vendorID, String name) throws SQLException
	{
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO KCB_VENDOR ( VENDORID, NAME ) VALUES ('");
		sql.append(vendorID);
		sql.append("','");
		sql.append(name);
		sql.append("')");
		
		PreparedStatement s = this.conn.prepareStatement(sql.toString());
		s.execute();
		s.close();
	}
	
	public int resetBaseLoad(int productID) throws SQLException
	{
		CallableStatement proc = null;
		proc = this.conn.prepareCall("{call keyCodeBank.resetBaseLoad(?,?)}");
		proc.setInt(1, productID);
		proc.registerOutParameter(2, OracleTypes.NUMERIC);
		
		proc.execute();
		int load = proc.getInt(2);
		proc.close();
		
		return load;
	}
	
	public int getProductID(String sku) throws SQLException
	{	
		if(sku == null || sku.length() == 0)
			throw new IllegalArgumentException("Invalid SKU: input was null or zero-length");
		
		PreparedStatement s = conn.prepareStatement("SELECT PRODUCTID FROM KCB_PRODUCT WHERE SKU = '"+sku+"'");
		ResultSet rs = s.executeQuery();
		
		int productID = -1;
		while(rs != null && rs.next())
			productID = rs.getInt(1);
		
		rs.close();
		s.close();
		return productID;
	}
	
	public Collection<Integer> getProductIDs() throws SQLException
	{
		Collection<Integer> productIDs = null;
		PreparedStatement s = this.conn.prepareStatement("SELECT PRODUCTID FROM KCB_PRODUCT");
		ResultSet rs = s.executeQuery();
		
		productIDs = new HashSet<Integer>(7);
		// Loop through the products, adding each ID/SKU pair to the map
		while(rs != null && rs.next())
			productIDs.add(Integer.valueOf(rs.getInt(1)));
		s.close();
		rs.close();
		
		return productIDs;
	}
	
	public Map<String, Integer> getProductMap() throws SQLException
	{
		Map<String, Integer> productMap = null;
		PreparedStatement s = this.conn.prepareStatement("SELECT PRODUCTID, SKU FROM KCB_PRODUCT");
		ResultSet rs = s.executeQuery();
		
		productMap = new HashMap<String,Integer>(0);
		// Loop through the products, adding each ID/SKU pair to the map
		while(rs != null && rs.next()){
			// Treat the SKU as the map key, since the load data has the SKUs but needs the KCB ProductIDs
			productMap.put(rs.getString(2),Integer.valueOf(rs.getInt(1)));
		}
		s.close();
		rs.close();
		
		return productMap;
	}
	
	public void writeDailyReports(java.sql.Date reportDate) throws SQLException
	{
		CallableStatement proc = this.conn.prepareCall("{call kcbReports.writeDailyReports(?)}");
		proc.setDate(1, reportDate);
		proc.execute();
		proc.close();
		return;
	}
	
	public void writeMonthlyReports(java.sql.Date reportDate) throws SQLException
	{
		CallableStatement proc = this.conn.prepareCall("{call kcbReports.writeMonthlyReports(?)}");
		proc.setDate(1, reportDate);
		proc.execute();
		proc.close();
		return;
	}
	
	public void writeYearlyReports(java.sql.Date reportDate) throws SQLException
	{
		CallableStatement proc = this.conn.prepareCall("{call kcbReports.writeYearlyReports(?)}");
		proc.setDate(1, reportDate);
		proc.execute();
		proc.close();
		return;
	}
	
	public void writeAnnualReports(java.sql.Date reportDate) throws SQLException
	{
		writeYearlyReports(reportDate);
	}
}
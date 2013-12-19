package com.bestbuy.sdp.provisioning.test;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.junit.*;

import com.accenture.common.ex.ApplicationException;
import com.bestbuy.sdp.kcb.KeyCode;
import com.bestbuy.sdp.kcb.KeyCodeBank;


import static org.junit.Assert.*;
import static com.accenture.common.util.DatabaseUtil.getConnection;

public class KeyCodeBankTest 
{
	String loadFileName = "allVendors-dummyData.csv";
	
	static final String URL = "jdbc:oracle:thin:@localhost:1521:SDPDEV";
	static final String USER= "CNF_TPA_SCH01";
	
	static int orderID_counter = 0;
	
	static void wipeKCB(Connection conn) throws SQLException{
		// Wipe the KCB
		String sql;
		PreparedStatement s;
		sql = "DELETE FROM KCB_KEYCODEPROPERTY";
		s	= conn.prepareStatement(sql);
		s.execute();
		sql = "DELETE FROM KCB_KEYCODE";
		s	= conn.prepareStatement(sql);
		s.execute();
		/**
		 * Skipping these for now, to preserve descriptions
		sql = "DELETE FROM KCB_PRODUCT";
		s	= conn.prepareStatement(sql);
		s.execute();
		sql = "DELETE FROM KCB_VENDOR";
		s	= conn.prepareStatement(sql);
		s.execute();
		
		s.close();
		conn.commit();
		conn.close();
		 */
	}
	
	@BeforeClass
	public static void oneTimeSetUp() throws SQLException{
		
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws SQLException{
		
	}
	
	/**
	 * Loads a static test file of randomly-generated dummy data, edited to meet certain
	 * test criteria.
	 * @throws IOException
	 * @throws SQLException
	 */
	@Test
	public void testFreshLoad() throws IOException, SQLException{
		
		wipeKCB(getConnection(URL, USER, USER));
		
		File loadFile	= new File(loadFileName);
		
		try{
			Collection<KeyCode> keyCodes = KeyCode.parse(loadFile);
			KeyCodeBank kcb = new KeyCodeBank(getConnection(URL, USER, USER));
			Collection<KeyCode> failures = kcb.load(keyCodes);
			
			kcb.close();
			
			assertTrue(failures.size() == 0);
		}catch(IOException ioex){
			throw ioex;
		}catch(SQLException sqlex){
			throw sqlex;
		}
	}
	
	/**
	 * Re-loads the load file into KCB, meaning all entries should fail because they
	 * are already present in the KCB.
	 * @throws IOException
	 * @throws SQLException
	 */
	@Test
	public void testRedundantLoad() throws IOException, SQLException{
		File loadFile	= new File(loadFileName);
		
		try{
			Collection<KeyCode> keyCodes = KeyCode.parse(loadFile);
			int count = keyCodes.size();
			KeyCodeBank kcb = new KeyCodeBank(getConnection(URL, USER, USER));
			Collection<KeyCode> failures = kcb.load(keyCodes);
			// Reading from the same file as the fresh load, so all should fail
			assertTrue(failures.size() == count);
		}catch(IOException ioex){
			throw ioex;
		}catch(SQLException sqlex){
			throw sqlex;
		}
	}
	
	/**
	 * Pre-Condition: There are exactly 10 free entries in the KCB for KSP-9315653.
	 * Post-Condition:There are exactly  0 free entries in the KCB for the same product.
	 */
	@Test
	public void testReserve() throws SQLException, ApplicationException {
		KeyCode kc = new KeyCode();
		kc.setVendorID("KSP");
		kc.setSKU("9315653");
		final int NUM_NEEDED = 10;
		
		KeyCodeBank kcb = new KeyCodeBank(getConnection(URL, USER, USER));
		// Use up all the available KeyCodes for this product
		for(int a=0; a < NUM_NEEDED; a++){
			kc.setOrderID(Integer.toString(++orderID_counter));
			kc = kcb.reserve(kc);
			assertTrue((kc.getValue() != null) && (kc.getValue().length() > 0));
		}
		
		kcb.close();
	}
	
	@Test
	public void testReserveExhaustion() throws SQLException, ApplicationException {
		KeyCode kc = new KeyCode();
		kc.setVendorID("KSP");
		kc.setSKU("9315653");
		kc.setOrderID(Integer.toString(++orderID_counter));
		
		KeyCodeBank kcb = new KeyCodeBank(getConnection(URL, USER, USER));
		
		// Now try to reserve again, knowing that none are free (will throw exception)
		try{
			kc = kcb.reserve(kc);
		}catch(Exception ex){
			assertTrue(ex instanceof ApplicationException);
			ApplicationException appex = (ApplicationException)ex;
			// Cause of ApplicationException is SQLException, which should contain informative message from the proc
			assertTrue(appex.getCause().getMessage().contains("supply exhausted"));
			
		}finally{ kcb.close(); }
	}
	
	/**
	 * Pre-Condition: 	10 free entries for KSP-9315662.
	 * Post-Condition: 9 free entries for KSP-9315662, and one reserved.
	 */
	@Test
	public void testDuplicateReserve() throws SQLException, ApplicationException {
		
		String vendorID = "KSP";
		String sku		= "9315662";
		
		KeyCodeBank kcb = new KeyCodeBank(getConnection(URL, USER, USER));
		
		int duplicateOrderID = ++orderID_counter;
		
		KeyCode a;
		KeyCode b;
		
		a = new KeyCode();
		a.setVendorID(vendorID);
		a.setSKU(sku);
		a.setOrderID(Integer.toString(duplicateOrderID));
		// Reserve it once
		a = kcb.reserve(a);
		// Now reserve it again
		b = kcb.reserve(a);
		
		assertTrue(a.equals(b));
		
		kcb.close();
	}
	
	@Test
	public void testInvalidReserve() throws SQLException{
		// Request reservation for an invalid product (vendor ID/SKU combo)
		KeyCode kc = new KeyCode();
		kc.setVendorID("XXX");
		kc.setSKU("5555555");
		kc.setOrderID(Integer.toString(++orderID_counter));
		
		KeyCodeBank kcb = new KeyCodeBank(getConnection(URL, USER, USER));
		
		// Now try to reserve, knowing it will throw exception
		try{
			kc = kcb.reserve(kc);
		}catch(Exception ex){
			assertTrue(ex instanceof ApplicationException);
			ApplicationException appex = (ApplicationException)ex;
			// Cause of ApplicationException is SQLException, which should contain informative message from the proc
			assertTrue(appex.getCause().getMessage().contains("invalid VendorID/SKU combination"));
			
		}finally{ kcb.close(); }
	}
	
	@Test
	public void testDailyReport() throws SQLException{

		KeyCodeBank kcb = new KeyCodeBank(getConnection(URL, USER, USER));
		kcb.writeDailyReports(new java.sql.Date(System.currentTimeMillis()));
		//TODO: Check some values from the report for accuracy
	}
	
	@Test
	public void testMonthlyReport() throws SQLException{

		KeyCodeBank kcb = new KeyCodeBank(getConnection(URL, USER, USER));
		kcb.writeMonthlyReports(new java.sql.Date(System.currentTimeMillis()));
		//TODO: Check some values from the report for accuracy
	}
	
	@Test
	public void testYearlyReport() throws SQLException{

		KeyCodeBank kcb = new KeyCodeBank(getConnection(URL, USER, USER));
		kcb.writeYearlyReports(new java.sql.Date(System.currentTimeMillis()));
		//TODO: Check some values from the report for accuracy
	}
}
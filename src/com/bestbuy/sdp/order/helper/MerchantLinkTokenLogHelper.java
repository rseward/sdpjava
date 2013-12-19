package com.bestbuy.sdp.order.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.environment.WeblogicConsole;

public class MerchantLinkTokenLogHelper {
	
	public void insertRecord(
			String subscriptionOfferId, 
			String vendorID, 
			String bBYID,
			String custName, 
			String ccToken, 
			String cardType, 
			String expirationDate, 
			String address1, 
			String address2, 
			String city, 
			String state, 
			String zip, 
			String processedFlag ) {
		
		Connection conn = null;
		
		CallableStatement proc = null;
		
		
		try {
			conn = RuntimeEnvironment.getConn();

			proc = conn
					.prepareCall("{call merchantLinkTokenLog.insertMerchantLinkTokenLog(?,?,?,?,?,?,?,?,?,?,?,?,?) }");
			proc.setString(1, subscriptionOfferId); 
			proc.setString(2, vendorID); 
			proc.setString(3, bBYID); 
			proc.setString(4, custName); 
			proc.setString(5, ccToken); 
			proc.setString(6, cardType); 
			proc.setString(7, expirationDate); 
			proc.setString(8, address1); 
			proc.setString(9, address2); 
			proc.setString(10, city); 
			proc.setString(11, state); 
			proc.setString(12, zip); 
			proc.setString(13, processedFlag);
			
			proc.executeQuery();
		
			
		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of VPS insertion procedure:");
			sqlex.printStackTrace();
			
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) {
			}

			try {
				if (conn != null)
					RuntimeEnvironment.releaseConn(conn);
			} catch (Exception ex) {
			}

			
		}

		
	}

}

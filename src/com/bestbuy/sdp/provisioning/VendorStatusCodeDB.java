package com.bestbuy.sdp.provisioning;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.environment.WeblogicConsole;

public class VendorStatusCodeDB {
	
	public static final String getSdpCode(String vendorID, String vendorCode) {
		Connection conn = null;
		CallableStatement proc = null;
		
		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn.prepareCall("{call vendorStatusCodes.getSdpCode(?,?,?) }");

			proc.setString(1, vendorID);
			proc.setString(2, vendorCode);
			proc.registerOutParameter(3, java.sql.Types.VARCHAR);
			proc.executeQuery();

			String s = proc.getString(3);
			if (s != null)
				s = s.trim();
			
			return s;
		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of Vendor Status Code getCanRetry procedure:");
			sqlex.printStackTrace();
			return vendorCode;
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) { }

			try {
				if (conn != null)
					RuntimeEnvironment.releaseConn(conn);
			} catch (Exception ex) { }
		}
	}
	
	public static final boolean getCanRetry(String vendorID, String vendorCode) {
		Connection conn = null;
		CallableStatement proc = null;

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn .prepareCall("{call vendorStatusCodes.canRetry(?,?,?) }");

			proc.setString(1, vendorID);
			proc.setString(2, vendorCode);
			proc.registerOutParameter(3, java.sql.Types.CHAR);
			proc.executeQuery();

			String s = proc.getString(3);
			if (s != null)
				s = s.trim();
			
			return (s != null && s.equalsIgnoreCase("Y"));
		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of Vendor Status Code getCanRetry procedure:");
			sqlex.printStackTrace();
			return false;
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) { }

			try {
				if (conn != null)
					RuntimeEnvironment.releaseConn(conn);
			} catch (Exception ex) { }
		}
	}
}

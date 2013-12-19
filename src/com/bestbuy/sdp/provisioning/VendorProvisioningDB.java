package com.bestbuy.sdp.provisioning;

import java.util.ArrayList;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;
import oracle.jdbc.driver.OracleTypes;
import org.apache.xmlbeans.XmlObject;

import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.environment.WeblogicConsole;

public final class VendorProvisioningDB {
	
	public static final int insertRecord(	String vendorID, 
											String vendorKey,
											int csmID,
											String sdpID, 
											String reqType, 
											String status, 
											XmlObject msgXml) {
		int rowID = -1;
		Connection conn = null;
		CallableStatement proc = null;
		oracle.sql.CLOB clob = null;

		// Get standardized "reqType" string
		VendorOperation op = VendorOperation.get(reqType);
		reqType = op.getShortName();

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call vendorProvisioning.insertVPS(?,?,?,?,?,?,?,?) }");

			proc.setString(1, vendorID);
			proc.setString(2, vendorKey);
			proc.setInt(3, csmID);
			proc.setString(4, sdpID);
			proc.setString(5, reqType);
			proc.setString(6, status);

			clob = oracle.sql.CLOB.createTemporary(conn, true, 1);
			if (msgXml != null && msgXml.xmlText().length() > 4000) {
				clob.open(1);
				clob.setString(1, msgXml.xmlText());
				clob.close();

				proc.setClob(7, clob);
			} else {
				proc.setString(7, msgXml.xmlText());
			}

			proc.registerOutParameter(8, Types.INTEGER);
			proc.executeQuery();
			rowID = proc.getInt(8);
		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of VPS insertion procedure:");
			sqlex.printStackTrace();
			return rowID;
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

			try {
				if (clob != null)
					clob.freeTemporary();
			} catch (Exception ex) { }
		}

		return rowID;
	}

	public static final Vendor[] getPendingVendors() {
		Connection conn = null;
		CallableStatement proc = null;

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call vendorProvisioning.getPendingVendors(?) }");

			proc.registerOutParameter(1, OracleTypes.CURSOR);
			proc.executeQuery();

			ResultSet rs = (ResultSet) proc.getObject(1);
			if (rs == null)
				return new Vendor[0];

			ArrayList<Vendor> vendorArray = new ArrayList<Vendor>(3);
			while (rs.next()) {
				vendorArray.add(new Vendor(rs.getString(1)));
			}

			return vendorArray.toArray(new Vendor[vendorArray.size()]);

		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of VPS getPendingVendors procedure:");
			sqlex.printStackTrace();
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
		return new Vendor[0];
	}

	public static final VendorProvisioningRequest[] getPendingRequests(
			String vendorID) {
		Connection conn = null;
		CallableStatement proc = null;

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call vendorProvisioning.getPendingRows(?,?) }");

			proc.setString(1, vendorID);
			proc.registerOutParameter(2, OracleTypes.CURSOR);
			proc.executeQuery();

			ResultSet rs = (ResultSet) proc.getObject(2);
			if (rs == null)
				return new VendorProvisioningRequest[0];

			ArrayList<VendorProvisioningRequest> vendorRequests = new ArrayList<VendorProvisioningRequest>();
			VendorProvisioningRequest loopRequest;
			while (rs.next()) {
				loopRequest = new VendorProvisioningRequest();

				// Vendor ID
				loopRequest.setVendorID(rs.getString(1));
				// Vendor Key (Serial Number)
				loopRequest.setVendorKey(rs.getString(2));
				// CSM ID
				loopRequest.setCsmID(rs.getInt(3));
				// SDP ID (UUID)
				loopRequest.setSdpID(rs.getString(4));
				// Request Type
				loopRequest.setRequestType(rs.getString(5));
				// Status
				loopRequest.setStatus(rs.getString(6));
				// Retry Count
				loopRequest.setRetryCount(rs.getInt(7));
				// Message XML
				loopRequest.setMsgXML((java.sql.Clob) rs.getClob(8));

				vendorRequests.add(loopRequest);
			}

			return vendorRequests
					.toArray(new VendorProvisioningRequest[vendorRequests
							.size()]);

		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of VPS getPendingRows procedure:");
			sqlex.printStackTrace();
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
		return new VendorProvisioningRequest[0];
	}

	public static final String setStatus(String sdpID, String newStatus, String vendorCode){
		
		Connection conn = null;
		CallableStatement proc = null;
		
		String sdpCode = (vendorCode == null ? null : vendorCode.trim());
		
		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn.prepareCall("{call vendorProvisioning.setStatus(?,?,?,?) }");

			proc.setString(1, sdpID);
			proc.setString(2, newStatus);
			proc.setString(3, vendorCode);
			proc.registerOutParameter(4, OracleTypes.VARCHAR);
			
			proc.executeQuery();
			
			sdpCode = proc.getString(4);
		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of VPS setStatus(?,?,?,?) procedure:");
			sqlex.printStackTrace();
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) {
				// Ignore 'finally' block errors.
			}

			try {
				if (conn != null)
					RuntimeEnvironment.releaseConn(conn);
			} catch (Exception ex) {
				// Ignore 'finally' block errors.
			}
		}
		return sdpCode;		
	}
	
	public static final void setStatus(String sdpID, String newStatus) {

		Connection conn = null;
		CallableStatement proc = null;

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn.prepareCall("{call vendorProvisioning.setStatus(?,?) }");

			proc.setString(1, sdpID);
			proc.setString(2, newStatus);

			proc.executeQuery();

		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of VPS setStatus procedure:");
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
		return;
	}

	public static int getRetryCount(String sdpID) {
		Connection conn = null;
		CallableStatement proc = null;

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn.prepareCall("{call vendorProvisioning.getRetryCount(?,?) }");

			proc.setString(1, sdpID);
			proc.registerOutParameter(2, Types.INTEGER);
			proc.executeQuery();

			return proc.getInt(2);

		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of VPS getRetryCount procedure:");
			sqlex.printStackTrace();
			return -1;
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
	
	public static int getCsmID(String sdpID) {
		
		Connection conn = null;
		CallableStatement proc = null;
		
		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn.prepareCall("{call vendorProvisioning.getCsmID(?,?) }");
			
			proc.setString(1, sdpID);
			proc.registerOutParameter(2, Types.INTEGER);
			proc.executeQuery();
			
			return proc.getInt(2);
			
		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of VPS getCsmID procedure:");
			sqlex.printStackTrace();
			return -1;
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
	
	public static String getVendorKey(String sdpID) {
		
		Connection conn = null;
		CallableStatement proc = null;
		
		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn.prepareCall("{call vendorProvisioning.getVendorKey(?,?) }");
			
			proc.setString(1, sdpID);
			proc.registerOutParameter(2, Types.VARCHAR);
			proc.executeQuery();
			
			return proc.getString(2);
			
		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of VPS getCsmID procedure:");
			sqlex.printStackTrace();
			return "VPS Error";
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

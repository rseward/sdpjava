package com.bestbuy.sdp.provisioning;

import java.sql.Types;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.accenture.common.ex.ApplicationException;
import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.environment.WeblogicConsole;
import com.bestbuy.sdp.tpa.AssociationType;
import com.bestbuy.sdp.tpa.VpsLogLinkType;
import com.bestbuy.sdp.tpa.TpaVendorLogMessageDocument;

public class VendorLogDB {
	public static final int insertRequestLog(String vendorID, String reqType,
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
			proc = conn.prepareCall("{call vendorLogs.insertReqLog(?,?,?,?) }");

			proc.setString(1, vendorID);
			proc.setString(2, reqType);

			clob = oracle.sql.CLOB.createTemporary(conn, true, 1);
			if (msgXml != null && msgXml.xmlText().length() > 4000) {
				clob.open(1);
				clob.setString(1, msgXml.xmlText());
				clob.close();

				proc.setClob(3, clob);
			} else {
				proc.setString(3, msgXml.xmlText());
			}

			proc.registerOutParameter(4, Types.INTEGER);
			proc.executeQuery();
			rowID = proc.getInt(4);
		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of Vendor Request Log insertion procedure:");
			sqlex.printStackTrace();
			return rowID;
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

			try {
				if (clob != null)
					clob.freeTemporary();
			} catch (Exception ex) {
			}
		}
		return rowID;
	}

	public static final int insertResponseLog(String vendorID, int reqLogID,
			String reqType, String statusCode, XmlObject msgXml) {
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
					.prepareCall("{call vendorLogs.insertRespLog(?,?,?,?,?,?) }");

			proc.setString(1, vendorID);
			proc.setInt(2, reqLogID);
			proc.setString(3, reqType);
			proc.setString(4, statusCode);

			clob = oracle.sql.CLOB.createTemporary(conn, true, 1);
			if (msgXml != null && msgXml.xmlText().length() > 4000) {
				clob.open(1);
				clob.setString(1, msgXml.xmlText());
				clob.close();
				proc.setClob(5, clob);
			} else {
				proc.setString(5, msgXml.xmlText());
			}

			proc.registerOutParameter(6, Types.INTEGER);
			proc.executeQuery();
			rowID = proc.getInt(6);
		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of Vendor Response Log insertion procedure:");
			sqlex.printStackTrace();
			return rowID;
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

			try {
				if (clob != null)
					clob.freeTemporary();
			} catch (Exception ex) {
			}
		}
		return rowID;
	}
	
	public static final void insertVPSRequestLinkLog(XmlObject msgXml,int reqLogID) throws ApplicationException
	{
		Connection conn = null;
		CallableStatement proc = null;
		TpaVendorLogMessageDocument tpaVenLogMsgDoc = null;
		VpsLogLinkType vpsLogLinkType = null;
		List <AssociationType> associationList =null;
		try
		{
			tpaVenLogMsgDoc=TpaVendorLogMessageDocument.Factory.parse(msgXml.xmlText());
		}
		catch(XmlException xmle)
		{
			throw new ApplicationException("Input request is not of the type TpaVendorLogMessageDocument",1);
		}
		
		if(tpaVenLogMsgDoc != null)
		{
			vpsLogLinkType = tpaVenLogMsgDoc.getTpaVendorLogMessage().getVpsLogLink();
			if(vpsLogLinkType != null)
			{
				associationList = vpsLogLinkType.getAssociationList();
			}
			if(associationList != null)
			{
				int listSize = associationList.size();
				
				for(int arrayCounter = 0; arrayCounter < listSize; arrayCounter++)
			    {
					AssociationType association = associationList.get(arrayCounter);
					try 
					{
						conn = RuntimeEnvironment.getConn();										
						proc = conn.prepareCall("{call vendorLogs.insertVPSRequestLinkLog(?,?,?,?) }");
						proc.setInt(1,reqLogID);
						proc.setString(2, association.getVendorKey());
						proc.setString(3, association.getSdpId());
						proc.registerOutParameter(4, Types.INTEGER);
						proc.executeQuery();
						int rowID = proc.getInt(4);
						if(rowID == -1)
						{
							throw new ApplicationException("Unable to insert in VPSRequestLinkLog for" +
									" SDPID : "+association.getSdpId()+
									" and Request Log ID: "+reqLogID+
									" and Vendor Key: "+association.getVendorKey(),1);
						}
					}
					catch (SQLException sqlex) 
					{
						WeblogicConsole.error("Caught SQLException during execution of VPS RequestLinkLog insertion procedure:");
						sqlex.printStackTrace();
					}
					finally 
					{
						// Clean-up
						try 
						{
							if (proc != null)
								proc.close();
						} 
						catch (Exception ex) 
						{
						}
						try 
						{
							if (conn != null)
								RuntimeEnvironment.releaseConn(conn);
						} 
						catch (Exception ex) {
						}
					}
			    }
			}
			else
			{
				throw new ApplicationException("No links associated in the vpsLogLinkType request",1);
			}
		}
		else
		{
			throw new ApplicationException("tpaVenLogMsgDoc element is null",1);			
		}
	}
}

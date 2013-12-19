package com.bestbuy.sdp.catalog.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.bestbuy.sdp.environment.RuntimeEnvironment;

public class CommsatTemplateProvider {

	public String getTemplateId(String sku, String dateString) throws SQLException {
		String templateId = "EX";
		Connection conn = null;
		CallableStatement proc = null;
		Calendar date = Calendar.getInstance();
		if (dateString != null && dateString.trim().length() > 0 ) {
			try {
				DateFormat formatter;

				formatter = new SimpleDateFormat("yyyy-MM-dd");
				date.setTime(formatter.parse(dateString.substring(0, 10)));
				//Logger.log("Today is " + date);
			} catch (ParseException e) {
				//Logger.log("Exception :" + e);
			}
		}
		//Logger.log("sku :" + sku);
		//Logger.log("dateString :" + dateString);
		//Logger.log("date :" + date);
		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call commsatConfig.getTemplateId(?,?,?) }");

			proc.setString(1, sku);
			proc.setDate(2, new Date(date.getTimeInMillis()));
			proc.registerOutParameter(3, Types.VARCHAR);
			proc.executeQuery();

			templateId = proc.getString(3);

		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			throw new SQLException(sqlex.getMessage());
//			WeblogicConsole
//					.error("Caught SQLException during execution of commsatConfig.getTemplateId:");
//			sqlex.printStackTrace();
//			return templateId;
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

		return templateId;
	}

	public String getTemplateId(String masterVendorId, String prodType, String dateString)throws SQLException {
		String templateId = "EX";
		Connection conn = null;
		CallableStatement proc = null;
		Calendar date = Calendar.getInstance();
		if (dateString != null && dateString.trim().length() > 0 ) {
			try {
				DateFormat formatter;

				formatter = new SimpleDateFormat("yyyy-MM-dd");
				date.setTime(formatter.parse(dateString.substring(0, 10)));
				//Logger.log("Today is " + date);
			} catch (ParseException e) {
				//Logger.log("Exception :" + e);
			}
		}
		//Logger.log("masterVendorId :" + masterVendorId);
		//Logger.log("prodType :" + prodType);
		//Logger.log("dateString :" + dateString);
		//Logger.log("date :" + date);
		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call commsatConfig.getTemplateId(?,?,?,?) }");

			proc.setString(1, masterVendorId);
			proc.setString(2,prodType);
			proc.setDate(3, new Date(date.getTimeInMillis()));
			proc.registerOutParameter(4, Types.VARCHAR);
			proc.executeQuery();

			templateId = proc.getString(4);

		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			throw new SQLException(sqlex.getMessage());
//			WeblogicConsole
//			.error("Caught SQLException during execution of commsatConfig.getTemplateId:");
//			sqlex.printStackTrace();
//			return templateId;
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

		return templateId;
	}


}

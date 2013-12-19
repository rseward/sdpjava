package com.bestbuy.sdp.order.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.environment.WeblogicConsole;

public class CommsatLogHelper {

	public void insertRecord(String subscriptionOfferId, String emailId,
			String redemptionCode, String templateId, String status, String userId,
			String confirmationId, String transactionDate, String bbyId, String lineItemId, String regId) {

		Connection conn = null;
		CallableStatement proc = null;

		Calendar date = Calendar.getInstance();
		if (transactionDate != null && transactionDate.trim().length() > 0 ) {
			try {
				DateFormat formatter;

				formatter = new SimpleDateFormat("yyyy-MM-dd");
				date.setTime(formatter.parse(transactionDate.substring(0, 10)));
				//Logger.log("Today is " + date);
			} catch (ParseException e) {
				//Logger.log("Exception :" + e);
			}
		}

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call commsatHistory.insertCommunicationLog(?,?,?,?,?,?,?,?,?,?,?) }");

			proc.setString(1, subscriptionOfferId);
			proc.setString(2, emailId);
			proc.setString(3, redemptionCode);
			proc.setString(4, templateId);
			proc.setString(5, status);
			proc.setString(6, userId);
			proc.setString(7, confirmationId);
			proc.setDate(8,  new Date(date.getTimeInMillis()));
			proc.setString(9, bbyId);
			proc.setString(10, lineItemId);
			proc.setString(11, regId);

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

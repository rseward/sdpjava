package com.bestbuy.sdp.order.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.services.exception.SDPInternalException;


public class InsertExceptionHelper {
	public void insertSDPException(Connection conn, String sdpId, String sdpOrderId,
			String profileType, String rqstMsg, String stckTrace,
			String srcSysId, String errCde) throws SDPInternalException, SQLException{

		CallableStatement proc = null;

		try{
			//Commented by Krapa: using stored procedure instead of sql query
//			sqlCommand = "INSERT INTO SDP_EXCEPTION_MESSAGE_LOG (SDP_ID, SDP_ORDER_ID, PROD_TYP, RQST_MSG, STACK_TRC, SRC_SYS_ID, ERR_CDE, " +
//					"REC_CRT_TS, REC_CRT_USR_ID, REC_STAT_CDE, ROW_NUM) VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE, ?, ?, 1)";

			proc = conn
			.prepareCall("{call RTA.insertException(?, ?, ?, ?, ?, ?, ?) }");

			if(sdpId != null && sdpId.trim().length() > 0){
				proc.setString(1, sdpId);
			}else{
				proc.setString(1, null);
			}

			if(sdpOrderId != null && sdpOrderId.trim().length() > 0){
				proc.setString(2, sdpOrderId);
			}else{
				proc.setString(2, null);
			}

			if(profileType != null && profileType.trim().length() > 0){
				proc.setString(3, profileType);
			}else{
				proc.setString(3, null);
			}

			if(rqstMsg != null && rqstMsg.trim().length() > 0){
				proc.setString(4, rqstMsg);
			}else{
				throw new SDPInternalException("", "Insert Exception :: request message is null. ");
			}

			if(stckTrace != null && stckTrace.trim().length() > 0){
				proc.setString(5, stckTrace);
			}else{
				proc.setString(5, null);
			}

			if(srcSysId != null && srcSysId.trim().length() > 0){
				proc.setString(6, srcSysId);
			}else{
				proc.setString(6, null);
			}

			if(errCde != null && errCde.trim().length() > 0){
				proc.setString(7, errCde);
			}else{
				proc.setString(7, null);
			}

			proc.executeQuery();


		}catch(SQLException sql){
			sql.printStackTrace();

			throw new SDPInternalException("", "SQL exception when inserting Exception Message: "
					+ sql.getMessage() );

		}finally{
			try {
				proc.close();
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}
	}
}

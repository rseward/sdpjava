package com.bestbuy.sdp.order.helper;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail.ErrorDescription;
import com.bestbuy.schemas.sdp.db.updateOrder.UpdateOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.updateOrder.UpdateOrderResponseDocument.UpdateOrderResponse;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.services.SdpDBConstants;
import com.bestbuy.sdp.services.exception.SDPInternalException;

public class UpdateOrderHelper {
	

	public void updateSDPCustomer(Connection conn, String sdpCustId,
			String custId, String custMailTxt, String lastName,
			String firstName, String midName, String addrLn1, String addrLn2,
			String bldAddrLblTxt, String city, String country, String ccExpDt,
			String ccName, String ccNum, String ccType, String phNo,
			String phAddrLbl, String pstCode, String stateCde,
			String dlvrEmailTxt, String recStatCde, String rewardZoneId) throws SQLException {

		CallableStatement proc = null;
		
		try{
//			sqlCommand = "UPDATE SDP_CUSTOMER SET CUST_ID = ?, CUST_EMAIL_TXT = ?, CUST_LAST_NM = ?, " +
//				" CUST_FRST_NM = ?, CUST_MID_NM = ?, ADDR_LINE1_TXT = ?, ADDR_LINE2_TXT = ?, BLG_ADDR_LBL_TXT = ?, CITY_TXT = ?, " +
//				" CTRY_TXT = ?, CRCD_EXP_DT = ?, CRCD_NM = ?, CRCD_NBR = ?, CRCD_TYP = ?, PH_NBR = ?, PH_ADDR_LBL = ?, POSTAL_CDE = ?, " +
//				" STATE_CDE = ?, DLVR_EMAIL_TXT = ?, REC_UPD_TS = SYSDATE, REC_UPD_USR_ID = ? , REC_STAT_CDE = ? " +
//				" WHERE SDP_CUST_ID = ?";
		
			proc = conn
			.prepareCall("{call RTA.updateSDPCustomer( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");
			
			if(custId != null && custId.trim().length() > 0){
				proc.setString(1, custId);
			}else{
				proc.setString(1, null);
			}
			
			if(custMailTxt != null && custMailTxt.trim().length() > 0){
				proc.setString(2, custMailTxt);
			}else{
				proc.setString(2, null);
			}
			
			if(lastName != null && lastName.trim().length() > 0){
				proc.setString(3, lastName);
			}else{
				proc.setString(3, null);
			}
			
			if(firstName != null && firstName.trim().length() > 0){
				proc.setString(4, firstName);
			}else{
				proc.setString(4, null);
			}
			
			if(midName != null && midName.trim().length() > 0){
				proc.setString(5, midName);
			}else{
				proc.setString(5, null);
			}
			
			if(addrLn1 != null && addrLn1.trim().length() > 0){
				proc.setString(6, addrLn1);
			}else{
				proc.setString(6, null);
			}
			
			if(addrLn2 != null && addrLn2.trim().length() > 0){
				proc.setString(7, addrLn2);
			}else{
				proc.setString(7, null);
			}
			
			if(bldAddrLblTxt != null && bldAddrLblTxt.trim().length() > 0){
				proc.setString(8, bldAddrLblTxt);
			}else{
				proc.setString(8, null);
			}
			
			if(city != null && city.trim().length() > 0){
				proc.setString(9, city);
			}else{
				proc.setString(9, null);
			}
			
			if(country != null && country.trim().length() > 0){
				proc.setString(10, country);
			}else{
				proc.setString(10, null);
			}
			
			if(ccExpDt != null && ccExpDt.trim().length() > 0){
				proc.setString(11, ccExpDt);
			}else{
				proc.setString(11, null);
			}
			
			if(ccName != null && ccName.trim().length() > 0){
				proc.setString(12, ccName);
			}else{
				proc.setString(12, null);
			}
			
			if(ccNum != null && ccNum.trim().length() > 0){
				proc.setString(13, ccNum);
			}else{
				proc.setString(13, null);
			}
			
			if(ccType != null && ccType.trim().length() > 0){
				proc.setString(14, ccType);
			}else{
				proc.setString(14, null);
			}
			
			if(phNo != null && phNo.trim().length() > 0){
				proc.setString(15, phNo);
			}else{
				proc.setString(15, null);
			}
			
			if(phAddrLbl != null && phAddrLbl.trim().length() > 0){
				proc.setString(16, phAddrLbl);
			}else{
				proc.setString(16, null);
			}
			
			if(pstCode != null && pstCode.trim().length() > 0){
				proc.setString(17, pstCode);
			}else{
				proc.setString(17, null);
			}
			
			if(stateCde != null && stateCde.trim().length() > 0){
				proc.setString(18, stateCde);
			}else{
				proc.setString(18, null);
			}
			
			if(dlvrEmailTxt != null && dlvrEmailTxt.trim().length() > 0){
				proc.setString(19, dlvrEmailTxt);
			}else{
				proc.setString(19, null);
				
			}
			
			if(rewardZoneId != null && rewardZoneId.trim().length() > 0){
				proc.setString(20, rewardZoneId);
			}else{
				proc.setString(20, null);
			}
			
			if(sdpCustId != null && sdpCustId.trim().length() > 0){
				proc.setString(21, sdpCustId);
			}else{
				proc.setString(21, null);
			}
			
			proc.executeQuery();
		} finally {
			try {
				proc.close();
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}
	}
		
	public void updateSDPOrder(Connection conn, String sdpOrderId,
			String sdpId, String triggerSku, String externalId,
			String parentSku, String transTmsp, String transId, String transDt,
			String storeId, String regId, String lineId, String lineItemId,
			String busKeyType, String busKey, String primSkuPrc, String primSkuTax,
			String primSkuTaxRate, String prntSkuPrc, String prntSkuTax,
			String prntSkuTaxRate, String qnty, String valPkgId,
			String srcSysId, String confId, String keyCode, String masterItemId,
			String vndrPrdId, String vndrId, String catalogId, String sdpCustId,
			String cnclRsnCode, String ordStatCde, String recStatCde, 
			String errorCde) throws SQLException,SDPInternalException {

		CallableStatement proc = null;
		
		try{
			
			//Commented by Krapa: using stored procedure
//			sqlCommand = "UPDATE SDP_ORDER SET SDP_ID= ? , PRM_SKU_ID= ? , EXTNL_ID= ? ," +
//				" PRNT_SKU= ? , ERR_CDE = ? , TRANS_TS= ? , TRANS_ID= ? , TRANS_DT= ? , STOR_ID= ? ," +
//				" RGST_ID= ? , LN_ID= ? , LN_ITEM_ID= ? , BSNS_KEY= ? , PRM_SKU_PRC= ? ," +
//				" PRM_SKU_TAX= ? , PRM_SKU_TAX_RATE= ? , PRNT_SKU_PRC= ? , PRNT_SKU_TAX= ? , " +
//				" PRNT_SKU_TAX_RATE= ? , QTY= ? , VAL_PKG_ID= ? , SRC_SYS_ID= ? , CONF_ID= ? ," +
//				" KEY_CDE= ? , MSTR_ITM_ID= ? , VNDR_PROD_ID= ? , VNDR_ID= ? ," +
//				" CTLG_ID= ? , SDP_CUST_ID= ? , CNCL_REAS_CDE = ? ," +
//				" ORD_STAT_CDE= ? ,  REC_STAT_CDE= ?, BSNS_KEY_TYP= ?  WHERE SDP_ORDER_ID = ? ";
			
			proc = conn
			.prepareCall("{call RTA.updateSDPOrder( ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, " +
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  " +
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");
			
			
			if(sdpId != null && sdpId.trim().length() > 0){
				proc.setString(1, sdpId);
			}else{
				proc.setString(1, null);
			}
			
			if(triggerSku != null && triggerSku.trim().length() > 0){
				proc.setString(2, triggerSku);
			}else{
				proc.setString(2, null);
			}
			
			if(externalId != null && externalId.trim().length() > 0){
				proc.setString(3, externalId);
			}else{
				proc.setString(3, null);
			}
			
			if(parentSku != null && parentSku.trim().length() > 0){
				proc.setString(4, parentSku);
			}else{
				proc.setString(4, null);
			}	

			if(errorCde != null && errorCde.trim().length() > 0){
				proc.setString(5, errorCde);
			}else{
				proc.setString(5, null);
			}
			
			if(transTmsp != null && transTmsp.trim().length() > 0){
				proc.setTimestamp(6, stringToTimestampConvert(transTmsp));
			}else{
				proc.setString(6, null);
			}
			
			if(transId != null && transId.trim().length() > 0){
				proc.setString(7, transId);
			}else{
				proc.setString(7, null);
			}
			
			if(transDt != null && transDt.trim().length() > 0){
				proc.setDate(8, stringToDateConvert(transDt));
			}else{
				proc.setDate(8, null);
			}
			
			if(storeId != null && storeId.trim().length() > 0){
				proc.setString(9, storeId);
			}else{
				proc.setString(9, null);
			}
			
			if(regId != null && regId.trim().length() > 0){
				proc.setString(10, regId);
			}else{
				proc.setString(10, null);
			}
			
			if(lineId != null && lineId.trim().length() > 0){
				proc.setString(11, lineId);
			}else{
				proc.setString(11, null);
			}
			
			if(lineItemId != null && lineItemId.trim().length() > 0){
				proc.setString(12, lineItemId);
			}else{
				proc.setString(12, null);
			}
			
			if(busKey != null && busKey.trim().length() > 0){
				proc.setString(13, busKey);
			}else{
				proc.setString(13, null);
			}
			
			if(busKeyType != null && busKeyType.trim().length() > 0){
				proc.setString(14, busKeyType);	
			}else{
				proc.setString(14, null);
			}
			
			if(primSkuPrc != null && primSkuPrc.trim().length() > 0){
				proc.setString(15, primSkuPrc);
			}else{
				proc.setString(15, null);	
			}
			
			if(primSkuTax != null && primSkuTax.trim().length() > 0){
				proc.setString(16, primSkuTax);
			}else{
				proc.setString(16, null);
			}
			
			if(primSkuTaxRate != null && primSkuTaxRate.trim().length() > 0){
				proc.setString(17, primSkuTaxRate);
			}else{
				proc.setString(17, null);
			}
			
			if(prntSkuPrc != null && prntSkuPrc.trim().length() > 0){
				proc.setString(18, prntSkuPrc);
			}else{
				proc.setString(18, null);
			}
			
			if(prntSkuTax != null && prntSkuTax.trim().length() > 0){
				proc.setString(19, prntSkuTax);
			}else{
				proc.setString(19, null);
			}
			
			if(prntSkuTaxRate != null && prntSkuTaxRate.trim().length() > 0){
				proc.setString(20, prntSkuTaxRate);
			}else{
				proc.setString(20, null);
			}
			
			if(qnty != null && qnty.trim().length() > 0){
				proc.setString(21, qnty);
			}else{
				proc.setString(21, null);
			}
			
			if(valPkgId != null && valPkgId.trim().length() > 0){
				proc.setString(22, valPkgId);
			}else{
				proc.setString(22, null);
			}
			
			if(srcSysId != null && srcSysId.trim().length() > 0){
				proc.setString(23, srcSysId);
			}else{
				proc.setString(23, null);
			}
			
			if(confId != null && confId.trim().length() > 0){
				proc.setString(24, confId);
			}else{
				proc.setString(24, null);
			}		
			
			if(keyCode != null && keyCode.trim().length() > 0){
				proc.setString(25, keyCode);
			}else{
				proc.setString(25, null);
			}
			
			if(masterItemId != null && masterItemId.trim().length() > 0){
				proc.setString(26, masterItemId);
			}else{
				proc.setString(26, null);
			}
			
			if(vndrPrdId != null && vndrPrdId.trim().length() > 0){
				proc.setString(27, vndrPrdId);
			}else{
				proc.setString(27, null);
			}
			//Commented by Krapa: not present in procedure
//			if(vndrId != null && vndrId.trim().length() > 0){
//				proc.setString(27, vndrId);
//			}else{
//				proc.setString(27, null);
//			}
			//Commented by Krapa: column deleted from sdp_order
//			if(prodType != null && prodType.trim().length() > 0){
//				ps.setString(28, prodType);	
//			}else{
//				ps.setString(28, null);
//			}
			
			if(catalogId != null && catalogId.trim().length() > 0){
				proc.setString(28, catalogId);	
			}else{
				proc.setString(28, null);
			}
			
			if(sdpCustId != null && sdpCustId.trim().length() > 0){
				proc.setString(29, sdpCustId);	
			}else{
				proc.setString(29, null);
			}
			
			if(cnclRsnCode != null && cnclRsnCode.trim().length() > 0){
				proc.setString(30, cnclRsnCode);	
			}else{
				proc.setString(30, null);
			}
			
			if(ordStatCde != null && ordStatCde.trim().length() > 0){
				proc.setString(31, ordStatCde);	
			}else{
				proc.setString(31, null);
			}
			
			if(sdpOrderId != null && sdpOrderId.trim().length() > 0){
				proc.setString(32, sdpOrderId);	
			}else{
				proc.setString(32, null);
			}
			proc.executeQuery();
		}  finally {
			try {
				proc.close();
			} catch (SQLException e) {
				e.printStackTrace();
				Logger.logStackTrace(e.fillInStackTrace());
			}
		} 
	}

	public void updateSDPSubscription(Connection conn,
			String sdpOrderId, String contractId, String contractStCde,
			String contractEndDt, String recStatCde) throws SQLException,SDPInternalException {

		PreparedStatement proc = null;
		
		try{
			
//			sqlCommand = "UPDATE SDP_SUBSCRIPTION SET CNTRCT_ID = ?, CNTRCT_END_DT = ?, CNTRCT_STAT_CDE = ? " +
//				"WHERE SDP_ORDER_ID = ?";
			
			proc = conn
			.prepareCall("{call RTA.updateSDPSubscription( ?, ?, ?, ?) }");
			
			if(contractId != null && contractId.trim().length() > 0){
				proc.setString(1, contractId);
			}else{
				proc.setString(1, null);
			}
			
			if(contractEndDt != null && contractEndDt.trim().length() > 0){
				proc.setDate(2, stringToDateConvert(contractEndDt));
			}else{
				proc.setString(2, null);
			}
			
			if(contractStCde != null && contractStCde.trim().length() > 0){
				proc.setString(3, contractStCde);
			}else{
				proc.setString(3, null);
			}
			
			if(sdpOrderId != null && sdpOrderId.trim().length() > 0){
				proc.setString(4, sdpOrderId);
			}else{
				proc.setString(4, null);
			}
			proc.executeQuery();
			
		}finally {
			try {
				proc.close();
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		} 
	}

	public void updateSDPRequestResponseLog(Connection conn, String lineItemId,
			String sdpOrderId, String sdpId, String rqstType, String profileId,
			String srcSysId, String rqstMsg, String respMsg, String transactionStatusCode)
			throws SDPInternalException, SQLException {

		
		PreparedStatement stmt = null;

		try {
			String sqlCommand = "UPDATE SDP_ORD_RQST_RESP_LOG SET " 
				+(lineItemId != null ? "LN_ITEM_ID = ?, " : "")		
				+(sdpOrderId != null ? "SDP_ORDER_ID = ?, " : "")
					+ (rqstType != null ? "RQST_TYP = ?, " : "") 
					+ ("PRFL_ID = ?, ") 
					+ (srcSysId != null ? "SRC_SYS_ID =?, ": "") 
					+ (rqstMsg != null ? "RQST_MSG =?, " : "" )
					+ (respMsg != null ? "RESP_MSG =?, " : "" )
					+ (transactionStatusCode != null ? "STAT_CDE = ? ," : "") 
					+ "REC_UPD_TS = SYSDATE, "
					+"REC_UPD_USR_ID = 'SDP'"
					+" WHERE SDP_ID = ? AND PRFL_ID != 1";
			
			stmt = conn.prepareStatement(sqlCommand);
			int index =0;
			if (lineItemId != null && lineItemId.trim().length() > 0) {
				stmt.setString(++index, lineItemId);
			} 
			if (sdpOrderId != null && sdpOrderId.trim().length() > 0) {
				stmt.setString(++index, sdpOrderId);
			} 
			if (rqstType != null && rqstType.trim().length() > 0) {
				stmt.setString(++index, rqstType);
			} 
			if (profileId != null && profileId.trim().length() > 0) {
				stmt.setString(++index, profileId);
			}
			if (srcSysId != null && srcSysId.trim().length() > 0) {
				stmt.setString(++index, srcSysId);
			} 
			if (rqstMsg != null && rqstMsg.trim().length() > 0) {
				stmt.setString(++index, rqstMsg);
			} 
			if (respMsg != null && respMsg.trim().length() > 0) {
				stmt.setString(++index, respMsg);
			} 
			if (transactionStatusCode != null && transactionStatusCode.trim().length() > 0) {
				stmt.setString(++index, transactionStatusCode);
			} 
			if (sdpId != null && sdpId.trim().length() > 0) {
				stmt.setString(++index, sdpId);
			}

			 int rs = stmt.executeUpdate();

		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}
	}
	
	public void updateSDPOrderDetails(Connection conn, String sdpOrderId,
			String name, String value, String recStatCde) {
	
		CallableStatement proc = null;
		
		try{
//			sqlCommand = "UPDATE SDP_ORDER_DETAILS SET ATTR_KEY = ?, ATTR_VAL = ?, REC_STAT_CDE = ? " +
//			"WHERE SDP_ORDER_ID = ?";
			proc = conn.prepareCall("{call RTA.updateSDPOrderDetails(?,?,?) }");
			
			if(name != null && name.trim().length() > 0){
				proc.setString(1, name.trim());
			}else{
				proc.setString(1, null);
			}
			
			if(value != null && value.trim().length() > 0){
				proc.setString(2, value.trim());
			}else{
				proc.setString(2, null);
			}
			
			if(sdpOrderId != null && sdpOrderId.trim().length() > 0){
				proc.setString(3, sdpOrderId);
			}else{
				proc.setString(3, null);
			}
			proc.executeUpdate();
			
		} catch (SQLException e) {
			Logger.logStackTrace(e.fillInStackTrace());
		} finally {
			try {
				proc.close();
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		} 
		
	}
	
	public UpdateOrderResponseDocument generateResponse(String status, String errorCde,String errorDscp){
		
		UpdateOrderResponseDocument responseDocument = UpdateOrderResponseDocument.Factory.newInstance();
		UpdateOrderResponse orderResponse = responseDocument.addNewUpdateOrderResponse();;
		
		ServiceResult serviceResult = null;
		serviceResult = orderResponse.addNewServiceResult();
		
		// check status code
		if(status != null){
			if(status.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_SUCCESS)){
				serviceResult.setStatusCode(BigInteger.ZERO);
			}else if(status.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_FAILURE)){
				serviceResult.setStatusCode(BigInteger.ONE);
				serviceResult.setErrorCode(errorCde);
				ErrorDetailList detail = serviceResult.addNewErrorDetailList();
				ErrorDetail errorDtl = detail.addNewErrorDetail();
				errorDtl.setErrorCode(errorCde);
				errorDtl.setMoreDetail(errorDscp);
				ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
				errorDsp.setOriginal(errorDscp);
			}	
		}else{
			serviceResult.setStatusCode(BigInteger.ONE);
			serviceResult.setErrorCode(errorCde);
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode(errorCde);
			errorDtl.setMoreDetail(errorDscp);
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(errorDscp);
		}
		return responseDocument;
	}
	
	// Note date format to be in yyyy-MM-dd
	private java.sql.Date stringToDateConvert(String date) throws SDPInternalException{
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");   
		java.sql.Date sqltDate = null;
		try {
			java.util.Date parsedUtilDate = formatter.parse(date);   
			sqltDate = new java.sql.Date(parsedUtilDate.getTime()); 
			
		} catch (ParseException e) {
			Logger.logStackTrace(e.fillInStackTrace());
		      throw new SDPInternalException("20040003","Please valid timestamp - >yyyy-MM-dd");
		    
		}
		return sqltDate;
	}
	
	// Note timestamp format to be in yyyy-MM-dd
	private java.sql.Timestamp stringToTimestampConvert (String date) throws SDPInternalException{
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    Timestamp sqlTimstamp = null;
	
	    date = date.replace("T", " ");
	    try {
	      java.util.Date parsedUtilDate = formatter.parse(date);
	      sqlTimstamp = new Timestamp(parsedUtilDate.getTime());
	    }
	    catch (ParseException e) {
	    	Logger.logStackTrace(e.fillInStackTrace());
	      throw new SDPInternalException("20040003","Please valid timestamp - >yyyy-MM-dd hh:mm:ss ");
	    	
	    }
	    return sqlTimstamp;
	  }
	
}

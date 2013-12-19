package com.bestbuy.sdp.order.helper;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import oracle.jdbc.driver.OracleTypes;

import com.accenture.xml.sdp.bby.om.bbyOrder.BBYOrder;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail.ErrorDescription;
import com.accenture.xml.sdp.bby.utilities.identifier.ArrayofExternalID;
import com.accenture.xml.sdp.bby.utilities.identifier.ExternalID;
import com.bestbuy.schemas.sdp.db.createOrder.CreateOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.createOrder.CreateOrderResponseDocument.CreateOrderResponse;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.order.bean.RetrieveOrderVO;
import com.bestbuy.sdp.services.SdpDBConstants;
import com.bestbuy.sdp.services.exception.DuplicateOrderExistsException;
import com.bestbuy.sdp.services.exception.SDPInternalException;

/**
 * Helper class for Real time activation create order java callouts.
 * 
 * @author a148045
 * 
 */
public class CreateOrderHelper {

	public void insertSDPSubscription(Connection conn, String spdOrderId,
			String contractId, String contractStCde, String contractEndDt)
			throws SDPInternalException, SQLException {

		CallableStatement proc = null;

		try {
			proc = conn
					.prepareCall("{call RTA.insertSubscriptionData(?, ?, ?, ?) }");

			if (spdOrderId != null && spdOrderId.trim().length() > 0) {
				proc.setString(1, spdOrderId);
			} else {
				proc.setString(1, null);
			}

			if (contractId != null && contractId.trim().length() > 0) {
				proc.setString(2, contractId);
			} else {
				proc.setString(2, null);
			}

			if (contractEndDt != null && contractEndDt.trim().length() > 0) {
				proc.setDate(3, stringToDateConvert(contractEndDt));
			} else {
				proc.setString(3, null);
			}

			if (contractStCde != null && contractStCde.trim().length() > 0) {
				proc.setString(4, contractStCde);
			} else {
				proc.setDate(4, null);
			}

			proc.execute();

		} finally {
			try {
				proc.close();
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}
	}

	/**
	 * Inserts data in SDP_ORDER table this table contains all the key
	 * information of sdp order with sdpOrderId as a primary key and catalogId,
	 * sdpCustId and ordStatCde as a foreign key.
	 * 
	 * Throws DuplicateOrderExistsException when order already exists. Throws
	 * SDPInternalException for catching all the exception in SDP.
	 * 
	 * @param conn
	 * @param sdpOrderId
	 * @param sdpId
	 * @param triggerSku
	 * @param externalId
	 * @param parentSku
	 * @param transTmsp
	 * @param transId
	 * @param transDt
	 * @param storeId
	 * @param regId
	 * @param lineId
	 * @param lineItemId
	 * @param busKey
	 * @param primSkuPrc
	 * @param primSkuTax
	 * @param primSkuTaxRate
	 * @param prntSkuPrc
	 * @param prntSkuTax
	 * @param prntSkuTaxRate
	 * @param qnty
	 * @param valPkgId
	 * @param srcSysId
	 * @param confId
	 * @param keyCode
	 * @param masterItemId
	 * @param vndrPrdId
	 * @param vndrId
	 * @param catalogId
	 * @param sdpCustId
	 * @param cnclRsnCode
	 * @param ordStatCde
	 * @param recStatCde
	 * @param errorCde
	 * @return
	 * @throws DuplicateOrderExistsException
	 * @throws SDPInternalException
	 */
	public String insertSDPOrder(Connection conn, String sdpOrderId,
			String sdpId, String triggerSku, String externalId,
			String parentSku, String transTmsp, String transId, String transDt,
			String storeId, String regId, String lineId, String lineItemId,
			String busKeyType, String busKey, String primSkuPrc,
			String primSkuTax, String primSkuTaxRate, String prntSkuPrc,
			String prntSkuTax, String prntSkuTaxRate, String qnty,
			String valPkgId, String srcSysId, String confId, String keyCode,
			String masterItemId, String vndrPrdId, String vndrId,
			String catalogId, String sdpCustId, String cnclRsnCode,
			String ordStatCde, String recStatCde, String errorCde)
			throws DuplicateOrderExistsException, SDPInternalException,
			SQLException {

		CallableStatement proc = null;

		try {

			proc = conn
					.prepareCall("{call RTA.insertOrderData(?,?, ?, ?, ?,?,?, ?, ?, ?,?,?, ?, ?, ?,?,?, ?, ?, ?,?,?, ?, ?, ?,?,?, ?, ?, ?,?,?) }");
			/*
			 * Commented by Logesh if(sdpOrderId != null &&
			 * sdpOrderId.trim().length() > 0){ proc.setString(1, sdpOrderId);
			 * }else{ proc.setString(1, null); }
			 */
			proc.registerOutParameter(1, OracleTypes.VARCHAR);

			if (sdpId != null && sdpId.trim().length() > 0) {
				proc.setString(2, sdpId);
			} else {
				proc.setString(2, null);
			}

			if (triggerSku != null && triggerSku.trim().length() > 0) {
				proc.setString(3, triggerSku);
			} else {
				proc.setString(3, null);
			}

			if (externalId != null && externalId.trim().length() > 0) {
				proc.setString(4, externalId);
			} else {
				proc.setString(4, null);
			}

			if (parentSku != null && parentSku.trim().length() > 0) {
				proc.setString(5, parentSku);
			} else {
				proc.setString(5, null);
			}

			if (transTmsp != null && transTmsp.trim().length() > 0) {
				proc.setTimestamp(6, stringToTimestampConvert(transTmsp));
			} else {
				proc.setString(6, null);
			}

			if (transId != null && transId.trim().length() > 0) {
				proc.setString(7, transId);
			} else {
				proc.setString(7, null);
			}

			if (transDt != null && transDt.trim().length() > 0) {
				proc.setDate(8, stringToDateConvert(transDt));
			} else {
				proc.setDate(8, null);
			}

			if (storeId != null && storeId.trim().length() > 0) {
				proc.setString(9, storeId);
			} else {
				proc.setString(9, null);
			}

			if (regId != null && regId.trim().length() > 0) {
				proc.setString(10, regId);
			} else {
				proc.setString(10, null);
			}

			if (lineId != null && lineId.trim().length() > 0) {
				proc.setString(11, lineId);
			} else {
				proc.setString(11, null);
			}

			if (lineItemId != null && lineItemId.trim().length() > 0) {
				proc.setString(12, lineItemId);
			} else {
				proc.setString(12, null);
			}
			// Added by Krapa : to populate BSNS_KEY_TYP column
			if (busKeyType != null && busKeyType.trim().length() > 0) {
				proc.setString(13, busKeyType);
			} else {
				proc.setString(13, null);
			}

			if (busKey != null && busKey.trim().length() > 0) {
				proc.setString(14, busKey);
			} else {
				proc.setString(14, null);
			}

			if (primSkuPrc != null && primSkuPrc.trim().length() > 0) {
				proc.setString(15, primSkuPrc);
			} else {
				proc.setString(15, null);
			}

			if (primSkuTax != null && primSkuTax.trim().length() > 0) {
				proc.setString(16, primSkuTax);
			} else {
				proc.setString(16, null);
			}

			if (primSkuTaxRate != null && primSkuTaxRate.trim().length() > 0) {
				proc.setString(17, primSkuTaxRate);
			} else {
				proc.setString(17, null);
			}

			if (prntSkuPrc != null && prntSkuPrc.trim().length() > 0) {
				proc.setString(18, prntSkuPrc);
			} else {
				proc.setString(18, null);
			}

			if (prntSkuTax != null && prntSkuTax.trim().length() > 0) {
				proc.setString(19, prntSkuTax);
			} else {
				proc.setString(19, null);
			}

			if (prntSkuTaxRate != null && prntSkuTaxRate.trim().length() > 0) {
				proc.setString(20, prntSkuTaxRate);
			} else {
				proc.setString(20, null);
			}

			if (qnty != null && qnty.trim().length() > 0) {
				proc.setString(21, qnty);
			} else {
				proc.setString(21, null);
			}

			if (valPkgId != null && valPkgId.trim().length() > 0) {
				proc.setString(22, valPkgId);
			} else {
				proc.setString(22, null);
			}

			if (srcSysId != null && srcSysId.trim().length() > 0) {
				proc.setString(23, srcSysId);
			} else {
				proc.setString(23, null);
			}

			if (confId != null && confId.trim().length() > 0) {
				proc.setString(24, confId);
			} else {
				proc.setString(24, null);
			}

			if (keyCode != null && keyCode.trim().length() > 0) {
				proc.setString(25, keyCode);
			} else {
				proc.setString(25, null);
			}

			if (masterItemId != null && masterItemId.trim().length() > 0) {
				proc.setString(26, masterItemId);
			} else {
				proc.setString(26, null);
			}

			if (vndrPrdId != null && vndrPrdId.trim().length() > 0) {
				proc.setString(27, vndrPrdId);
			} else {
				proc.setString(27, null);
			}

			if (catalogId != null && catalogId.trim().length() > 0) {
				proc.setString(28, catalogId);
			} else {
				proc.setString(28, null);
			}

			if (sdpCustId != null && sdpCustId.trim().length() > 0) {
				proc.setString(29, sdpCustId);
			} else {
				proc.setString(29, null);
			}

			if (cnclRsnCode != null && cnclRsnCode.trim().length() > 0) {
				proc.setString(30, cnclRsnCode);
			} else {
				proc.setString(30, null);
			}

			if (ordStatCde != null && ordStatCde.trim().length() > 0) {
				proc.setString(31, ordStatCde);
			} else {
				proc.setString(31, null);
			}

			if (errorCde != null && errorCde.trim().length() > 0) {
				proc.setString(32, errorCde);
			} else {
				proc.setString(32, null);
			}

			// Logger.log(proc.toString());
			proc.execute();
			return proc.getString(1);

		} finally {
			try {
				proc.close();
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}
	}

	/**
	 * Inserts data in SDP_CUSTOMER table this table contains all the
	 * information of a customer with sdpCustId as a primary key.
	 * 
	 * Throws SDPInternalException for catching all the exception in SDP.
	 * 
	 * @param conn
	 * @param sdpCustId
	 * @param custId
	 * @param custMailTxt
	 * @param lastName
	 * @param firstName
	 * @param midName
	 * @param addrLn1
	 * @param addrLn2
	 * @param bldAddrLblTxt
	 * @param city
	 * @param country
	 * @param ccExpDt
	 * @param ccName
	 * @param ccNum
	 * @param ccType
	 * @param phNo
	 * @param phAddrLbl
	 * @param pstCode
	 * @param stateCde
	 * @param dlvrEmailTxt
	 * @param recStatCde
	 * @return
	 * @throws SDPInternalException
	 */
	public String insertSDPCustomer(Connection conn, String sdpCustId,
			String custId, String custMailTxt, String lastName,
			String firstName, String midName, String addrLn1, String addrLn2,
			String bldAddrLblTxt, String city, String country, String ccExpDt,
			String ccName, String ccNum, String ccType, String phNo,
			String phAddrLbl, String pstCode, String stateCde,
			String dlvrEmailTxt, String recStatCde, String rewardZoneId)
			throws SDPInternalException, SQLException {

		CallableStatement proc = null;

		try {

			proc = conn
					.prepareCall("{call RTA.insertCustomerData(?,?, ?, ?, ?,?,?, ?, ?, ?,?,?, ?, ?, ?,?,?, ?, ?, ?, ?) }");

			proc.registerOutParameter(1, OracleTypes.VARCHAR);

			if (custId != null && custId.trim().length() > 0) {
				proc.setString(2, custId);
			} else {
				proc.setString(2, null);
			}

			if (custMailTxt != null && custMailTxt.trim().length() > 0) {
				proc.setString(3, custMailTxt);
			} else {
				proc.setString(3, null);
			}

			if (lastName != null && lastName.trim().length() > 0) {
				proc.setString(4, lastName);
			} else {
				proc.setString(4, null);
			}

			if (firstName != null && firstName.trim().length() > 0) {
				proc.setString(5, firstName);
			} else {
				proc.setString(5, null);
			}

			if (midName != null && midName.trim().length() > 0) {
				proc.setString(6, midName);
			} else {
				proc.setString(6, null);
			}

			if (addrLn1 != null && addrLn1.trim().length() > 0) {
				proc.setString(7, addrLn1);
			} else {
				proc.setString(7, null);
			}

			if (addrLn2 != null && addrLn2.trim().length() > 0) {
				proc.setString(8, addrLn2);
			} else {
				proc.setString(8, null);
			}

			if (bldAddrLblTxt != null && bldAddrLblTxt.trim().length() > 0) {
				proc.setString(9, bldAddrLblTxt);
			} else {
				proc.setString(9, null);
			}

			if (city != null && city.trim().length() > 0) {
				proc.setString(10, city);
			} else {
				proc.setString(10, null);
			}

			if (country != null && country.trim().length() > 0) {
				proc.setString(11, country);
			} else {
				proc.setString(11, null);
			}

			if (ccExpDt != null && ccExpDt.trim().length() > 0) {
				proc.setString(12, ccExpDt);
			} else {
				proc.setString(12, null);
			}

			if (ccName != null && ccName.trim().length() > 0) {
				proc.setString(13, ccName);
			} else {
				proc.setString(13, null);
			}

			if (ccNum != null && ccNum.trim().length() > 0) {
				proc.setString(14, ccNum);
			} else {
				proc.setString(14, null);
			}

			if (ccType != null && ccType.trim().length() > 0) {
				proc.setString(15, ccType);
			} else {
				proc.setString(15, null);
			}

			if (phNo != null && phNo.trim().length() > 0) {
				proc.setString(16, phNo);
			} else {
				proc.setString(16, null);
			}

			if (phAddrLbl != null && phAddrLbl.trim().length() > 0) {
				proc.setString(17, phAddrLbl);
			} else {
				proc.setString(17, null);
			}

			if (pstCode != null && pstCode.trim().length() > 0) {
				proc.setString(18, pstCode);
			} else {
				proc.setString(18, null);
			}

			if (stateCde != null && stateCde.trim().length() > 0) {
				proc.setString(19, stateCde);
			} else {
				proc.setString(19, null);
			}

			if (dlvrEmailTxt != null && dlvrEmailTxt.trim().length() > 0) {
				proc.setString(20, dlvrEmailTxt);
			} else {
				proc.setString(20, null);
			}

			if (rewardZoneId != null && rewardZoneId.trim().length() > 0) {
				proc.setString(21, rewardZoneId);
			} else {
				proc.setString(21, null);
			}

			proc.execute();

			return proc.getString(1);

		} finally {
			try {
				proc.close();
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}
	}

	/**
	 * Inserts data in SDP_ORDER_DETAILS table this table contains detail
	 * information of orders with sdpOrderId as a primary key.
	 * 
	 * Throws SDPInternalException for catching all the exception in SDP
	 * 
	 * @param conn
	 * @param sdpOrderId
	 * @param attrKey
	 * @param attrValue
	 * @param recStatCode
	 * @throws SDPInternalException
	 */
	public void insertSDPOrderDetails(Connection conn, String sdpOrderId,
			String attrKey, String attrValue) throws SDPInternalException,
			SQLException {

		CallableStatement proc = null;

		try {

			proc = conn.prepareCall("{call RTA.insertOrderDetails(?,?,?) }");

			if (sdpOrderId != null && sdpOrderId.trim().length() > 0) {
				proc.setString(1, sdpOrderId);
			} else {
				proc.setString(1, null);
			}

			if (attrKey != null && attrKey.trim().length() > 0) {
				proc.setString(2, attrKey.trim());
			} else {
				proc.setString(2, null);
			}

			if (attrValue != null && attrValue.trim().length() > 0) {
				proc.setString(3, attrValue.trim());
			} else {
				proc.setString(3, null);
			}

			proc.execute();

		} finally {
			try {
				proc.close();
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}
	}

	public String getNextKey(Connection conn, String key)
			throws SDPInternalException, SQLException {
		PreparedStatement ps = null;
		String sqlCommand = null;
		ResultSet rs = null;

		try {

			if (SdpDBConstants.CUSTOMER_KEY.equals(key)) {
				sqlCommand = "SELECT SDP_CUST_ID_SEQ.nextval from dual";
			} else if (SdpDBConstants.SDP_ORDER_KEY.equals(key)) {
				sqlCommand = "SELECT SDP_ORDER_ID_SEQ.nextval from dual";
			}

			ps = conn.prepareStatement(sqlCommand);
			rs = ps.executeQuery();

			if (rs != null && rs.next()) {
				return rs.getString(1);
			} else {
				throw new SDPInternalException("",
						"Error out while fetching next available value for "
								+ key + " Sequence");
			}

		} catch (SQLException e) {
			Logger.logStackTrace(e.fillInStackTrace());
			throw new SQLException("", "SQLException :: " + e.getMessage());
		}
	}

	public CreateOrderResponseDocument generateResponse(String requestStatus,
			String errorCde, String errorDscp, String sdpOrderId,
			String sdpCustId, BBYOrder bbyOrder) {

		CreateOrderResponseDocument responseDocument = CreateOrderResponseDocument.Factory
				.newInstance();
		CreateOrderResponse orderResponse = responseDocument
				.addNewCreateOrderResponse();

		ServiceResult serviceResult = null;
		serviceResult = orderResponse.addNewServiceResult();

		orderResponse.setActivationOrderID(sdpOrderId);
		if (sdpCustId != null) {
			orderResponse.setSDPCustomerId(sdpCustId);
		}

		// check status code
		if (requestStatus != null) {

			if (requestStatus
					.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_SUCCESS)) {

				serviceResult.setStatusCode(BigInteger.ZERO);

			} else if (requestStatus
					.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_FAILURE)) {
				orderResponse.addNewBBYOrder();
				orderResponse.setBBYOrder(bbyOrder);
				serviceResult.setStatusCode(BigInteger.ONE);
				serviceResult.setErrorCode(errorCde);
				ErrorDetailList detail = serviceResult.addNewErrorDetailList();
				ErrorDetail errorDtl = detail.addNewErrorDetail();
				errorDtl.setErrorCode(errorCde);
				errorDtl.setMoreDetail(errorDscp);
				ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
				errorDsp.setOriginal(errorDscp);
				serviceResult.setErrorSeverity("critical");

			} else if (requestStatus
					.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_DUPLICATE)) {

				orderResponse.addNewBBYOrder();

				orderResponse.setBBYOrder(bbyOrder);

				serviceResult.setStatusCode(BigInteger.valueOf(2));
				serviceResult.setErrorCode(errorCde);
				ErrorDetailList detail = serviceResult.addNewErrorDetailList();
				ErrorDetail errorDtl = detail.addNewErrorDetail();
				errorDtl.setErrorCode(errorCde);
				errorDtl.setMoreDetail(errorDscp);
				ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
				errorDsp.setOriginal(errorDscp);

			}

		}

		return responseDocument;
	}

	public void insertSDPRequestResponseLog(Connection conn, String lineItemId,
			String sdpOrderId, String sdpId, String rqstType, String profileId,
			String srcSysId, String rqstMsg, String respMsg,
			String transactionStatusCode) throws SDPInternalException, SQLException {

		CallableStatement proc = null;

		try {

			proc = conn
					.prepareCall("{call RTA.insertSDPRequestResponseLog(?, ?, ?, ?, ?, ?, ?, ?, ?) }");

			if (lineItemId != null && lineItemId.trim().length() > 0) {
				proc.setString(1, lineItemId);
			} else {
				proc.setString(1, null);
			}

			if (sdpOrderId != null && sdpOrderId.trim().length() > 0) {
				proc.setString(2, sdpOrderId);
			} else {
				proc.setString(2, null);
			}

			if (sdpId != null && sdpId.trim().length() > 0) {
				proc.setString(3, sdpId);
			} else {
				proc.setString(3, null);
			}

			if (rqstType != null && rqstType.trim().length() > 0) {
				proc.setString(4, rqstType);
			} else {
				proc.setString(4, null);
			}

			if (profileId != null && profileId.trim().length() > 0) {
				proc.setString(5, profileId);
			} else {
				proc.setString(5, "1");
			}

			if (srcSysId != null && srcSysId.trim().length() > 0) {
				proc.setString(6, srcSysId);
			} else {
				proc.setString(6, null);
			}

			if (rqstMsg != null && rqstMsg.trim().length() > 0) {
				proc.setString(7, rqstMsg);
			} else {
				proc.setString(7, null);
			}

			if (respMsg != null && respMsg.trim().length() > 0) {
				proc.setString(8, respMsg);
			} else {
				proc.setString(8, null);
			}

			if (transactionStatusCode != null && transactionStatusCode.trim().length() > 0) {
				proc.setString(9, transactionStatusCode);
			} else {
				proc.setString(9, null);
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

	// Note date format to be in yyyy-MM-dd
	private java.sql.Date stringToDateConvert(String date)
			throws SDPInternalException {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date sqltDate = null;
		try {
			java.util.Date parsedUtilDate = formatter.parse(date);
			sqltDate = new java.sql.Date(parsedUtilDate.getTime());

		} catch (ParseException e) {
			Logger.logStackTrace(e.fillInStackTrace());
			throw new SDPInternalException(" ",
					"Please valid timestamp - >yyyy-MM-dd");

		}
		return sqltDate;
	}

	// Note timestamp format to be in yyyy-MM-dd
	private java.sql.Timestamp stringToTimestampConvert(String date)
			throws SDPInternalException {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Timestamp sqlTimstamp = null;

		date = date.replace("T", " ");
		try {
			java.util.Date parsedUtilDate = formatter.parse(date);
			sqlTimstamp = new Timestamp(parsedUtilDate.getTime());
		} catch (ParseException e) {
			Logger.logStackTrace(e.fillInStackTrace());
			throw new SDPInternalException(" ",
					"Please valid timestamp - >yyyy-MM-dd hh:mm:ss ");

		}
		return sqlTimstamp;
	}

	/**
	 * returns RetrieveOrderVO
	 * 
	 * @param conn
	 * @param lineItemId
	 * @param bbyOrder
	 * @return
	 * @throws SDPInternalException
	 * @throws SQLException
	 */
	public RetrieveOrderVO retrieveSDPOrderDetails(Connection conn,
			String lineItemId) throws SDPInternalException, SQLException {

		CallableStatement proc = null;

		ResultSet rs = null;

		RetrieveOrderVO retrieveOrderVO = null;

		proc = conn.prepareCall("{call RTA.retrieveSDPOrderDetails(?,?) }");

		if (lineItemId != null && lineItemId.trim().length() > 0) {
			proc.setString(1, lineItemId);
		} else {
			proc.setString(1, null);
		}

		proc.registerOutParameter(2, OracleTypes.CURSOR);

		proc.executeQuery();

		rs = (ResultSet) proc.getObject(2);

		if (rs != null && rs.next()) {

			retrieveOrderVO = new RetrieveOrderVO();

			retrieveOrderVO.setSdpOrderId(rs.getString("SDP_ORDER_ID"));

			retrieveOrderVO.setOrderStatus(rs.getString("ORD_STAT_CDE"));

			retrieveOrderVO.setSdpId(rs.getString("SDP_ID"));

			if (rs.getString("SDP_CUST_ID") != null) {
				retrieveOrderVO.setSdpCustomerId(rs.getString("SDP_CUST_ID"));

			}

			if (rs.getString("KEY_CDE") != null) {
				retrieveOrderVO.setKeyCode(rs.getString("KEY_CDE"));

			}

			if (rs.getString("CONF_ID") != null) {
				retrieveOrderVO.setConfirmationCode(rs.getString("CONF_ID"));

			}
			
			if (rs.getString("BSNS_KEY") != null) {
				retrieveOrderVO.setBusinessKey(rs.getString("BSNS_KEY"));

			}
			
			if (rs.getString("BSNS_KEY_TYP") != null) {
				retrieveOrderVO.setBusinessKeyType(rs.getString("BSNS_KEY_TYP"));

			}

		}

		return retrieveOrderVO;

	}

	// public int retrieveSDPCustomerDetails(Connection conn, String sdpOrderId,
	// BBYOrder bbyOrder) throws SDPInternalException {
	//
	// PreparedStatement proc = null;
	//
	// String sqlCommand = null;
	//
	// ResultSet rs = null;
	//
	// try {
	// sqlCommand = "SELECT SDP_CUST_ID FROM SDP_ORDER WHERE SDP_ORDER_ID = ?";
	//
	// proc = conn.prepareStatement(sqlCommand);
	// proc.setString(1, sdpOrderId);
	//
	// rs = proc.executeQuery();
	//
	// if (rs != null && rs.next()) {
	//
	// bbyOrder.getCustomer().setC4CustomerID(rs.getString(1));
	// return rs.getInt("SDP_CUST_ID");
	// }
	//
	// return -1;
	//
	// } catch(SQLException e) {
	// Logger.logStackTrace(e.fillInStackTrace());
	// throw new SDPInternalException("", "SQLException :: " +e.getMessage());
	// }
	// }

}

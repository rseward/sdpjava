package com.bestbuy.sdp.order.helper;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import oracle.jdbc.driver.OracleTypes;

import com.accenture.xml.sdp.bby.om.bbyOrder.BBYOrder;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail.ErrorDescription;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderResponseDocument.RetrieveOrderResponse;
import com.bestbuy.schemas.sdp.db.retrieveOrderByAttributes.RetrieveOrderByAttributesResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrderByAttributes.RetrieveOrderByAttributesResponseDocument.RetrieveOrderByAttributesResponse;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.services.SdpDBConstants;
import com.bestbuy.sdp.services.exception.SDPInternalException;

public class RetrieveOrderHelper {

	public RetrieveOrderResponseDocument retrieveOrder(Connection conn,
			String searchCriteria, String searchValue)
			throws SDPInternalException {

		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		boolean hasError = false;
		boolean isLineItemIdSearch = false;

		boolean isContractIdSearch = false;

		ServiceResult svResult = ServiceResult.Factory.newInstance();
		RetrieveOrderResponseDocument response = RetrieveOrderResponseDocument.Factory
				.newInstance();
		RetrieveOrderResponse rtrOdrResponse = response
				.addNewRetrieveOrderResponse();

		if ("LineItemID".equals(searchCriteria)) {
			isLineItemIdSearch = true;
		} else if ("ContractID".equals(searchCriteria)) {
			isContractIdSearch = true;
		} else {
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = "20040006";
			errorDscp = "Invalid  Search Criteria  --> LineItemID or ContractID.";
			response = generateRetrieveOrderResponse(reqStatus, svErrorCde,
					errorDscp);
			// Logger.log(response);
			return response;
		}

		CallableStatement proc = null;
		ResultSet rs = null;

		BBYOrder bbyOrder = null;

		try {
			if (isLineItemIdSearch) {
				proc = conn
						.prepareCall("{call RTA.retrieveOrderByLineItemId(?,?) }");
			} else if (isContractIdSearch) {
				proc = conn
						.prepareCall("{call RTA.retrieveOrderByContractId(?,?) }");
			}

			proc.setString(1, searchValue);

			proc.registerOutParameter(2, OracleTypes.CURSOR);
			proc.executeQuery();
			rs = (ResultSet) proc.getObject(2);

			boolean foundRecords = false;

			if (rs != null) {
				if (rs.next()) {
					ConstructBBYOrder constructBBYOrder = new ConstructBBYOrder();
					bbyOrder = constructBBYOrder.consBBYOrder(rs);
					if (bbyOrder != null) {
						foundRecords = true;
						hasError = false;
					}
				}

			}
			if (foundRecords) {
				conn.commit();
				svResult.setStatusCode(BigInteger.ZERO);
				rtrOdrResponse.setServiceResult(svResult);
			} else {
				hasError = true;
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20040007";
				errorDscp = "Method = retrieveOrder() :: Order Not found :: "
						+ searchValue;
			}
		} catch (SQLException e) {
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = "20040016";
			errorDscp = "Method = retrieveOrder() :: SDP DB issue. "
					+ e.getMessage();
		}
		// build service result
		if (!hasError) {
			rtrOdrResponse.setBBYOrder(bbyOrder);
		} else {
			response = generateRetrieveOrderResponse(reqStatus, svErrorCde,
					errorDscp);
		}
		// Logger.log(response);
		return response;
	}

	public RetrieveOrderByAttributesResponseDocument retrieveOrderByAttributes(
			String transId, String transDt, String storeId, String regId,
			String lineId, String sku, String serialNum)
			throws SDPInternalException {

		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		boolean foundOrder = false;
		int i = 0;

		Connection conn = null;

		ServiceResult svResult = ServiceResult.Factory.newInstance();
		RetrieveOrderByAttributesResponseDocument response = RetrieveOrderByAttributesResponseDocument.Factory
				.newInstance();
		RetrieveOrderByAttributesResponse rtrOdrResponse = response
				.addNewRetrieveOrderByAttributesResponse();

		CallableStatement proc = null;
		ResultSet rs = null;

		try {
			conn = RuntimeEnvironment.getConn();
			if(storeId.equalsIgnoreCase("960") && serialNum != null){
				proc = conn
				.prepareCall("{call RTA.retrieveOrderByStrIdSerialNum(?,?,?)}");
				proc.setString(1, storeId);
				proc.setString(2, serialNum);
				proc.registerOutParameter(3, OracleTypes.CURSOR);
				proc.executeQuery();

				rs = (ResultSet) proc.getObject(3);
				
			}else if (lineId != null && lineId.trim().length() > 0) {
				proc = conn
						.prepareCall("{call RTA.retrieveOrderByFivePartKey(?,?,?,?,?,?)}");

				proc.setString(1, transId);
				proc.setDate(2, stringToDateConvert(transDt));
				proc.setString(3, storeId);
				proc.setString(4, regId);
				proc.setString(5, lineId);
				proc.registerOutParameter(6, OracleTypes.CURSOR);
				proc.executeQuery();

				rs = (ResultSet) proc.getObject(6);

			} else if (lineId == null
					&& (sku != null && sku.trim().length() > 0)) {
				if (serialNum != null && serialNum.trim().length() > 0) {

					proc = conn
							.prepareCall("{call RTA.retrieveOrderBy4pkSkuSerialNum(?,?, ?,?,?,?,?)}");
					proc.setString(1, transId);
					proc.setDate(2, stringToDateConvert(transDt));
					proc.setString(3, storeId);
					proc.setString(4, regId);
					proc.setString(5, sku);
					proc.setString(6, serialNum);
					proc.registerOutParameter(7, OracleTypes.CURSOR);
					proc.executeQuery();
					rs = (ResultSet) proc.getObject(7);

				} else {
					proc = conn
							.prepareCall("{call RTA.retrieveOrderBy4pkSku(?,?, ?,?,?,?) }");
					proc.setString(1, transId);
					proc.setDate(2, stringToDateConvert(transDt));
					proc.setString(3, storeId);
					proc.setString(4, regId);
					proc.setString(5, sku);
					proc.registerOutParameter(6, OracleTypes.CURSOR);
					proc.executeQuery();
					rs = (ResultSet) proc.getObject(6);
				}

			} else {
				throw new SDPInternalException("20040006",
						"Method = retrieveOrderByAttributes() :: Invalid SearchCriteria");
			}

			if (rs != null) {

				while (rs.next()) {
					foundOrder = true;
					BBYOrder bbyOrder = null;
					ConstructBBYOrder constructBBYOrder = new ConstructBBYOrder();
					bbyOrder = constructBBYOrder.consBBYOrder(rs);
					rtrOdrResponse.addNewBBYOrder();
					rtrOdrResponse.setBBYOrderArray(i, bbyOrder);
					i++;
				}

			}

			if (foundOrder) {

				svResult.setStatusCode(BigInteger.ZERO);
				rtrOdrResponse.setServiceResult(svResult);

			} else {
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20040007";
				errorDscp = " Order Not found :: ";
				response = generateRetrieveOrderByAttributesResponse(reqStatus,
						svErrorCde, errorDscp);
			}

			if (conn != null) {
				conn.close();
			}

		} catch (SQLException e) {
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = "20040016";
			errorDscp = "Method = retrieveOrderByAttributes() :: SDP DB issue. "
					+ e.getMessage();
			response = generateRetrieveOrderByAttributesResponse(reqStatus,
					svErrorCde, errorDscp);
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return response;
	}

	public static RetrieveOrderResponseDocument generateRetrieveOrderResponse(
			String reqStatus, String svErrorCde, String errorDscp) {

		RetrieveOrderResponseDocument response = RetrieveOrderResponseDocument.Factory
				.newInstance();
		RetrieveOrderResponse rtrOdrResponse = response
				.addNewRetrieveOrderResponse();

		ServiceResult serviceResult = null;
		serviceResult = rtrOdrResponse.addNewServiceResult();

		// check status code
		if (reqStatus != null) {
			if (reqStatus.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_SUCCESS)) {
				serviceResult.setStatusCode(BigInteger.ZERO);
			} else if (reqStatus
					.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_FAILURE)) {
				serviceResult.setStatusCode(BigInteger.ONE);
				serviceResult.setErrorCode(svErrorCde);
				ErrorDetailList detail = serviceResult.addNewErrorDetailList();
				ErrorDetail errorDtl = detail.addNewErrorDetail();
				errorDtl.setErrorCode(svErrorCde);
				errorDtl.setMoreDetail(errorDscp);
				ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
				errorDsp.setOriginal(errorDscp);
			}
		} else {
			serviceResult.setStatusCode(BigInteger.ONE);
			serviceResult.setErrorCode(svErrorCde);
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode(svErrorCde);
			errorDtl.setMoreDetail(errorDscp);
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(errorDscp);
		}
		return response;
	}

	public static RetrieveOrderByAttributesResponseDocument generateRetrieveOrderByAttributesResponse(
			String reqStatus, String svErrorCde, String errorDscp) {

		RetrieveOrderByAttributesResponseDocument response = RetrieveOrderByAttributesResponseDocument.Factory
				.newInstance();
		RetrieveOrderByAttributesResponse rtrOdrResponse = response
				.addNewRetrieveOrderByAttributesResponse();

		ServiceResult serviceResult = null;
		serviceResult = rtrOdrResponse.addNewServiceResult();

		// check status code
		if (reqStatus != null) {
			if (reqStatus.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_SUCCESS)) {
				serviceResult.setStatusCode(BigInteger.ZERO);
			} else if (reqStatus
					.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_FAILURE)) {
				serviceResult.setStatusCode(BigInteger.ONE);
				serviceResult.setErrorCode(svErrorCde);
				ErrorDetailList detail = serviceResult.addNewErrorDetailList();
				ErrorDetail errorDtl = detail.addNewErrorDetail();
				errorDtl.setErrorCode(svErrorCde);
				errorDtl.setMoreDetail(errorDscp);
				ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
				errorDsp.setOriginal(errorDscp);
			}
		} else {
			serviceResult.setStatusCode(BigInteger.ONE);
			serviceResult.setErrorCode(svErrorCde);
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode(svErrorCde);
			errorDtl.setMoreDetail(errorDscp);
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(errorDscp);
		}
		return response;
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
			throw new SDPInternalException("20040006",
					"Please valid timestamp - >yyyy-MM-dd");

		}
		return sqltDate;
	}

}

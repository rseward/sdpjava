package com.bestbuy.sdp.subscription;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.accenture.common.ex.ApplicationException;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail.ErrorDescription;
import com.bestbuy.schemas.sdp.db.lookupVendorID.LookupVendorIDRequestDocument;
import com.bestbuy.schemas.sdp.db.lookupVendorID.LookupVendorIDResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderResponseDocument.RetrieveOrderResponse;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.services.exception.SDPInternalException;

public class CSMSubscription {

	public RetrieveOrderResponseDocument retrieveSubscription(String keyType,
			String keyValue) throws NamingException, SQLException {

		Connection conn = RuntimeEnvironment
				.getConn(RuntimeEnvironment.CNF_CSM_DATASOURCE_JNDI_NAME);

		CSMDbHelper helper = new CSMDbHelper();

		RetrieveOrderResponseDocument responseDoc = null;

		try {
			responseDoc = helper.retrieveOrder(conn, keyType, keyValue, true);
			RuntimeEnvironment.releaseConn(conn);
		} catch (SDPInternalException e) {
			
			ServiceResult serviceResult = ServiceResult.Factory.newInstance();
			responseDoc = RetrieveOrderResponseDocument.Factory.newInstance();

			RetrieveOrderResponse rtrOdrResponse = responseDoc
					.addNewRetrieveOrderResponse();

			serviceResult.setStatusCode(BigInteger.ONE);
			serviceResult.setErrorCode(e.getErrorCode());
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode(e.getErrorCode());
			errorDtl.setMoreDetail(e.getErrorMessage() + " :: "
					+ e.getMessage());
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(e.getErrorMessage() + " :: " + e.getMessage());
			rtrOdrResponse.setServiceResult(serviceResult);
			RuntimeEnvironment.releaseConn(conn);
		}finally {
			RuntimeEnvironment.releaseConn(conn);
		}

		return responseDoc;

	}

	public RetrieveOrderResponseDocument migrateSubscription(String keyType,
			String keyValue) throws NamingException, SQLException {

		Connection conn = RuntimeEnvironment
				.getConn(RuntimeEnvironment.CNF_CSM_DATASOURCE_JNDI_NAME);

		CSMDbHelper helper = new CSMDbHelper();

		RetrieveOrderResponseDocument responseDoc = null;

		try {
			responseDoc = helper.retrieveOrder(conn, keyType, keyValue, true);
			RuntimeEnvironment.releaseConn(conn);
		} catch (SDPInternalException e) {
			e.printStackTrace();
			ServiceResult serviceResult = ServiceResult.Factory.newInstance();
			responseDoc = RetrieveOrderResponseDocument.Factory.newInstance();

			RetrieveOrderResponse rtrOdrResponse = responseDoc
					.addNewRetrieveOrderResponse();

			serviceResult.setStatusCode(BigInteger.ONE);
			serviceResult.setErrorCode(e.getErrorCode());
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode(e.getErrorCode());
			errorDtl.setMoreDetail(e.getErrorMessage() + " :: "
					+ e.getMessage());
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(e.getErrorMessage() + " :: " + e.getMessage());
			rtrOdrResponse.setServiceResult(serviceResult);
			RuntimeEnvironment.releaseConn(conn);
		} finally {
			RuntimeEnvironment.releaseConn(conn);
		}

		return responseDoc;

	}

	/*
	 * Commented by Deepak Khetan. Created new method to refer xml input an
	 * output. public String retrieveVendorIdByContractID(String contractID,
	 * String serialNum) {
	 * 
	 * CSMDbHelper helper = new CSMDbHelper();
	 * 
	 * String vendorID = null; try { vendorID =
	 * helper.retrieveVendorIdByContractID(contractID, serialNum); } catch
	 * (SDPInternalException e) { e.printStackTrace(); } return vendorID; }
	 */

	@SuppressWarnings("deprecation")
	public LookupVendorIDResponseDocument retrieveVendorIdByContractID(
			XmlObject xmlRequestDoc) throws ApplicationException {

		LookupVendorIDResponseDocument response = null;
		LookupVendorIDRequestDocument requestDoc = null;
		CSMDbHelper helper = new CSMDbHelper();

		// String vendorID = null;

		try {
			// Parse Request
			if (xmlRequestDoc != null) {
				requestDoc = LookupVendorIDRequestDocument.Factory
						.parse(xmlRequestDoc.toString());

			} else {
				throw new SDPInternalException("20040001",
						"Request message is null", null);
			}

			response = helper.retrieveVendorIdByContractID(requestDoc);
		} catch (XmlException xmle) {
			Logger.logStackTrace(xmle.fillInStackTrace());
			throw new ApplicationException("Error while parsing xml document. "
					+ xmlRequestDoc, 1);
		} catch (SDPInternalException e) {
			Logger.logStackTrace(e.fillInStackTrace());
			throw new ApplicationException("" + e.getMessage(), 1);
		}
		return response;
	}
}

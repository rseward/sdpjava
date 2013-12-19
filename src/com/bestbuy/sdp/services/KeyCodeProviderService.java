package com.bestbuy.sdp.services;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.accenture.common.ex.ApplicationException;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.bestbuy.schemas.sdp.tpa.kcb.ConditionsType;
import com.bestbuy.schemas.sdp.tpa.kcb.DataType;
import com.bestbuy.schemas.sdp.tpa.kcb.KeyCodeAttributeType;
import com.bestbuy.schemas.sdp.tpa.kcb.KeyCodeAttributesType;
import com.bestbuy.schemas.sdp.tpa.kcb.ReleaseKeyCodeRequest;
import com.bestbuy.schemas.sdp.tpa.kcb.ReleaseKeyCodeRequestDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.ReleaseKeyCodeResponse;
import com.bestbuy.schemas.sdp.tpa.kcb.ReleaseKeyCodeResponseDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeRequest;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeRequestDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeResponse;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeResponseDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.ReservedKeyCodeType;
import com.bestbuy.schemas.sdp.tpa.kcb.UpdateKeyCodeRequest;
import com.bestbuy.schemas.sdp.tpa.kcb.UpdateKeyCodeRequestDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.UpdateKeyCodeResponse;
import com.bestbuy.schemas.sdp.tpa.kcb.UpdateKeyCodeResponseDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeRequest.Status.Enum;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.kcb.DefaultKeyCodeProvider;
import com.bestbuy.sdp.kcb.KeyCode;
import com.bestbuy.sdp.kcb.KeyCodeProvider;
import com.bestbuy.sdp.kcb.KeyCode.OrdStatus;
import com.bestbuy.sdp.services.exception.SDPInternalException;

public class KeyCodeProviderService {
	public static final ReserveKeyCodeResponseDocument reserveKeyCode(
			XmlObject requestXmlObject) {
		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		boolean hasError = false;

		KeyCodeProvider kcProvider = new DefaultKeyCodeProvider();

		ReserveKeyCodeRequestDocument xmlRequestDoc = null;
		ReserveKeyCodeResponseDocument xmlResponseDoc =
			ReserveKeyCodeResponseDocument.Factory
				.newInstance();

		ReserveKeyCodeResponse xmlResponse = xmlResponseDoc
				.addNewReserveKeyCodeResponse();
		ServiceResult svResult = xmlResponse.addNewServiceResult();
		try {
			try {
				xmlRequestDoc = ReserveKeyCodeRequestDocument.Factory
						.parse(requestXmlObject.xmlText());
			} catch (XmlException xmle) {
				throw new SDPInternalException("",
						"ReserveKeyCode Failed : Error while parsing");
			}
			String orderID = xmlRequestDoc.getReserveKeyCodeRequest()
					.getOrderID();
			String productID = xmlRequestDoc.getReserveKeyCodeRequest()
					.getProductSKU();
			String vendorID = xmlRequestDoc.getReserveKeyCodeRequest()
					.getVendorID();

			Enum status = xmlRequestDoc.getReserveKeyCodeRequest().getStatus();
			KeyCode keyCode = null;
			if ((vendorID != null && vendorID.trim().length() > 0)
					&& (productID != null && productID.trim().length() > 0)
					&& (orderID != null && orderID.trim().length() > 0)) {
				keyCode = kcProvider.reserve(vendorID, productID, orderID);
			}// Adds error code in serviceResult of response if KeyCode /
				// vendorID / SKU is/are null and return the response
			else {
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20030004";
				errorDscp = "ReserveKeyCode failed : VendorID / ProductID / SKU  is/are mandatory. ";
				xmlResponseDoc = DefaultKeyCodeProvider
						.generateReserveKeyResponse(reqStatus, svErrorCde,
								errorDscp);
				//Logger.log(xmlResponseDoc);
				return xmlResponseDoc;
			}
			if (status != null
					&& ReserveKeyCodeRequest.Status.ACTIVATED.intValue() == status
							.intValue()) {
				keyCode.setKeyCodeActual(keyCode.getValue());
				keyCode.setStatus("ACTIVATED");
				kcProvider.update(keyCode);
			}

			// Adds error code in serviceResult of response if the KCB is
			// depleted or cannot otherwise respond and return the response
			if (keyCode == null || keyCode.getValue() == null
					|| keyCode.getValue().length() == 0) {
				// TODO: Determine errorCode to KCB depletion, or invoke lookup
				// service
				throw new SDPInternalException(
						"20030001","ReserveKeyCode Failed : Duplicate Order for VendorID = " + vendorID
								+ ", Product = " + productID
								+ ", or cannot otherwise fulfill the request.");
			}

			// Set the reserved keyCode value
			ReservedKeyCodeType xmlKeyCode = xmlResponse.addNewKeyCode();
			xmlKeyCode.setValue(keyCode.getValue());

			// Load the KeyCode Attributes element, if there are any
			if (keyCode.getProperties() != null
					&& keyCode.getProperties().size() > 0) {
				Properties properties = keyCode.getProperties();

				Set<Entry<Object, Object>> propertySet = properties.entrySet();
				Iterator<Entry<Object, Object>> i = propertySet.iterator();

				// Initialize the Attributes XML
				KeyCodeAttributesType xmlAttributes = xmlKeyCode
						.addNewAttributes();
				KeyCodeAttributeType xmlAttribute;

				while (i.hasNext()) {
					Entry<Object, Object> entry = i.next();

					xmlAttribute = xmlAttributes.addNewAttribute();
					xmlAttribute.setName(entry.getKey().toString());
					xmlAttribute.setValue(entry.getValue().toString());
				}
			}
			svResult.setStatusCode(BigInteger.ZERO);
		} catch (ApplicationException e) {
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			errorDscp = "ReserveKeyCode Failed : Error occurred invoking the Key Bank - " +
					"Please check SDP exception logs "
					+ e.getMessage();
			Logger.logStackTrace(e.fillInStackTrace());
		} catch (SQLException e) {
			if (e.getMessage().indexOf("ORA-20002") != -1) {
				hasError = true;
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20030003";
				errorDscp = "ReserveKeyCode Failed : Invalid VendorID/SKU combination.";
			} else if (e.getMessage().indexOf("ORA-20001") != -1) {
				hasError = true;
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20030002";
				errorDscp = "ReserveKeyCode Failed : Supply exhausted for product.";
			} else {
				hasError = true;
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20030008";
				errorDscp = "ReserveKeyCode Failed : SDP DB issue." + e.getMessage();
			}
			Logger.logStackTrace(e.fillInStackTrace());
		}catch(SDPInternalException e){
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = e.getErrorCode();
			errorDscp = e.getErrorMessage();
		}
		if (hasError) {
			xmlResponseDoc = DefaultKeyCodeProvider.generateReserveKeyResponse(
					reqStatus, svErrorCde, errorDscp);
		} else {
			xmlResponse.setServiceResult(svResult);
		}
		//Logger.log(xmlResponseDoc);
		return xmlResponseDoc;
	}
	
	public static final ReserveKeyCodeResponseDocument reserveKeyCodeByMasterItemId(
			XmlObject requestXmlObject) {
		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		boolean hasError = false;

		KeyCodeProvider kcProvider = new DefaultKeyCodeProvider();

		ReserveKeyCodeRequestDocument xmlRequestDoc = null;
		ReserveKeyCodeResponseDocument xmlResponseDoc =
			ReserveKeyCodeResponseDocument.Factory
				.newInstance();

		ReserveKeyCodeResponse xmlResponse = xmlResponseDoc
				.addNewReserveKeyCodeResponse();
		ServiceResult svResult = xmlResponse.addNewServiceResult();
		try {
			try {
				xmlRequestDoc = ReserveKeyCodeRequestDocument.Factory
						.parse(requestXmlObject.xmlText());
			} catch (XmlException xmle) {
				throw new SDPInternalException(
						"","ReserveKeyCodeByMasterItemId Failed : parsing Failed ");
			}
			String orderID = xmlRequestDoc.getReserveKeyCodeRequest()
					.getOrderID();
			String masterItemId = xmlRequestDoc.getReserveKeyCodeRequest()
					.getMasterItemId();
			String vendorID = xmlRequestDoc.getReserveKeyCodeRequest()
					.getVendorID();

			Enum status = xmlRequestDoc.getReserveKeyCodeRequest().getStatus();
			KeyCode keyCode = null;
			if ((vendorID != null && vendorID.trim().length() > 0)
					&& (masterItemId != null && masterItemId.trim().length() > 0)
					&& (orderID != null && orderID.trim().length() > 0)) {
				keyCode = kcProvider.reserveByMasterItemId(vendorID, masterItemId, orderID, status.toString());
			}// Adds error code in serviceResult of response if KeyCode /
				// vendorID / SKU is/are null and return the response
			else {
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20030012";
				errorDscp = "ReserveKeyCodeByMasterItemId Failed : " +
						"VendorID / MasterItemId / OrderId is/are mandatory. ";
				xmlResponseDoc = DefaultKeyCodeProvider
						.generateReserveKeyResponse(reqStatus, svErrorCde,
								errorDscp);
				//Logger.log(xmlResponseDoc);
				return xmlResponseDoc;
			}
			// Commented by Krapa : not required
//			if (status != null
//					&& ReserveKeyCodeRequest.Status.ACTIVATED.intValue() == status
//							.intValue()) {
//				keyCode.setKeyCodeActual(keyCode.getValue());
//				keyCode.setStatus("ACTIVATED");
//				kcProvider.update(keyCode);
//			}

			// Adds error code in serviceResult of response if the KCB is
			// depleted or cannot otherwise respond and return the response
			if (keyCode == null || keyCode.getValue() == null
					|| keyCode.getValue().length() == 0) {
				// TODO: Determine errorCode to KCB depletion, or invoke lookup
				// service
				throw new SDPInternalException(
						"20030009","ReserveKeyCodeByMasterItemId Failed : Duplicate Order for VendorID = " 
						+ vendorID+ ", MasterItemId = " + masterItemId
								+ ", or cannot otherwise fulfill the request.");
			}

			// Set the reserved keyCode value
			ReservedKeyCodeType xmlKeyCode = xmlResponse.addNewKeyCode();
			xmlKeyCode.setValue(keyCode.getValue());

			// Load the KeyCode Attributes element, if there are any
			if (keyCode.getProperties() != null
					&& keyCode.getProperties().size() > 0) {
				Properties properties = keyCode.getProperties();

				Set<Entry<Object, Object>> propertySet = properties.entrySet();
				Iterator<Entry<Object, Object>> i = propertySet.iterator();

				// Initialize the Attributes XML
				KeyCodeAttributesType xmlAttributes = xmlKeyCode
						.addNewAttributes();
				KeyCodeAttributeType xmlAttribute;

				while (i.hasNext()) {
					Entry<Object, Object> entry = i.next();

					xmlAttribute = xmlAttributes.addNewAttribute();
					xmlAttribute.setName(entry.getKey().toString());
					xmlAttribute.setValue(entry.getValue().toString());
				}
			}
			svResult.setStatusCode(BigInteger.ZERO);
		} catch (ApplicationException e) {
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			errorDscp = "ReserveKeyCodeByMasterItemId Failed : " +
					"Error occurred invoking the Key Bank - Please check SDP exception logs "
					+ e.getMessage();
			Logger.logStackTrace(e.fillInStackTrace());
		} catch (SQLException e) {
			if (e.getMessage().indexOf("ORA-20002") != -1) {
				hasError = true;
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20030011";
				errorDscp = "ReserveKeyCodeByMasterItemId Failed : Invalid VendorID/MasterItemId combination.";
			} else if (e.getMessage().indexOf("ORA-20001") != -1) {
				hasError = true;
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20030010";
				errorDscp = "ReserveKeyCodeByMasterItemId Failed : Supply exhausted for product.";
			} else {
				hasError = true;
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20030008";
				errorDscp = "ReserveKeyCodeByMasterItemId Failed : SDP DB issue." + e.getMessage();
			}
			Logger.logStackTrace(e.fillInStackTrace());
		}catch(SDPInternalException e){
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = e.getErrorCode();
			errorDscp = e.getErrorMessage();
		}
		if (hasError) {
			xmlResponseDoc = DefaultKeyCodeProvider.generateReserveKeyResponse(
					reqStatus, svErrorCde, errorDscp);
		} else {
			xmlResponse.setServiceResult(svResult);
		}
		//Logger.log(xmlResponseDoc);
		return xmlResponseDoc;
	}

	public static final UpdateKeyCodeResponseDocument updateKeyCode(
			XmlObject requestXmlObject) {
		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		boolean hasError = false;

		UpdateKeyCodeRequestDocument xmlRequestDoc = null;
		UpdateKeyCodeResponseDocument xmlResponseDoc = UpdateKeyCodeResponseDocument.Factory
				.newInstance();
		UpdateKeyCodeResponse xmlResponse = xmlResponseDoc
				.addNewUpdateKeyCodeResponse();
		ServiceResult svResult = xmlResponse.addNewServiceResult();
		try {
			try {
				xmlRequestDoc = UpdateKeyCodeRequestDocument.Factory
						.parse(requestXmlObject.xmlText());
			} catch (XmlException xmle) {
				throw new ApplicationException(
						"Input request is not of the type UpdateKeyCodeRequestDocument",
						1);
			}
			UpdateKeyCodeRequest xmlRequest = xmlRequestDoc
					.getUpdateKeyCodeRequest();
			ConditionsType xmlRequestConditions = xmlRequest.getConditions();
			DataType xmlRequestData = xmlRequest.getData();

			KeyCode keyCode = new KeyCode();
			if ((xmlRequestConditions.getVendorID() != null && xmlRequestConditions
					.getVendorID().trim().length() > 0)
					&& (xmlRequestConditions.getOrderID() != null && xmlRequestConditions
							.getOrderID().trim().length() > 0)
					&& (xmlRequestData.getStatus().toString() != null && xmlRequestData
							.getStatus().toString().trim().length() > 0)
					&& (xmlRequestData.getKeyCode() != null && xmlRequestData
							.getKeyCode().trim().length() > 0)) {
				// Vendor ID and Order ID are the conditions of the request
				// (i.e. the "WHERE" clause)

				keyCode.setVendorID(xmlRequestConditions.getVendorID());
				keyCode.setOrderID(xmlRequestConditions.getOrderID());
				try{
					keyCode.setStatus(xmlRequestData.getStatus().toString());
				}// Adds error code in serviceResult of response if Status Code
					// is not either of the five status and return the response
				catch (IllegalArgumentException e) {
					reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
					svErrorCde = "20030006";
					errorDscp = "Status Code can be of any of the following values "
							+ "--> 'FREE', 'RESERVED', 'ACTIVATED', 'IGNORE', 'CANCELLED'";
					xmlResponseDoc = DefaultKeyCodeProvider
							.generateUpdateKeyResponse(reqStatus, svErrorCde,
									errorDscp);
					//Logger.log(xmlResponseDoc);
					return xmlResponseDoc;
				}

				keyCode.setKeyCodeActual(xmlRequestData.getKeyCode().trim());
			}// Adds error code in serviceResult of response if Vendor Id /
				// Order Id /status / keycode is/are null and return the
				// response
			else {
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20030005";
				errorDscp = "Vendor Id / Order Id /status / keycode is/are mandatory";
				xmlResponseDoc = DefaultKeyCodeProvider
						.generateUpdateKeyResponse(reqStatus, svErrorCde,
								errorDscp);
				//Logger.log(xmlResponseDoc);
				return xmlResponseDoc;
			}
			DefaultKeyCodeProvider kcProvider = new DefaultKeyCodeProvider();
			boolean isKeyCodeChanged = kcProvider.update(keyCode);

			// Prepare the response document

			xmlResponse.setOrderID(keyCode.getOrderID());
			xmlResponse.setKeyCodeChanged(isKeyCodeChanged);
			xmlResponse.setKeyCode(keyCode.getKeyCodeActual());
			svResult.setStatusCode(BigInteger.ZERO);
		} catch (ApplicationException e) {
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			errorDscp = "Error occurred invoking the Key Bank - Please check SDP exception logs. "
					+ e.getMessage();
		} catch (SQLException e) {
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = "20030008";
			errorDscp = "SDP DB issue. " + e.getMessage();
		}
		if (hasError) {
			xmlResponseDoc = DefaultKeyCodeProvider.generateUpdateKeyResponse(
					reqStatus, svErrorCde, errorDscp);
		} else {
			xmlResponse.setServiceResult(svResult);
		}
		//Logger.log(xmlResponseDoc);
		return xmlResponseDoc;
	}

	public static final ReleaseKeyCodeResponseDocument releaseKeyCode(
			XmlObject requestXmlObject) {
		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		boolean hasError = false;

		ReleaseKeyCodeResponseDocument xmlResponseDoc = ReleaseKeyCodeResponseDocument.Factory
				.newInstance();
		ReleaseKeyCodeResponse xmlResponse = xmlResponseDoc
				.addNewReleaseKeyCodeResponse();
		ServiceResult svResult = ServiceResult.Factory.newInstance();
		try {
			ReleaseKeyCodeRequestDocument xmlRequestDoc = null;
			try {
				xmlRequestDoc = ReleaseKeyCodeRequestDocument.Factory
						.parse(requestXmlObject.xmlText());
			} catch (XmlException xmle) {
				throw new ApplicationException(
						"Input request is not of the type ReleaseKeyCodeRequestDocument",
						1);
			}
			ReleaseKeyCodeRequest xmlRequest = xmlRequestDoc
					.getReleaseKeyCodeRequest();

			KeyCode keyCode = new KeyCode();
			// Adds error code in serviceResult of response if order ID or
			// keyCode value is null and return the response
			if (xmlRequest.getKeyCode() != null
					&& xmlRequest.getKeyCode().trim().length() > 0) {
				keyCode.setVendorID(xmlRequest.getVendorID().trim());
				keyCode.setValue(xmlRequest.getKeyCode());
			} else if (xmlRequest.getOrderID() != null
					&& xmlRequest.getOrderID().trim().length() > 0) {
				keyCode.setVendorID(xmlRequest.getVendorID().trim());
				keyCode.setOrderID(xmlRequest.getOrderID());
			} else {
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20030007";
				errorDscp = "Either order ID or keyCode value is required";
				xmlResponseDoc = DefaultKeyCodeProvider
						.generateReleaseKeyResponse(reqStatus, svErrorCde,
								errorDscp);
				//Logger.log(xmlResponseDoc);
				return xmlResponseDoc;
			}

			DefaultKeyCodeProvider kcProvider = new DefaultKeyCodeProvider();
			kcProvider.release(keyCode);

			// Prepare the response document

			xmlResponse.setVendorID(keyCode.getVendorID());
			xmlResponse.setKeyCode(keyCode.getValue());
			xmlResponse.setOrderID(keyCode.getOrderID());
			svResult.setStatusCode(BigInteger.ZERO);

		} catch (ApplicationException e) {
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			errorDscp = "Error occurred invoking the Key Bank - Please check SDP exception logs."
					+ e.getMessage();
		} catch (SQLException e) {
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = "20030008";
			errorDscp = "SDP DB issue. " + e.getMessage();
		}
		if (hasError) {
			xmlResponseDoc = DefaultKeyCodeProvider.generateReleaseKeyResponse(
					reqStatus, svErrorCde, errorDscp);
		} else {
			xmlResponse.setServiceResult(svResult);
		}
		xmlResponse.setServiceResult(svResult);
		//Logger.log(xmlResponseDoc);
		return xmlResponseDoc;
	}
	
	private static String trimString(String val) {
		if (val == null) {
			return val;
		}
		return val.trim();
	}

}
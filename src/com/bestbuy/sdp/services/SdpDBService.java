package com.bestbuy.sdp.services;

import java.math.BigInteger;
import java.sql.SQLException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.accenture.common.ex.ApplicationException;
import com.accenture.xml.sdp.bby.om.bbyOrder.BBYOrder;
import com.accenture.xml.sdp.bby.om.bbyOrder.BBYOrderDocument;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResultDocument;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail.ErrorDescription;
import com.bestbuy.schemas.sdp.db.createOrder.CreateOrderRequestDocument;
import com.bestbuy.schemas.sdp.db.createOrder.CreateOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.createOrder.CreateOrderResponseDocument.CreateOrderResponse;
import com.bestbuy.schemas.sdp.db.insertException.InsertExceptionRequestDocument;
import com.bestbuy.schemas.sdp.db.insertRequestResponseLog.ReqResLogRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveCatalog.RetrieveCatalogOfferRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveCatalog.RetrieveCatalogOfferResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.updateOrder.UpdateOrderRequestDocument;
import com.bestbuy.schemas.sdp.db.updateOrder.UpdateOrderResponseDocument;
import com.bestbuy.sdp.catalog.SDPCatalogDB;
import com.bestbuy.sdp.catalog.helper.RetrieveCatalogOfferHelper;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.environment.WeblogicConsole;
import com.bestbuy.sdp.order.SDPOrderDB;
import com.bestbuy.sdp.order.helper.RetrieveOrderHelper;
import com.bestbuy.sdp.order.helper.UpdateOrderHelper;
import com.bestbuy.sdp.services.exception.SDPInternalException;

/**
 *This Class contains Real time activation java callouts.
 *
 * @author a148045
 *
 */
public class SdpDBService {

	/**
	 * This is a Java callout method which accepts XML as a parameter to create
	 * new order, it also generates response for that request. Returns that
	 * response in xml form. This method is called when request is in xml form
	 * other then BBYOrder.
	 *
	 * throws ApplicationException
	 *
	 * @param requestXmlObject
	 * @return xmlResponseDoc
	 * @throws ApplicationException
	 */
	public static final CreateOrderResponseDocument createOrder(
			XmlObject requestXmlObject) throws ApplicationException {

		BBYOrder xmlRequestDoc = null;
		CreateOrderResponseDocument xmlResponseDoc = null;

		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		try {
			if (requestXmlObject == null) {
				//Logger.log("XML object is null");
				throw new SDPInternalException("20040001",
						"Method=createOrder() :: Request message is null");

			} else {
				try {
					xmlRequestDoc = CreateOrderRequestDocument.Factory.parse(
							requestXmlObject.xmlText()).getCreateOrderRequest()
							.getBBYOrder();
					xmlResponseDoc = sdpOrderDB.createOrder(xmlRequestDoc);

				} catch (XmlException xmle) {
					throw new SDPInternalException("20040001",
							"Method=createOrder() :: Request message parsing exception ", xmle);
				}
			}
		} catch (SDPInternalException e) {

			// Generate response with Exception details
			xmlResponseDoc = CreateOrderResponseDocument.Factory.newInstance();
			CreateOrderResponse orderResponse = xmlResponseDoc
					.addNewCreateOrderResponse();

			orderResponse.addNewBBYOrder();
			orderResponse.setBBYOrder(xmlRequestDoc);

			ServiceResult serviceResult = null;
			serviceResult = orderResponse.addNewServiceResult();

			serviceResult.setStatusCode(BigInteger.ONE);
			serviceResult.setErrorCode(e.getErrorCode());
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode(e.getErrorCode());
			errorDtl.setMoreDetail(e.getErrorMessage() + " :: " + e.getMessage());
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(e.getErrorMessage() + " :: " + e.getMessage());
			serviceResult.setErrorSeverity("critical");
			Logger.logStackTrace(e.fillInStackTrace());

		}

		return xmlResponseDoc;
	}

	/**
	 * This is a Java callout method which accepts request in xml form as a
	 * parameter to create new order, it also generates response for that
	 * request. Returns that response in xml form.
	 *
	 * This method is called when request is in BBYOrder xml form.
	 *
	 * Throws ApplicationException
	 *
	 * @param bbyOrder
	 * @return xmlResponseDoc
	 * @throws ApplicationException
	 */
	public static final CreateOrderResponseDocument createOrder(
			BBYOrder bbyOrder) throws ApplicationException {

		BBYOrder xmlRequestDoc = null;
		CreateOrderResponseDocument xmlResponseDoc = null;

		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		try {
			if (bbyOrder == null) {
				//Logger.log("bbyOrder is null");
				throw new SDPInternalException("20040001",
						"Method=createOrder() ::Request message is null");
			} else {
				try {
					xmlRequestDoc = BBYOrderDocument.Factory.parse(
							bbyOrder.xmlText()).getBBYOrder();
					xmlResponseDoc = sdpOrderDB.createOrder(xmlRequestDoc);
				} catch (XmlException xmle) {
					throw new SDPInternalException("20040001",
							"Method=createOrder() ::Error while parsing Request xml document", xmle);
				}
			}
		} catch (SDPInternalException e) {
			xmlResponseDoc = CreateOrderResponseDocument.Factory.newInstance();
			CreateOrderResponse orderResponse = xmlResponseDoc
					.addNewCreateOrderResponse();

			orderResponse.addNewBBYOrder();
			orderResponse.setBBYOrder(bbyOrder);
			ServiceResult serviceResult = null;
			serviceResult = orderResponse.addNewServiceResult();

			serviceResult.setStatusCode(BigInteger.ONE);
			serviceResult.setErrorCode(e.getErrorCode());
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode(e.getErrorCode());
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(e.getErrorMessage() + e.getMessage());
			errorDtl.setMoreDetail(e.getErrorMessage() + e.getMessage());
			serviceResult.setErrorSeverity("critical");
			Logger.logStackTrace(e.fillInStackTrace());
		}
		return xmlResponseDoc;
	}

	/**
	 * Java callout method. Updates information of the order returns
	 * xmlResponseDoc, appropriate response for the order. This method is called
	 * when request is in xml form other then BBYOrder.
	 *
	 * throws ApplicationException
	 *
	 * @param requestXmlObject
	 * @return xmlResponseDoc
	 * @throws ApplicationException
	 */
	public static final UpdateOrderResponseDocument updateOrder(
			XmlObject requestXmlObject) throws ApplicationException {

		BBYOrder xmlRequestDoc = null;
		UpdateOrderRequestDocument requestDoc = null;
		UpdateOrderResponseDocument xmlResponseDoc = null;
		ServiceResult serviceResult = null;
		SDPOrderDB sdpOrderDB = new SDPOrderDB();
		String errorCode = null;

		try {
			if (requestXmlObject == null) {
				//Logger.log("XML object is null");

				throw new SDPInternalException("20040003",
						"Request message is null");

			} else {
				try {
					requestDoc = UpdateOrderRequestDocument.Factory.parse(
							requestXmlObject.xmlText());
					xmlRequestDoc= requestDoc.getUpdateOrderRequest().getBBYOrder();
					serviceResult = requestDoc.getUpdateOrderRequest().getServiceResult();
					if(serviceResult != null){
						if(serviceResult.getErrorDetailList() != null){
							ErrorDetail detail = serviceResult.getErrorDetailList().getErrorDetailArray(0);
							if(detail != null){
								errorCode = trimString(detail.getErrorCode());
							}
						}
					}
					xmlResponseDoc = sdpOrderDB.updateOrder(xmlRequestDoc, errorCode);
				} catch (XmlException xmle) {
					throw new SDPInternalException("20040003",
							"Error while parsing Request xml document", xmle);
				}
			}
		} catch (SDPInternalException e) {
			String reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			String errorMsg = e.getErrorMessage() + e.getMessage();
			UpdateOrderHelper updateOrd = new UpdateOrderHelper();
			xmlResponseDoc = updateOrd.generateResponse(reqStatus, e
					.getErrorCode(), errorMsg);
			Logger.logStackTrace(e.fillInStackTrace());
		}
		return xmlResponseDoc;
	}

	/**
	 * Java callout method. Updates information of the particular order and
	 * generate appropriate response for the order returns response. This method
	 * is called when request in BBYOrder xml form.
	 *
	 * throws ApplicationException
	 *
	 * @param bbyOrder
	 * @return xmlResponseDoc
	 * @throws ApplicationException
	 */
	public static final UpdateOrderResponseDocument updateOrder(
			BBYOrder bbyOrder, ServiceResult serviceResult) throws ApplicationException {

		BBYOrder xmlRequestDoc = null;
		UpdateOrderResponseDocument xmlResponseDoc = null;

		SDPOrderDB sdpOrderDB = new SDPOrderDB();
		String errorCode = null;
		ServiceResult result = null;

		try {
			if (bbyOrder == null) {
				//Logger.log("bbyOrder is null");
				throw new SDPInternalException("20040003", "BBYOrder is null",
						null);
			} else {
				try {
					xmlRequestDoc = BBYOrderDocument.Factory.parse(
							bbyOrder.xmlText()).getBBYOrder();
					result = ServiceResultDocument.Factory.parse(serviceResult.xmlText()).getServiceResult();
					if(result != null){
						if(result.getErrorDetailList() != null){
							ErrorDetail detail = result.getErrorDetailList().getErrorDetailList().get(0);
							if(detail != null){
								errorCode = trimString(detail.getErrorCode());
								//System.out.println("ErrorCode ::" + errorCode);
							}
						}
					}
					xmlResponseDoc = sdpOrderDB.updateOrder(xmlRequestDoc,errorCode);
				} catch (XmlException xmle) {
					throw new SDPInternalException("20040003",
							"Error while parsing Request xml document", xmle);
				}
			}
		} catch (SDPInternalException e) {
			String reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;

			String errorMsg = e.getErrorMessage() + " :: " + e.getMessage();
			UpdateOrderHelper updateOrd = new UpdateOrderHelper();
			xmlResponseDoc = updateOrd.generateResponse(reqStatus, e
					.getErrorCode(), errorMsg);
			Logger.logStackTrace(e.fillInStackTrace());
		}
		return xmlResponseDoc;
	}

	/**
	 * Java callout method. Retrieves order information and generate appropriate
	 * response for the order returns response
	 *
	 * throws ApplicationException
	 *
	 * @param requestXmlObject
	 * @return xmlResponseDoc
	 * @throws ApplicationException
	 */
	public static final XmlObject retrieveOrder(XmlObject requestXmlObject)
			throws ApplicationException {

		RetrieveOrderRequestDocument xmlRequestDoc = null;
		RetrieveOrderResponseDocument xmlResponseDoc = null;

		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		try {
			if (requestXmlObject == null) {
				//Logger.log("XML object is null");
				throw new SDPInternalException("20040005",
						"Request message is null");
			} else {
				try {
					xmlRequestDoc = RetrieveOrderRequestDocument.Factory
							.parse(requestXmlObject.xmlText());
					xmlResponseDoc = sdpOrderDB.retrieveOrder(xmlRequestDoc);

				} catch (XmlException xmle) {
					throw new SDPInternalException("20040005",
							"Error while parsing Request xml document", xmle);
				}

			}
		} catch (SDPInternalException e) {
			String reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			String errorDscp = e.getErrorCode() + " :: " + e.getMessage();
			xmlResponseDoc = RetrieveOrderHelper.generateRetrieveOrderResponse(
					reqStatus, e.getErrorCode(), errorDscp);
		}
		return xmlResponseDoc;
	}

	/**
	 * Java callout method. Retrieves catalog information by sku and generate
	 * appropriate response for the order returns response
	 *
	 * throws ApplicationException
	 *
	 * @param requestXmlObject
	 * @return xmlResponseDoc
	 * @throws ApplicationException
	 */
	public static final RetrieveCatalogOfferResponseDocument retrieveCatalogBySku(
			XmlObject requestXmlObject) throws ApplicationException {

		RetrieveCatalogOfferRequestDocument xmlRequestDoc = null;
		RetrieveCatalogOfferResponseDocument xmlResponseDoc = null;

		SDPCatalogDB sdpCatalogDB = new SDPCatalogDB();

		try {
			if (requestXmlObject == null) {
				//Logger.log("XML object is null");
				throw new SDPInternalException("20040008",
						"Request message is null");
			} else {
				try {
					//Logger.log("Request xml ::\n" + requestXmlObject);
					xmlRequestDoc = RetrieveCatalogOfferRequestDocument.Factory
							.parse(requestXmlObject.xmlText());
					xmlResponseDoc = sdpCatalogDB
							.retrieveCatalogBySKU(xmlRequestDoc);

				} catch (XmlException xmle) {

					throw new SDPInternalException("20040008",
							"Error while parsing Request xml document", xmle);
				}

			}
		} catch (SDPInternalException e) {
			String reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			String errorDscp = e.getMessage();
			xmlResponseDoc = RetrieveCatalogOfferHelper
					.generateRetrieveCatalogResponse(reqStatus, e
							.getErrorCode(), errorDscp);
			Logger.logStackTrace(e.fillInStackTrace());
		}
		return xmlResponseDoc;

	}

	public static final
	  RetrieveCatalogOfferResponseDocument retrieveCatalogByDigitalAttributes(
			XmlObject requestXmlObject) throws ApplicationException {

		RetrieveCatalogOfferRequestDocument xmlRequestDoc = null;
		RetrieveCatalogOfferResponseDocument xmlResponseDoc = null;

		SDPCatalogDB sdpCatalogDB = new SDPCatalogDB();

		try {
			if (requestXmlObject == null) {
				//Logger.log("XML object is null");
				throw new SDPInternalException("20040011",
						"Request message is null");

			} else {
				try {
					xmlRequestDoc = RetrieveCatalogOfferRequestDocument.Factory
							.parse(requestXmlObject.xmlText());
					xmlResponseDoc = sdpCatalogDB
							.retrieveCatalogByDigitalAttributes(xmlRequestDoc);

				} catch (XmlException xmle) {

					throw new SDPInternalException("20040011",
							"Error while parsing Request xml document", xmle);
				}
			}
		} catch (SDPInternalException e) {
			String reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			String errorDscp = e.getMessage();
			xmlResponseDoc = RetrieveCatalogOfferHelper
					.generateRetrieveCatalogResponse(reqStatus, e
							.getErrorCode(), errorDscp);
			Logger.logStackTrace(e.fillInStackTrace());
		}
		return xmlResponseDoc;

	}

	public static final void createSDPRequestResponseLogs(XmlObject requestXmlObject)
			throws ApplicationException {

		ReqResLogRequestDocument xmlRequestDoc = null;
		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		try{
		if (requestXmlObject == null) {
			//Logger.log("XML object is null");
			throw new SDPInternalException("","Request Xml is null");
		} else {

			try {
				xmlRequestDoc = ReqResLogRequestDocument.Factory
						.parse(requestXmlObject.xmlText());

				sdpOrderDB.createSDPReqRespLog(xmlRequestDoc);

			} catch (XmlException xmle) {
				Logger.logStackTrace(xmle.fillInStackTrace());
				throw new ApplicationException(
						"Error while parsing xml document. " + xmlRequestDoc, 1);
			} catch (SDPInternalException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}
		}catch(SDPInternalException e){
			Logger.logStackTrace(e.fillInStackTrace());
		}
	}

	/**
	 * Java callout method. Extract information from xml to insert information
	 * SDP_EXCEPTION_MESSAGE_LOG table returns generated response this method is
	 * called when request is in xml form other then BBYOrder.
	 *
	 * throws ApplicationException
	 *
	 * @param requestXmlObject
	 * @throws ApplicationException
	 */
	public static final void createExceptionMessage(XmlObject requestXmlObject,
			String sdpId, String sdpOrderId, String srcSysId)
			throws ApplicationException {

		InsertExceptionRequestDocument xmlRequestDoc = null;
		XmlObject resMsg = null;
		ServiceResult serviceResult = null;

		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		if (requestXmlObject == null) {
			//Logger.log("XML object is null");
		} else {

			
			try {
				xmlRequestDoc = InsertExceptionRequestDocument.Factory
						.parse(requestXmlObject.xmlText());

				resMsg = xmlRequestDoc.getInsertExceptionRequest()
						.getRequestMessage();
				serviceResult = xmlRequestDoc.getInsertExceptionRequest()
						.getServiceResult();
				Logger
						.log("Inside service class :: \n\n\n Request Message :: \n"
								+ resMsg
								+ "\n\n:: Service Result ::\n"
								+ serviceResult
								+ "\n\n :: SdpId ::"
								+ sdpId
								+ "\n \n:: SdpOrderId :: "
								+ sdpOrderId
								+ "\n\n:: SrcsysId :: " + srcSysId);
				sdpOrderDB.createException(resMsg, serviceResult, sdpId, sdpOrderId,
						srcSysId);

			} catch (XmlException xmle) {
				Logger.logStackTrace(xmle.fillInStackTrace());
				throw new ApplicationException(
						"Error while parsing xml document. " + xmlRequestDoc, 1);
			} catch (SDPInternalException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}
	}

	public void createExceptionMessage(XmlObject requestXmlObject)
	throws ApplicationException {

		InsertExceptionRequestDocument xmlRequestDoc = null;
		XmlObject resMsg = null;
		ServiceResult result = null;
		String sdpId = null, sdpOrderId = null, srcSysId = null;


		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		if (requestXmlObject == null) {
			//Logger.log("XML object is null");
		} else {

			
			try {
				xmlRequestDoc = InsertExceptionRequestDocument.Factory
						.parse(requestXmlObject.xmlText());

				resMsg = xmlRequestDoc.getInsertExceptionRequest()
						.getRequestMessage();
				result = xmlRequestDoc.getInsertExceptionRequest()
						.getServiceResult();
				
				if((xmlRequestDoc.getInsertExceptionRequest().getSdpId() != null)
						&& (xmlRequestDoc.getInsertExceptionRequest().getSdpId().trim().length() > 0)){
					sdpId = xmlRequestDoc.getInsertExceptionRequest().getSdpId().trim();
				}else{
					throw new SDPInternalException("","SdpId is null");
				}
				if((xmlRequestDoc.getInsertExceptionRequest().getSdpOrderId() != null)
						&& (xmlRequestDoc.getInsertExceptionRequest().getSdpOrderId().trim().length() > 0)){
					sdpOrderId = xmlRequestDoc.getInsertExceptionRequest().getSourceSystemId().trim();
				}

				if((xmlRequestDoc.getInsertExceptionRequest().getSourceSystemId() != null)
						&& (xmlRequestDoc.getInsertExceptionRequest().getSourceSystemId().trim().length() > 0)){
					srcSysId = xmlRequestDoc.getInsertExceptionRequest().getSourceSystemId().trim();
				}else{
					//throw new SDPInternalException("","SourceSystemId is null");
				}
				Logger
						.log("Inside service class :: \n\n\n Request Message :: \n"
								+ resMsg
								+ "\n\n:: Service Result ::\n"
								+ result
								+ "\n\n :: SdpId ::"
								+ sdpId
								+ "\n \n:: SdpOrderId :: "
								+ sdpOrderId
								+ "\n\n:: SrcsysId :: " + srcSysId);
				sdpOrderDB.createException(resMsg, result, sdpId, sdpOrderId,
						srcSysId);

			} catch (XmlException xmle) {
				Logger.logStackTrace(xmle.fillInStackTrace());
				throw new ApplicationException(
						"Error while parsing xml document. " + xmlRequestDoc, 1);
			} catch (SDPInternalException e) {
				Logger.logStackTrace(e.fillInStackTrace());
				throw new ApplicationException(
						"Method = createExceptionMessage() :: "+ e.getErrorMessage() + "  ErrorCode : " + e.getErrorCode() + xmlRequestDoc, 1);
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
				throw new ApplicationException(
						"Method = createExceptionMessage() :: "+ e.getMessage() + "  ErrorCode : " + e.getErrorCode() + xmlRequestDoc, 1);
			}
		}
	}

	/**
	 * Java callout method. Extract information from xml to insert information
	 * SDP_EXCEPTION_MESSAGE_LOG table returns generated response this method is
	 * called when request is in BBYOrder xml form.
	 *
	 * throws ApplicationException
	 *
	 * @param bb
	 * @param serviceResult
	 * @throws ApplicationException
	 */
	public static final void createExceptionMessage(BBYOrder bbyOrder,
			ServiceResult serviceResult) throws ApplicationException {

		SDPOrderDB sdpOrderDB = new SDPOrderDB();
		BBYOrder resMsg = null;
		ServiceResult result = null;

		if (bbyOrder == null || serviceResult == null) {
			//Logger.log("Either service result or BBYOrder is null");

		} else {
			try {
				resMsg =  BBYOrderDocument.Factory.parse(
						bbyOrder.xmlText()).getBBYOrder();
				result = ServiceResultDocument.Factory.parse(serviceResult.xmlText()).getServiceResult();
				//System.out.println(result.getStatusCode());
				sdpOrderDB.createException(resMsg, result);

			} catch (SDPInternalException e) {
				e.printStackTrace();
				Logger.logStackTrace(e.fillInStackTrace());
			}catch ( XmlException e){
				e.printStackTrace();
			}
		}
	}

	public static String getTemplateIdBySku(String sku, String dateString) {

		try {
			return new SDPCatalogDB().getTemplateId(sku, dateString);
		} catch (SQLException e) {
			WeblogicConsole
						.error("Caught SQLException during execution of commsatConfig.getTemplateId:");
			e.printStackTrace();
			return "EX";
		}
	}

	public static String getTemplateIdByDigitalAttr(String masterVendorId, String prodType, String dateString) {

		try {
			return new SDPCatalogDB().getTemplateId(masterVendorId, prodType, dateString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			WeblogicConsole
			.error("Caught SQLException during execution of commsatConfig.getTemplateId:");
			e.printStackTrace();
			return "EX";
		}

	}

	public static final void createCommsatLog(String subscriptionOfferId,
			String emailId, String redemptionCode, String templateId,
			String status, String userId, String confirmationId,
			String transactionDate, String bbyId, String lineItemId,
			String regId) {

		new SDPOrderDB().createCommsatLog(subscriptionOfferId, emailId,
				redemptionCode, templateId, status, userId, confirmationId,
				transactionDate, bbyId, lineItemId, regId);

	}

	public static final void createMerchantLinkTokenLog(
			String subscriptionOfferId, String vendorID, String bBYID,
			String custName, String ccToken, String cardType,
			String expirationDate, String address1, String address2,
			String city, String state, String zip, String processedFlag) {

		new SDPOrderDB().createMerchantLinkTokenLog(subscriptionOfferId,
				vendorID, bBYID, custName, ccToken, cardType, expirationDate,
				address1, address2, city, state, zip, processedFlag);

	}


	public static ServiceResult updateOrderStatus(BBYOrder bbyorder) throws ApplicationException {
		BBYOrder xmlRequestDoc = null;
		ServiceResult serviceResult = null;
		SDPOrderDB orderDB = new SDPOrderDB();
	    try {
			if(bbyorder == null){
				throw new SDPInternalException("20040020","Request is null");
			}else{
				try{
			      		xmlRequestDoc = BBYOrderDocument.Factory.parse(bbyorder.xmlText()).getBBYOrder();
						serviceResult = orderDB.updateOrderStatus(xmlRequestDoc);
				}catch (XmlException xmle) {
					Logger.logStackTrace(xmle.fillInStackTrace());
					throw new SDPInternalException("20040021",
							"Error while parsing xml document. ");
				}
			}
		}catch(SDPInternalException e){
			serviceResult = ServiceResult.Factory.newInstance();
			serviceResult.setStatusCode(BigInteger.ONE);
			serviceResult.setErrorCode(e.getErrorCode());
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode(e.getErrorCode());
			errorDtl.setMoreDetail(e.getErrorMessage() + " :: " + e.getMessage());
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(e.getErrorMessage() + " :: " + e.getMessage());
			serviceResult.setErrorSeverity("critical");
			e.printStackTrace();
		}
		return serviceResult;
	}
	
	private static String trimString(String val) {
		if (val == null) {
			return val;
		}
		return val.trim();
	}
}

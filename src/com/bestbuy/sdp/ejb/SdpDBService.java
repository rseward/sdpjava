package com.bestbuy.sdp.ejb;

import java.math.BigInteger;
import java.sql.SQLException;

import javax.ejb.SessionBean;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import weblogic.ejb.GenericSessionBean;
import weblogic.ejbgen.Constants;
import weblogic.ejbgen.FileGeneration;
import weblogic.ejbgen.JndiName;
import weblogic.ejbgen.LocalMethod;
import weblogic.ejbgen.RemoteMethod;
import weblogic.ejbgen.Session;

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
import com.bestbuy.schemas.sdp.db.insertCommsatLog.InsertCommsatLogRequestDocument;
import com.bestbuy.schemas.sdp.db.insertCommsatLog.InsertCommsatLogRequestDocument.InsertCommsatLogRequest;
import com.bestbuy.schemas.sdp.db.insertException.InsertExceptionRequestDocument;
import com.bestbuy.schemas.sdp.db.insertMerchantLinkTokenLog.InsertMerchantLinkTokenLogRequestDocument;
import com.bestbuy.schemas.sdp.db.insertMerchantLinkTokenLog.InsertMerchantLinkTokenLogRequestDocument.InsertMerchantLinkTokenLogRequest;
import com.bestbuy.schemas.sdp.db.insertRequestResponseLog.ReqResLogRequestDocument;
import com.bestbuy.schemas.sdp.db.insertVPSRecord.InsertVPSRecordRequestDocument;
import com.bestbuy.schemas.sdp.db.insertVPSRecord.InsertVPSRecordResponseDocument;
import com.bestbuy.schemas.sdp.db.insertVPSRecord.InsertVPSRecordRequestDocument.InsertVPSRecordRequest;
import com.bestbuy.schemas.sdp.db.insertVPSRecord.InsertVPSRecordResponseDocument.InsertVPSRecordResponse;
import com.bestbuy.schemas.sdp.db.insertVPSRequestLinkLog.InsertVPSRequestLinkLogRequestDocument;
import com.bestbuy.schemas.sdp.db.insertVPSRequestLinkLog.InsertVPSRequestLinkLogRequestDocument.InsertVPSRequestLinkLogRequest;
import com.bestbuy.schemas.sdp.db.insertVPSStatus.InsertVPSStatusRequestDocument;
import com.bestbuy.schemas.sdp.db.insertVPSStatus.InsertVPSStatusRequestDocument.InsertVPSStatusRequest;
import com.bestbuy.schemas.sdp.db.insertVPSStatusWithVndrCode.InsertVPSStatusWithVndrCodeRequestDocument;
import com.bestbuy.schemas.sdp.db.insertVPSStatusWithVndrCode.InsertVPSStatusWithVndrCodeResponseDocument;
import com.bestbuy.schemas.sdp.db.insertVPSStatusWithVndrCode.InsertVPSStatusWithVndrCodeRequestDocument.InsertVPSStatusWithVndrCodeRequest;
import com.bestbuy.schemas.sdp.db.insertVPSStatusWithVndrCode.InsertVPSStatusWithVndrCodeResponseDocument.InsertVPSStatusWithVndrCodeResponse;
import com.bestbuy.schemas.sdp.db.insertVndrRequestLog.InsertVndrRequestLogRequestDocument;
import com.bestbuy.schemas.sdp.db.insertVndrRequestLog.InsertVndrRequestLogResponseDocument;
import com.bestbuy.schemas.sdp.db.insertVndrRequestLog.InsertVndrRequestLogRequestDocument.InsertVndrRequestLogRequest;
import com.bestbuy.schemas.sdp.db.insertVndrRequestLog.InsertVndrRequestLogResponseDocument.InsertVndrRequestLogResponse;
import com.bestbuy.schemas.sdp.db.insertVndrResponseLog.InsertVndrResponseLogRequestDocument;
import com.bestbuy.schemas.sdp.db.insertVndrResponseLog.InsertVndrResponseLogResponseDocument;
import com.bestbuy.schemas.sdp.db.insertVndrResponseLog.InsertVndrResponseLogRequestDocument.InsertVndrResponseLogRequest;
import com.bestbuy.schemas.sdp.db.insertVndrResponseLog.InsertVndrResponseLogResponseDocument.InsertVndrResponseLogResponse;
import com.bestbuy.schemas.sdp.db.retrieveCatalog.RetrieveCatalogOfferRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveCatalog.RetrieveCatalogOfferResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrderByAttributes.RetrieveOrderByAttributesRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrderByAttributes.RetrieveOrderByAttributesResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveRetryStatus.RetrieveRetryStatusRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveRetryStatus.RetrieveRetryStatusResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveRetryStatus.RetrieveRetryStatusRequestDocument.RetrieveRetryStatusRequest;
import com.bestbuy.schemas.sdp.db.retrieveRetryStatus.RetrieveRetryStatusResponseDocument.RetrieveRetryStatusResponse;
import com.bestbuy.schemas.sdp.db.retrieveTemplateIDByDigitalAttr.RetrieveTemplateIDByDigitalAttrRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveTemplateIDByDigitalAttr.RetrieveTemplateIDByDigitalAttrResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveTemplateIDByDigitalAttr.RetrieveTemplateIDByDigitalAttrRequestDocument.RetrieveTemplateIDByDigitalAttrRequest;
import com.bestbuy.schemas.sdp.db.retrieveTemplateIDBySKU.RetrieveTemplateIDBySKURequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveTemplateIDBySKU.RetrieveTemplateIDBySKUResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveTemplateIDBySKU.RetrieveTemplateIDBySKURequestDocument.RetrieveTemplateIDBySKURequest;
import com.bestbuy.schemas.sdp.db.updateOrder.UpdateOrderRequestDocument;
import com.bestbuy.schemas.sdp.db.updateOrder.UpdateOrderResponseDocument;
import com.bestbuy.sdp.catalog.SDPCatalogDB;
import com.bestbuy.sdp.catalog.helper.RetrieveCatalogOfferHelper;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.environment.WeblogicConsole;
import com.bestbuy.sdp.order.SDPOrderDB;
import com.bestbuy.sdp.order.helper.RetrieveOrderHelper;
import com.bestbuy.sdp.order.helper.UpdateOrderHelper;
import com.bestbuy.sdp.provisioning.VendorLogDB;
import com.bestbuy.sdp.provisioning.VendorProvisioningDB;
import com.bestbuy.sdp.provisioning.VendorStatusCodeDB;
import com.bestbuy.sdp.services.SdpDBConstants;
import com.bestbuy.sdp.services.exception.SDPInternalException;

/**
 * GenericSessionBean subclass automatically generated by OEPE.
 * 
 * Please complete the ejbCreate method as needed to properly initialize new
 * instances of your bean and add all required business methods. Also, review
 * the Session, JndiName and FileGeneration annotations to ensure the settings
 * match the bean's intended use.
 */
@Session(ejbName = "SdpDBService")
@JndiName(remote = "ejb.SdpDBServiceRemoteHome", local = "ejb.SdpDBServiceLocalHome")
@FileGeneration(remoteClass = Constants.Bool.TRUE, remoteHome = Constants.Bool.TRUE, localClass = Constants.Bool.TRUE, localHome = Constants.Bool.TRUE)
public class SdpDBService extends GenericSessionBean implements SessionBean {
	private long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see weblogic.ejb.GenericSessionBean#ejbCreate()
	 */
	public void ejbCreate() {
		// IMPORTANT: Add your code here
	}

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
	@RemoteMethod
	@LocalMethod
	public CreateOrderResponseDocument createOrder(XmlObject requestXmlObject)
			throws ApplicationException {

		BBYOrder xmlRequestDoc = null;

		CreateOrderResponseDocument xmlResponseDoc = null;

		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		try {
			if (requestXmlObject == null) {

				// Logger.log("XML object is null");
				throw new SDPInternalException("20040001",
						"Method = createOrder() :: Request message is null");

			} else {
				try {
					xmlRequestDoc = CreateOrderRequestDocument.Factory.parse(
							requestXmlObject.xmlText()).getCreateOrderRequest()
							.getBBYOrder();
					xmlResponseDoc = sdpOrderDB.createOrder(xmlRequestDoc);

				} catch (XmlException xmle) {
					throw new SDPInternalException(
							"20040001",
							"Method = createOrder() :: Request message parsing exception ",
							xmle);
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
			errorDtl.setMoreDetail(e.getErrorMessage() + " :: "
					+ e.getMessage());
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(e.getErrorMessage() + " :: " + e.getMessage());
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
	@RemoteMethod
	@LocalMethod
	public UpdateOrderResponseDocument updateOrder(XmlObject requestXmlObject)
			throws ApplicationException {

		BBYOrder xmlRequestDoc = null;
		UpdateOrderRequestDocument requestDoc = null;
		UpdateOrderResponseDocument xmlResponseDoc = null;
		ServiceResult serviceResult = null;
		SDPOrderDB sdpOrderDB = new SDPOrderDB();
		String errorCode = null;

		try {
			if (requestXmlObject == null) {
				// Logger.log("XML object is null");

				throw new SDPInternalException("20040003",
						"Method = updateOrder :: Request message is null");

			} else {
				try {
					requestDoc = UpdateOrderRequestDocument.Factory
							.parse(requestXmlObject.xmlText());
					xmlRequestDoc = requestDoc.getUpdateOrderRequest()
							.getBBYOrder();
					serviceResult = requestDoc.getUpdateOrderRequest()
							.getServiceResult();
					if (serviceResult != null) {
						if (serviceResult.getErrorDetailList() != null) {
							ErrorDetail detail = serviceResult
									.getErrorDetailList()
									.getErrorDetailArray(0);
							if (detail != null) {
								errorCode = detail.getErrorCode().trim();
							}
						}
					}
					xmlResponseDoc = sdpOrderDB.updateOrder(xmlRequestDoc,
							errorCode);
				} catch (XmlException xmle) {
					throw new SDPInternalException(
							"20040003",
							"Method = updateOrder :: Error while parsing Request xml document",
							xmle);
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
	@RemoteMethod
	@LocalMethod
	public UpdateOrderResponseDocument updateOrder(BBYOrder bbyOrder,
			ServiceResult serviceResult) throws ApplicationException {

		BBYOrder xmlRequestDoc = null;
		UpdateOrderResponseDocument xmlResponseDoc = null;

		SDPOrderDB sdpOrderDB = new SDPOrderDB();
		String errorCode = null;

		try {
			if (bbyOrder == null) {
				// Logger.log("bbyOrder is null");
				throw new SDPInternalException("20040003",
						"Method = updateOrder :: BBYOrder is null", null);
			} else {
				try {
					xmlRequestDoc = BBYOrderDocument.Factory.parse(
							bbyOrder.xmlText()).getBBYOrder();
					if (serviceResult != null) {
						if (serviceResult.getErrorDetailList() != null) {
							ErrorDetail detail = serviceResult
									.getErrorDetailList()
									.getErrorDetailArray(0);
							if (detail != null) {
								errorCode = detail.getErrorCode().trim();
							}
						}
					}
					xmlResponseDoc = sdpOrderDB.updateOrder(xmlRequestDoc,
							errorCode);
				} catch (XmlException xmle) {
					throw new SDPInternalException(
							"20040003",
							"Method = updateOrder :: Error while parsing Request xml document",
							xmle);
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
	@RemoteMethod
	@LocalMethod
	public XmlObject retrieveOrder(XmlObject requestXmlObject)
			throws ApplicationException {

		RetrieveOrderRequestDocument xmlRequestDoc = null;
		RetrieveOrderResponseDocument xmlResponseDoc = null;

		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		try {
			if (requestXmlObject == null) {
				// Logger.log("XML object is null");
				throw new SDPInternalException("20040005",
						"Method = retrieveOrder :: Request message is null");
			} else {
				try {
					xmlRequestDoc = RetrieveOrderRequestDocument.Factory
							.parse(requestXmlObject.xmlText());
					xmlResponseDoc = sdpOrderDB.retrieveOrder(xmlRequestDoc);

				} catch (XmlException xmle) {
					throw new SDPInternalException(
							"20040005",
							"Method = retrieveOrder :: Error while parsing Request xml document",
							xmle);
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
	 * Java callout method. Retrieves order information and generate appropriate
	 * response for the order returns response
	 * 
	 * throws ApplicationException
	 * 
	 * @param requestXmlObject
	 * @return xmlResponseDoc
	 * @throws ApplicationException
	 */
	@RemoteMethod
	@LocalMethod
	public XmlObject retrieveOrderByAttributes(XmlObject requestXmlObject)
			throws ApplicationException {

		RetrieveOrderByAttributesRequestDocument xmlRequestDoc = null;
		RetrieveOrderByAttributesResponseDocument xmlResponseDoc = null;

		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		try {
			if (requestXmlObject == null) {
				// Logger.log("XML object is null");
				throw new SDPInternalException("20040005",
						"Method = retrieveOrderByAttributes :: Request message is null");
			} else {
				try {
					xmlRequestDoc = RetrieveOrderByAttributesRequestDocument.Factory
							.parse(requestXmlObject.xmlText());
					xmlResponseDoc = sdpOrderDB
							.retrieveOrderByAttributes(xmlRequestDoc);

				} catch (XmlException xmle) {
					throw new SDPInternalException(
							"20040005",
							"Method = retrieveOrderByAttributes :: Error while parsing Request xml document",
							xmle);
				}

			}
		} catch (SDPInternalException e) {
			String reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			String svErrorCde = "20040005";
			String errorDscp = e.getErrorCode() + " :: " + e.getMessage();
			xmlResponseDoc = RetrieveOrderHelper
					.generateRetrieveOrderByAttributesResponse(reqStatus,
							svErrorCde, errorDscp);
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
	@RemoteMethod
	@LocalMethod
	public RetrieveCatalogOfferResponseDocument retrieveCatalogBySku(
			XmlObject requestXmlObject) throws ApplicationException {

		RetrieveCatalogOfferRequestDocument xmlRequestDoc = null;
		RetrieveCatalogOfferResponseDocument xmlResponseDoc = null;

		SDPCatalogDB sdpCatalogDB = new SDPCatalogDB();

		try {
			if (requestXmlObject == null) {
				// Logger.log("XML object is null");
				throw new SDPInternalException("20040008",
						"Method = retrieveCatalogBySku :: Request message is null");
			} else {
				try {
					// Logger.log("Request xml ::\n" + requestXmlObject);
					xmlRequestDoc = RetrieveCatalogOfferRequestDocument.Factory
							.parse(requestXmlObject.xmlText());
					xmlResponseDoc = sdpCatalogDB
							.retrieveCatalogBySKU(xmlRequestDoc);

				} catch (XmlException xmle) {

					throw new SDPInternalException(
							"20040008",
							"Method = retrieveCatalogBySku :: Error while parsing Request xml document",
							xmle);
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

	@RemoteMethod
	@LocalMethod
	public RetrieveCatalogOfferResponseDocument retrieveCatalogByDigitalAttributes(
			XmlObject requestXmlObject) throws ApplicationException {

		RetrieveCatalogOfferRequestDocument xmlRequestDoc = null;
		RetrieveCatalogOfferResponseDocument xmlResponseDoc = null;

		SDPCatalogDB sdpCatalogDB = new SDPCatalogDB();

		try {
			if (requestXmlObject == null) {
				// Logger.log("XML object is null");
				throw new SDPInternalException("20040011",
						"Method = retrieveCatalogByDigitalAttributes :: Request message is null");

			} else {
				try {
					xmlRequestDoc = RetrieveCatalogOfferRequestDocument.Factory
							.parse(requestXmlObject.xmlText());
					xmlResponseDoc = sdpCatalogDB
							.retrieveCatalogByDigitalAttributes(xmlRequestDoc);

				} catch (XmlException xmle) {

					throw new SDPInternalException(
							"20040011",
							"Method = retrieveCatalogByDigitalAttributes :: Error while parsing Request xml document",
							xmle);
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

	@RemoteMethod
	@LocalMethod
	public void createSDPRequestResponseLogs(XmlObject requestXmlObject)
			throws ApplicationException {

		ReqResLogRequestDocument xmlRequestDoc = null;
		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		try {
			if (requestXmlObject == null) {
				// Logger.log("XML object is null");
				throw new SDPInternalException("",
						"Method = createSDPRequestResponseLogs :: Request Xml is null");
			} else {

				try {
					xmlRequestDoc = ReqResLogRequestDocument.Factory
							.parse(requestXmlObject.xmlText());

					sdpOrderDB.createSDPReqRespLog(xmlRequestDoc);

				} catch (XmlException xmle) {
					Logger.logStackTrace(xmle.fillInStackTrace());
					throw new ApplicationException(
							"Method = createSDPRequestResponseLogs :: Error while parsing xml document. "
									+ xmle.getMessage(), 1);
				} catch (SDPInternalException e) {
					Logger.logStackTrace(e.fillInStackTrace());
					throw new ApplicationException(
							"Method = createSDPRequestResponseLogs :: Error while parsing xml document. "
									+ e.getMessage(), 1);
				} catch (SQLException e) {
					Logger.logStackTrace(e.fillInStackTrace());
					throw new ApplicationException(
							"Method = createSDPRequestResponseLogs :: SQLException. "
									+ e.getMessage(), 1);
				}
			}
		} catch (SDPInternalException e) {
			Logger.logStackTrace(e.fillInStackTrace());
			throw new ApplicationException(
					"Method = createSDPRequestResponseLogs :: Error while parsing xml document. "
							+ e.getMessage(), 1);
		}
	}

	@RemoteMethod
	@LocalMethod
	public void updateSDPRequestResponseLogs(XmlObject requestXmlObject)
			throws ApplicationException {

		ReqResLogRequestDocument xmlRequestDoc = null;
		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		try {
			if (requestXmlObject == null) {
				// Logger.log("XML object is null");
				throw new SDPInternalException("",
						"Method = updateSDPRequestResponseLogs :: Request Xml is null");
			} else {

				try {
					xmlRequestDoc = ReqResLogRequestDocument.Factory
							.parse(requestXmlObject.xmlText());

					sdpOrderDB.updateSDPReqRespLog(xmlRequestDoc);

				} catch (XmlException xmle) {
					Logger.logStackTrace(xmle.fillInStackTrace());
					throw new ApplicationException(
							"Method = updateSDPRequestResponseLogs :: Error while parsing xml document. "
									+ xmle.getMessage(), 1);
				} catch (SDPInternalException e) {
					Logger.logStackTrace(e.fillInStackTrace());
					throw new ApplicationException(
							"Method = updateSDPRequestResponseLogs :: Error while parsing xml document. "
									+ e.getMessage(), 1);
				} catch (SQLException e) {
					Logger.logStackTrace(e.fillInStackTrace());
					throw new ApplicationException(
							"Method = updateSDPRequestResponseLogs :: SQLException. "
									+ e.getMessage(), 1);
				}
			}
		} catch (SDPInternalException e) {
			Logger.logStackTrace(e.fillInStackTrace());
			throw new ApplicationException(
					"Method = updateSDPRequestResponseLogs :: Error while parsing xml document. "
							+ e.getMessage(), 1);
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
	@RemoteMethod
	@LocalMethod
	public void createExceptionMessage(XmlObject requestXmlObject)
			throws ApplicationException {

		InsertExceptionRequestDocument xmlRequestDoc = null;
		XmlObject resMsg = null;
		ServiceResult result = null;
		String sdpId = null, sdpOrderId = null, srcSysId = null;

		SDPOrderDB sdpOrderDB = new SDPOrderDB();

		if (requestXmlObject == null) {
			// Logger.log("XML object is null");
		} else {

			try {
				xmlRequestDoc = InsertExceptionRequestDocument.Factory
						.parse(requestXmlObject.xmlText());

				resMsg = xmlRequestDoc.getInsertExceptionRequest()
						.getRequestMessage();
				result = xmlRequestDoc.getInsertExceptionRequest()
						.getServiceResult();
				if ((xmlRequestDoc.getInsertExceptionRequest().getSdpId() != null)
						&& (xmlRequestDoc.getInsertExceptionRequest()
								.getSdpId().trim().length() > 0)) {
					sdpId = xmlRequestDoc.getInsertExceptionRequest()
							.getSdpId().trim();
				} else {
					throw new SDPInternalException("",
							"Method = createExceptionMessage() :: SdpId is null");
				}
				if ((xmlRequestDoc.getInsertExceptionRequest().getSdpOrderId() != null)
						&& (xmlRequestDoc.getInsertExceptionRequest()
								.getSdpOrderId().trim().length() > 0)) {
					sdpOrderId = xmlRequestDoc.getInsertExceptionRequest()
							.getSdpOrderId().trim();
				}

				if ((xmlRequestDoc.getInsertExceptionRequest()
						.getSourceSystemId() != null)
						&& (xmlRequestDoc.getInsertExceptionRequest()
								.getSourceSystemId().trim().length() > 0)) {
					srcSysId = xmlRequestDoc.getInsertExceptionRequest()
							.getSourceSystemId().trim();
				} else {
					// throw new SDPInternalException("","Method =
					// createExceptionMessage() :: SourceSystemId is null");
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
						"Method = createExceptionMessage() :: Error while parsing xml document. "
								+ xmlRequestDoc, 1);
			} catch (SDPInternalException e) {
				Logger.logStackTrace(e.fillInStackTrace());
				throw new ApplicationException(
						"Method = createExceptionMessage() :: "
								+ e.getErrorMessage() + "  ErrorCode : "
								+ e.getErrorCode() + xmlRequestDoc, 1);
			} catch (SQLException e) {
				Logger.logStackTrace(e.fillInStackTrace());
				throw new ApplicationException(
						"Method = createExceptionMessage() :: SQLException "
								+ e.getMessage(), 1);
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
	 * @param bbyOrderReq
	 * @param servRst
	 * @throws ApplicationException
	 */
	@RemoteMethod
	@LocalMethod
	public void createExceptionMessage(BBYOrder bbyOrder,
			ServiceResult serviceResult) throws ApplicationException {

		SDPOrderDB sdpOrderDB = new SDPOrderDB();
		BBYOrder xmlRequestDoc = null;
		ServiceResult result = null;

		if (bbyOrder == null || serviceResult == null) {
			// Logger.log("Either service result or BBYOrder is null");

		} else {
			try {
				xmlRequestDoc = BBYOrderDocument.Factory.parse(
						bbyOrder.xmlText()).getBBYOrder();
				result = ServiceResultDocument.Factory.parse(
						serviceResult.xmlText()).getServiceResult();
				// System.out.println(result.getStatusCode());
				sdpOrderDB.createException(xmlRequestDoc, result);

			} catch (SDPInternalException e) {
				e.printStackTrace();
				Logger.logStackTrace(e.fillInStackTrace());
			} catch (XmlException e) {
				e.printStackTrace();
			}
		}
	}

	@RemoteMethod
	@LocalMethod
	public RetrieveTemplateIDBySKUResponseDocument getTemplateIdBySku(
			XmlObject requestXmlObject) {
		RetrieveTemplateIDBySKURequest xmlRequestDoc = null;
		RetrieveTemplateIDBySKUResponseDocument xmlResponseDoc = null;
		String sku = null, dateString = null;
		String templateId = null;
		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		SDPCatalogDB catalogDB = new SDPCatalogDB();

		try {
			xmlRequestDoc = RetrieveTemplateIDBySKURequestDocument.Factory
					.parse(requestXmlObject.xmlText())
					.getRetrieveTemplateIDBySKURequest();
			sku = xmlRequestDoc.getSkuID();
			dateString = xmlRequestDoc.getDate();

			if (sku != null && sku.trim().length() > 0) {
				templateId = catalogDB.getTemplateId(sku.trim(), dateString);
				if (templateId != null && templateId.trim().length() > 0) {
					reqStatus = SdpDBConstants.SUCCESS;
				} else {
					reqStatus = SdpDBConstants.FAILURE;
					svErrorCde = "20040018";
					errorDscp = "TemplateId not defined in the system : getTemplateIdBySku()";
				}

			} else {
				reqStatus = SdpDBConstants.FAILURE;
				svErrorCde = "20040015";
				errorDscp = "Method = getTemplateIdBySku() :: SKU is Mandatory";
			}

		} catch (XmlException e) {
			e.printStackTrace();
			reqStatus = SdpDBConstants.FAILURE;
			svErrorCde = "20040015";
			errorDscp = "Method = getTemplateIdBySku() :: XmlException";
		} catch (SQLException e) {
			WeblogicConsole
					.error("Caught SQLException during execution of getTemplateIdBySku:");
			reqStatus = SdpDBConstants.FAILURE;
			svErrorCde = "20040016";
			errorDscp = "Method = getTemplateIdBySku() :: SDP DB issue"
					+ e.getMessage();
			e.printStackTrace();
		}
		xmlResponseDoc = SDPCatalogDB.generateTempIdBySkuRes(templateId,
				reqStatus, svErrorCde, errorDscp);

		return xmlResponseDoc;
	}

	@RemoteMethod
	@LocalMethod
	public RetrieveTemplateIDByDigitalAttrResponseDocument getTemplateIdByDigitalAttributes(
			XmlObject requestXmlObject) {
		RetrieveTemplateIDByDigitalAttrRequest xmlRequestDoc = null;
		RetrieveTemplateIDByDigitalAttrResponseDocument xmlResponseDoc = null;
		String masterVendorId = null, prodType = null, dateString = null;
		String templateId = null;
		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		SDPCatalogDB catalogDB = new SDPCatalogDB();
		try {
			xmlRequestDoc = RetrieveTemplateIDByDigitalAttrRequestDocument.Factory
					.parse(requestXmlObject.xmlText())
					.getRetrieveTemplateIDByDigitalAttrRequest();
			masterVendorId = xmlRequestDoc.getMasterVendorId();
			prodType = xmlRequestDoc.getDigitalProductType();
			dateString = xmlRequestDoc.getDate();
			if ((masterVendorId != null && masterVendorId.trim().length() > 0)
					&& (prodType != null && prodType.trim().length() > 0)) {

				templateId = catalogDB.getTemplateId(masterVendorId.trim(),
						prodType.trim(), dateString);
				if (templateId != null && templateId.trim().length() > 0) {
					reqStatus = SdpDBConstants.SUCCESS;
				} else {
					reqStatus = SdpDBConstants.SUCCESS;
					svErrorCde = "20040018";
					errorDscp = "TemplateId not defined in the system : getTemplateIdByDigitalAttributes()";
				}

			} else {
				reqStatus = SdpDBConstants.FAILURE;
				svErrorCde = "20040015";
				errorDscp = "Method = getTemplateIdByDigitalAttributes() :: masterVendorId/prodType is/are Mandatory )";
			}
		} catch (XmlException e) {

			e.printStackTrace();
			reqStatus = SdpDBConstants.FAILURE;
			svErrorCde = "20040015";
			errorDscp = "Method = getTemplateIdByDigitalAttributes() :: XmlException"
					+ e.getMessage();
		} catch (SQLException e) {
			WeblogicConsole
					.error("Caught SQLException during execution of getTemplateIdByDigitalAttributes:");
			reqStatus = SdpDBConstants.FAILURE;
			svErrorCde = "20040016";
			errorDscp = "Method = getTemplateIdByDigitalAttributes() :: SDP DB issue"
					+ e.getMessage();
			e.printStackTrace();
		}
		xmlResponseDoc = SDPCatalogDB.generateTempIdByDigitalAttrRes(
				templateId, reqStatus, svErrorCde, errorDscp);
		return xmlResponseDoc;

	}

	@RemoteMethod
	@LocalMethod
	public void createCommsatLog(XmlObject requestXmlObject) {

		InsertCommsatLogRequest xmlRequestDoc = null;
		String subscriptionOfferId = null, emailId = null, redemptionCode = null, templateId = null, status = null, userId = null, confirmationId = null, transactionDate = null, bbyId = null, lineItemId = null, regId = null;

		try {
			xmlRequestDoc = InsertCommsatLogRequestDocument.Factory.parse(
					requestXmlObject.xmlText()).getInsertCommsatLogRequest();
			subscriptionOfferId = xmlRequestDoc.getSdpOrderID();
			emailId = xmlRequestDoc.getEmailID();
			redemptionCode = xmlRequestDoc.getRedemptionCode();
			templateId = xmlRequestDoc.getTemplateID();
			status = xmlRequestDoc.getStatus();
			userId = xmlRequestDoc.getUserID();
			confirmationId = xmlRequestDoc.getConfirmationID();
			transactionDate = xmlRequestDoc.getTransactionDate();
			bbyId = xmlRequestDoc.getSDPID();
			lineItemId = xmlRequestDoc.getLineItemID();
			regId = xmlRequestDoc.getRegId();

			new SDPOrderDB().createCommsatLog(subscriptionOfferId, emailId,
					redemptionCode, templateId, status, userId, confirmationId,
					transactionDate, bbyId, lineItemId, regId);
		} catch (XmlException e) {

			e.printStackTrace();
		}
		return;

	}

	@RemoteMethod
	@LocalMethod
	public void createMerchantLinkTokenLog(XmlObject requestXmlObject) {
		InsertMerchantLinkTokenLogRequest xmlRequestDoc = null;
		String subscriptionOfferId = null, vendorID = null, BBYID = null, custName = null, ccToken = null, cardType = null, expirationDate = null, address1 = null, address2 = null, city = null, state = null, zip = null, processedFlag = null;

		try {
			xmlRequestDoc = InsertMerchantLinkTokenLogRequestDocument.Factory
					.parse(requestXmlObject.xmlText())
					.getInsertMerchantLinkTokenLogRequest();
			subscriptionOfferId = xmlRequestDoc.getSdpOrderID();
			vendorID = xmlRequestDoc.getVendorID();
			BBYID = xmlRequestDoc.getBBYID();
			custName = xmlRequestDoc.getCustomerName();
			ccToken = xmlRequestDoc.getCCToken();
			cardType = xmlRequestDoc.getCardType();
			expirationDate = xmlRequestDoc.getExpirationDate();
			address1 = xmlRequestDoc.getAddress1();
			address2 = xmlRequestDoc.getAddress2();
			city = xmlRequestDoc.getCity();
			state = xmlRequestDoc.getState();
			zip = xmlRequestDoc.getZipCode();
			processedFlag = xmlRequestDoc.getProcessedFlag();

			new SDPOrderDB().createMerchantLinkTokenLog(subscriptionOfferId,
					vendorID, BBYID, custName, ccToken, cardType,
					expirationDate, address1, address2, city, state, zip,
					processedFlag);
		} catch (XmlException e) {

			e.printStackTrace();
		}

		return;
	}

	@RemoteMethod
	@LocalMethod
	public InsertVndrRequestLogResponseDocument createVendorRequestLog(
			XmlObject requestXmlObject) {

		InsertVndrRequestLogRequest xmlRequestDoc = null;
		InsertVndrRequestLogResponseDocument xmlResponseDoc = null;
		int rowID = -1;
		String vendorID = null;
		String reqType = null;
		XmlObject msgXml = null;
		try {
			xmlRequestDoc = InsertVndrRequestLogRequestDocument.Factory.parse(
					requestXmlObject.xmlText())
					.getInsertVndrRequestLogRequest();
			vendorID = xmlRequestDoc.getVendorID();
			reqType = xmlRequestDoc.getRequestType();
			msgXml = xmlRequestDoc.getMessageXML();
			if ((vendorID != null && vendorID.trim().length() > 0)
					&& (reqType != null && reqType.trim().length() > 0)) {
				rowID = VendorLogDB.insertRequestLog(vendorID, reqType, msgXml);
			}
			xmlResponseDoc = InsertVndrRequestLogResponseDocument.Factory
					.newInstance();
			InsertVndrRequestLogResponse responseXml = xmlResponseDoc
					.addNewInsertVndrRequestLogResponse();
			responseXml.setRowID(rowID);
		} catch (XmlException e) {

			e.printStackTrace();
		}

		return xmlResponseDoc;
	}

	@RemoteMethod
	@LocalMethod
	public InsertVndrResponseLogResponseDocument createVendorResponseLog(
			XmlObject requestXmlObject) {
		InsertVndrResponseLogRequest xmlRequestDoc = null;
		InsertVndrResponseLogResponseDocument xmlResponseDoc = null;
		int rowID = -1;
		// TODO : implement
		String vendorID = null;
		int reqLogID = -1;
		String reqType = null;
		String statusCode = null;
		XmlObject msgXml = null;

		try {
			xmlRequestDoc = InsertVndrResponseLogRequestDocument.Factory.parse(
					requestXmlObject.xmlText())
					.getInsertVndrResponseLogRequest();
			vendorID = xmlRequestDoc.getVendorID();
			reqLogID = xmlRequestDoc.getRequestLogID();
			reqType = xmlRequestDoc.getRequestType();
			statusCode = xmlRequestDoc.getStatusCode();
			msgXml = xmlRequestDoc.getMessageXML();
			if ((vendorID != null && vendorID.trim().length() > 0)
					&& (reqLogID != -1)
					&& (reqType != null && reqType.trim().length() > 0)
					&& (statusCode != null && statusCode.trim().length() > 0)) {
				rowID = VendorLogDB.insertResponseLog(vendorID, reqLogID,
						reqType, statusCode, msgXml);
			}
			xmlResponseDoc = InsertVndrResponseLogResponseDocument.Factory
					.newInstance();
			InsertVndrResponseLogResponse responseXml = xmlResponseDoc
					.addNewInsertVndrResponseLogResponse();
			responseXml.setRowID(rowID);
		} catch (XmlException e) {

			e.printStackTrace();
		}

		return xmlResponseDoc;
	}

	@RemoteMethod
	@LocalMethod
	public void createVPSRequestLinkLog(XmlObject requestXmlObject)
			throws ApplicationException {
		InsertVPSRequestLinkLogRequest xmlRequestDoc = null;
		XmlObject msgXml = null;
		int reqLogID = -1;

		try {
			xmlRequestDoc = InsertVPSRequestLinkLogRequestDocument.Factory
					.parse(requestXmlObject.xmlText())
					.getInsertVPSRequestLinkLogRequest();
			msgXml = xmlRequestDoc.getMessageXML();
			reqLogID = xmlRequestDoc.getRequestLogID();
			if (reqLogID != -1) {
				VendorLogDB.insertVPSRequestLinkLog(msgXml, reqLogID);
			}
		} catch (XmlException e) {

			e.printStackTrace();
		}
		return;
	}

	@RemoteMethod
	@LocalMethod
	public InsertVPSRecordResponseDocument createVPSRecord(
			XmlObject requestXmlObject) {

		InsertVPSRecordRequest xmlRequestDoc = null;
		InsertVPSRecordResponseDocument xmlResponseDoc = null;
		int rowID = -1;
		String vendorID = null;
		String vendorKey = null;
		int csmID = -1;
		String sdpID = null;
		String reqType = null;
		String status = null;
		XmlObject msgXml = null;

		try {
			xmlRequestDoc = InsertVPSRecordRequestDocument.Factory.parse(
					requestXmlObject.xmlText()).getInsertVPSRecordRequest();
			vendorID = xmlRequestDoc.getVendorID();
			vendorKey = xmlRequestDoc.getVendorKey();
			csmID = xmlRequestDoc.getSdpOrderID();
			sdpID = xmlRequestDoc.getSDPID();
			reqType = xmlRequestDoc.getRequestType();
			status = xmlRequestDoc.getStatus();
			msgXml = xmlRequestDoc.getMessageXML();

			if ((vendorID != null && vendorID.trim().length() > 0)
					// && (vendorKey.trim() != null && vendorKey.trim().length()
					// > 0)
					&& (csmID != -1)
					&& (sdpID != null && sdpID.trim().length() > 0)
					&& (reqType != null && reqType.trim().length() > 0)
					&& (status != null && status.trim().length() > 0)) {

				rowID = VendorProvisioningDB.insertRecord(vendorID, vendorKey,
						csmID, sdpID, reqType, status, msgXml);

			}

			xmlResponseDoc = InsertVPSRecordResponseDocument.Factory
					.newInstance();
			InsertVPSRecordResponse responseXml = xmlResponseDoc
					.addNewInsertVPSRecordResponse();
			responseXml.setRowID(rowID);

		} catch (XmlException e) {

			e.printStackTrace();
		}

		return xmlResponseDoc;
	}

	@RemoteMethod
	@LocalMethod
	public RetrieveRetryStatusResponseDocument getCanRetry(
			XmlObject requestXmlObject) {
		// TODO : implement
		RetrieveRetryStatusRequest xmlRequestDoc = null;
		RetrieveRetryStatusResponseDocument xmlResponseDoc = null;
		String vendorID = null;
		String vendorCode = null;
		boolean retryStatus = false;

		try {
			xmlRequestDoc = RetrieveRetryStatusRequestDocument.Factory.parse(
					requestXmlObject.xmlText()).getRetrieveRetryStatusRequest();
			vendorID = xmlRequestDoc.getVendorID();
			vendorCode = xmlRequestDoc.getVendorCode();

			if ((vendorID != null && vendorID.trim().length() > 0)
					&& (vendorCode != null && vendorCode.trim().length() > 0)) {
				retryStatus = VendorStatusCodeDB.getCanRetry(vendorID,
						vendorCode);
			}

			xmlResponseDoc = RetrieveRetryStatusResponseDocument.Factory
					.newInstance();
			RetrieveRetryStatusResponse responseXml = xmlResponseDoc
					.addNewRetrieveRetryStatusResponse();
			responseXml.setRetryStatus(retryStatus);

		} catch (XmlException e) {

			e.printStackTrace();
		}

		return xmlResponseDoc;
	}

	@RemoteMethod
	@LocalMethod
	public InsertVPSStatusWithVndrCodeResponseDocument setStatusWithVendorCode(
			XmlObject requestXmlObject) {
		InsertVPSStatusWithVndrCodeRequest xmlRequestDoc = null;
		InsertVPSStatusWithVndrCodeResponseDocument xmlResponseDoc = null;
		String rowID = "-1";
		String sdpID = null;
		String newStatus = null;
		String vendorCode = null;

		try {
			xmlRequestDoc = InsertVPSStatusWithVndrCodeRequestDocument.Factory
					.parse(requestXmlObject.xmlText())
					.getInsertVPSStatusWithVndrCodeRequest();
			sdpID = xmlRequestDoc.getSDPID();
			newStatus = xmlRequestDoc.getStatus();
			vendorCode = xmlRequestDoc.getVendorCode();
			if ((sdpID != null && sdpID.trim().length() > 0)
					&& (newStatus != null && newStatus.trim().length() > 0)
					&& (vendorCode != null && vendorCode.trim().length() > 0)) {
				rowID = VendorProvisioningDB.setStatus(sdpID, newStatus,
						vendorCode);
			}
			xmlResponseDoc = InsertVPSStatusWithVndrCodeResponseDocument.Factory
					.newInstance();
			InsertVPSStatusWithVndrCodeResponse responseXml = xmlResponseDoc
					.addNewInsertVPSStatusWithVndrCodeResponse();
			responseXml.setRowID(Integer.valueOf(rowID));
		} catch (XmlException e) {

			e.printStackTrace();
		}

		return xmlResponseDoc;
	}

	@RemoteMethod
	@LocalMethod
	public void updateVPSStatus(XmlObject requestXmlObject) {
		InsertVPSStatusRequest xmlRequestDoc = null;
		String sdpID = null;
		String newStatus = null;

		try {
			xmlRequestDoc = InsertVPSStatusRequestDocument.Factory.parse(
					requestXmlObject.xmlText()).getInsertVPSStatusRequest();

			sdpID = xmlRequestDoc.getSDPID();
			newStatus = xmlRequestDoc.getStatus();

			if ((sdpID != null && sdpID.trim().length() > 0)
					&& (newStatus != null && newStatus.trim().length() > 0)) {
				VendorProvisioningDB.setStatus(sdpID, newStatus);
			}
		} catch (XmlException e) {

			e.printStackTrace();
		}
		return;
	}

	@RemoteMethod
	@LocalMethod
	public ServiceResult updateOrderStatus(BBYOrder bbyorder)
			throws ApplicationException {
		BBYOrder xmlRequestDoc = null;
		ServiceResult serviceResult = null;
		SDPOrderDB orderDB = new SDPOrderDB();
		try {
			if (bbyorder == null) {
				throw new SDPInternalException("20040020", "Request is null");
			} else {
				try {
					xmlRequestDoc = BBYOrderDocument.Factory.parse(
							bbyorder.xmlText()).getBBYOrder();
					serviceResult = orderDB.updateOrderStatus(xmlRequestDoc);
				} catch (XmlException xmle) {
					Logger.logStackTrace(xmle.fillInStackTrace());
					throw new SDPInternalException("20040021",
							"Error while parsing xml document. ");
				}
			}
		} catch (SDPInternalException e) {
			serviceResult = ServiceResult.Factory.newInstance();
			serviceResult.setStatusCode(BigInteger.ONE);
			serviceResult.setErrorCode(e.getErrorCode());
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode(e.getErrorCode());
			errorDtl.setMoreDetail(e.getErrorMessage() + " :: "
					+ e.getMessage());
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(e.getErrorMessage() + " :: " + e.getMessage());
			serviceResult.setErrorSeverity("critical");
			e.printStackTrace();
		}
		return serviceResult;
	}

	@RemoteMethod
	@LocalMethod
	public ServiceResult updateOrderStatus(XmlObject xmlObject)
			throws ApplicationException {
		BBYOrder xmlRequestDoc = null;
		ServiceResult serviceResult = null;
		SDPOrderDB orderDB = new SDPOrderDB();
		try {
			if (xmlObject == null) {
				throw new SDPInternalException("20040020", "Request is null");
			} else {
				try {
					xmlRequestDoc = BBYOrderDocument.Factory.parse(
							xmlObject.xmlText()).getBBYOrder();
					serviceResult = orderDB.updateOrderStatus(xmlRequestDoc);
				} catch (XmlException xmle) {
					Logger.logStackTrace(xmle.fillInStackTrace());
					throw new SDPInternalException("20040021",
							"Error while parsing xml document. ");
				}
			}
		} catch (SDPInternalException e) {
			serviceResult = ServiceResult.Factory.newInstance();
			serviceResult.setStatusCode(BigInteger.ONE);
			serviceResult.setErrorCode(e.getErrorCode());
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode(e.getErrorCode());
			errorDtl.setMoreDetail(e.getErrorMessage() + " :: "
					+ e.getMessage());
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp.setOriginal(e.getErrorMessage() + " :: " + e.getMessage());
			serviceResult.setErrorSeverity("critical");
			e.printStackTrace();
		}
		return serviceResult;
	}
	
	@RemoteMethod
	@LocalMethod
	public ServiceResult updateOrderStatus(String lineItemId, String sdpId, String transactionStatusCode)
			throws ApplicationException {
		ServiceResult serviceResult = null;
		SDPOrderDB orderDB = new SDPOrderDB();
		serviceResult = orderDB.updateOrderStatus(lineItemId, sdpId, transactionStatusCode);
		return serviceResult;
	}
}
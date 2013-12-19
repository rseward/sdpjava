package com.bestbuy.sdp.order;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hamcrest.core.IsNot;

import com.accenture.xml.sdp.bby.om.attribute.Attribute;
import com.accenture.xml.sdp.bby.om.bbyOrder.BBYOrder;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail.ErrorDescription;
import com.accenture.xml.sdp.bby.utilities.identifier.ArrayofExternalID;
import com.accenture.xml.sdp.bby.utilities.identifier.ExternalID;
import com.bestbuy.schemas.sdp.db.createOrder.CreateOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.insertRequestResponseLog.ReqResLogRequestDocument;
import com.bestbuy.schemas.sdp.db.insertRequestResponseLog.ReqResLogRequestDocument.ReqResLogRequest;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrder.SearchType.SearchCriteria.Enum;
import com.bestbuy.schemas.sdp.db.retrieveOrderByAttributes.FivePartKeyType;
import com.bestbuy.schemas.sdp.db.retrieveOrderByAttributes.RetrieveOrderByAttributesRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrderByAttributes.RetrieveOrderByAttributesResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrderByAttributes.SearchCriteriaType;
import com.bestbuy.schemas.sdp.db.updateOrder.UpdateOrderResponseDocument;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.order.bean.BBYOrderBean;
import com.bestbuy.sdp.order.bean.RetrieveOrderVO;
import com.bestbuy.sdp.order.helper.CommsatLogHelper;
import com.bestbuy.sdp.order.helper.CreateOrderHelper;
import com.bestbuy.sdp.order.helper.InsertExceptionHelper;
import com.bestbuy.sdp.order.helper.MerchantLinkTokenLogHelper;
import com.bestbuy.sdp.order.helper.RetrieveOrderHelper;
import com.bestbuy.sdp.order.helper.UpdateOrderHelper;
import com.bestbuy.sdp.services.SdpDBConstants;
import com.bestbuy.sdp.services.exception.DuplicateOrderExistsException;
import com.bestbuy.sdp.services.exception.SDPInternalException;

public class SDPOrderDB {

	// Subscription, order and customer variables.
	// SDP_ORDER
	private String sdpOrderId = null, sdpId = null, triggerSku = null,
			externalId = null, parentSku = null, transTmsp = null,
			transId = null, transDt = null, storeId = null, regId = null,
			lineId = null, lineItemId = null, busKeyType = null, busKey = null,
			primSkuPrc = null, primSkuTax = null, primSkuTaxRate = null,
			prntSkuPrc = null, prntSkuTax = null, prntSkuTaxRate = null,
			qnty = null, valPkgId = null, srcSysId = null, confId = null,
			keyCode = null, masterItemId = null, digitalVndrId = null,
			vndrPrdId = null, vndrId = null, prodType = null, catalogId = null,
			cnclRsnCode = null, transactionStatusCode = null, recStatCde = null,
			errorCde = null;

	// SDP_CUSTOMER
	private String sdpCustId = null, custId = null, custMailTxt = null,
			lastName = null, firstName = null, midName = null, addrLn1 = null,
			addrLn2 = null, bldAddrLblTxt = null, city = null, country = null,
			ccExpDt = null, ccName = null, ccNum = null, ccType = null,
			phNo = null, phAddrLbl = null, pstCode = null, stateCde = null,
			dlvrEmailTxt = null, rewardZoneId = null;

	// SDP_SUBSCRIPTION
	private String contractId = null, contractEndDt = null,
			contractStCde = null;

	// attribute and externalId list
	private List<Attribute> attrList = null;
	private List<ExternalID> extIdList = null;

	// variables for response
	private String reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
	private String svErrorCde = null;
	private String errorMsg = null;

	private BBYOrderBean populateBBYOrder(BBYOrder order) {
		BBYOrderBean bean = null;

		bean = new BBYOrderBean(order);
		// Logger.log(bean.toString());
		// SDP order
		sdpOrderId = bean.getSdpOrderId();
		sdpId = bean.getSdpId();
		triggerSku = bean.getTriggerSku();
		externalId = bean.getExternalId();
		parentSku = bean.getParentSku();
		transTmsp = bean.getTransTmstp();
		transId = bean.getTransId();
		transDt = bean.getTransDt();
		storeId = bean.getStoreId();
		regId = bean.getRegId();
		lineId = bean.getLineId();
		lineItemId = bean.getLineItemId();
		busKeyType = bean.getBusKeyType();
		busKey = bean.getBusKey();
		primSkuPrc = bean.getPrimSkuPrc();
		primSkuTax = bean.getPrimSkuTax();
		primSkuTaxRate = bean.getPrimSkuTaxRate();
		prntSkuPrc = bean.getPrntSkuPrc();
		prntSkuTax = bean.getPrntSkuTax();
		prntSkuTaxRate = bean.getPrntSkuTaxRate();
		qnty = bean.getQnty();
		valPkgId = bean.getValPkgId();
		srcSysId = bean.getSrcSysId();
		confId = bean.getConfId();
		keyCode = bean.getKeyCode();
		masterItemId = bean.getMasterItemId();
		digitalVndrId = bean.getDigitialVndrId();
		vndrPrdId = bean.getVndrPrdId();
		vndrId = bean.getVndrId();
		prodType = bean.getProdType();
		catalogId = bean.getCatalogId();
		sdpCustId = bean.getSdpCustId();
		cnclRsnCode = bean.getCnclRsnCode();
		transactionStatusCode = bean.getOrderStatusCode();

		recStatCde = bean.getRecStatCde();
		// Commented by Krapa : bbyorder is not havins placeholder for
		// errorCode.
		// errorCde = bean.getErrorCde();

		// sdp customer
		custId = bean.getCustId();
		custMailTxt = bean.getCustMailTxt();
		lastName = bean.getLastName();
		midName = bean.getMidName();
		firstName = bean.getFirstName();
		addrLn1 = bean.getAddrLn1();
		addrLn2 = bean.getAddrLn2();
		bldAddrLblTxt = bean.getBldAddrLblTxt();
		city = bean.getCity();
		country = bean.getCountry();
		ccExpDt = bean.getCcExpDt();
		ccName = bean.getCcName();
		ccNum = bean.getCcNum();
		ccType = bean.getCcType();
		phNo = bean.getPhNo();
		phAddrLbl = bean.getPhAddrLbl();
		pstCode = bean.getPstCode();
		stateCde = bean.getStateCde();
		dlvrEmailTxt = bean.getDlvrEmailTxt();
		rewardZoneId = bean.getRewardZoneId();

		// subscription
		contractId= bean.getContractId();
		contractEndDt = bean.getContractEndDt();
		contractStCde = bean.getContractStCde();

		// external id and attribute list
		extIdList = bean.getExtIdList();
		attrList = bean.getAttrList();

		return bean;
	}

	/**
	 * Creates Order in SDP database
	 * 
	 * @param createOrderDocument
	 * @return
	 */
	/**
	 * @param bbyOrder
	 * @return
	 */
	public CreateOrderResponseDocument createOrder(BBYOrder bbyOrder) {

		CreateOrderResponseDocument response = null;
		Connection conn = null;
		CreateOrderHelper createOrderHelper = new CreateOrderHelper();

		try {

			// Parse Request
			if (bbyOrder != null) {
				populateBBYOrder(bbyOrder);
			} else {
				throw new SDPInternalException("20040001",
						"Request message is null", null);
			}

			conn = RuntimeEnvironment.getConn();
			setAutoCommit(conn, false);

			boolean hasCustomerData = false;

			// Retrieves Keycode & Confirmationm id if Order exists in database
			RetrieveOrderVO retrieveOrderVO = createOrderHelper
					.retrieveSDPOrderDetails(conn, lineItemId);

			// Check id SDP Order already exist in database
			if (retrieveOrderVO != null) {

				ArrayofExternalID externalIds = bbyOrder.getBBYOfferArray(0)
						.getVendorOfferArray(0).getBaseProductCollection()
						.getSubCategoryArray(0).getTypeArray(0)
						.getProductArray(0).getProductIdentifier()
						.getExternalIDCollection();

				// SDP_ID
				bbyOrder
						.getBBYOfferArray(0)
						.getVendorOfferArray(0)
						.getVendorOfferIdentifier()
						.setConnect4SubscriptionOfferID(
								String.valueOf(retrieveOrderVO.getSdpOrderId()));

				// ORD_STAT_CDE
				bbyOrder.getBBYOfferArray(0).getVendorOfferArray(0).setStatus(
						retrieveOrderVO.getOrderStatus());

				ExternalID keyCodeExternalId = null;
				if (externalIds.getExternalIDList() != null) {
					for (ExternalID externalID : externalIds
							.getExternalIDList()) {
						if ("KeyCode".equals(externalID.getType())) {
							keyCodeExternalId = externalID;
						}
					}

				}

				if (retrieveOrderVO.getKeyCode() != null) {
					if (keyCodeExternalId == null) {

						ExternalID extId = externalIds.addNewExternalID();
						extId.setID(retrieveOrderVO.getKeyCode());
						extId.setType("KeyCode");

					} else {
						keyCodeExternalId.setID(retrieveOrderVO.getKeyCode());
					}

				}
				
/* Added section to include SerialNumber for a duplicate request */
				
				ExternalID serialNumberExternalId = null;
				if (externalIds.getExternalIDList() != null) {
					for (ExternalID externalID : externalIds
							.getExternalIDList()) {
						if ("SerialNumber".equals(externalID.getType())) {
							serialNumberExternalId = externalID;
						}
					}

				}

				if (retrieveOrderVO.getBusinessKeyType() != null) {
					if ("SerialNumber".equals(retrieveOrderVO.getBusinessKeyType()) && retrieveOrderVO.getBusinessKey() != null){
						if (serialNumberExternalId == null) {
							ExternalID extId = externalIds.addNewExternalID();
							extId.setID(retrieveOrderVO.getBusinessKey());
							extId.setType("SerialNumber");

						} else {
							serialNumberExternalId.setID(retrieveOrderVO.getBusinessKey());
						}
					}
				}
/* End check for SerialNumber */
					
				if (retrieveOrderVO.getConfirmationCode() != null) {

					ExternalID extId = externalIds.addNewExternalID();
					extId.setID(retrieveOrderVO.getConfirmationCode());
					extId.setType("ConfirmationCode");

				}

				// Insert customer Info if sdpCustomerId is not present in
				// database
				if (retrieveOrderVO.getSdpCustomerId() == null) {

					if (custId != null || custMailTxt != null
							|| lastName != null || firstName != null
							|| midName != null) {
						hasCustomerData = true;
					}

					if (hasCustomerData) {

						sdpCustId = createOrderHelper.insertSDPCustomer(conn,
								sdpCustId, custId, custMailTxt, lastName,
								firstName, midName, addrLn1, addrLn2,
								bldAddrLblTxt, city, country, ccExpDt, ccName,
								ccNum, ccType, phNo, phAddrLbl, pstCode,
								stateCde, dlvrEmailTxt, recStatCde,
								rewardZoneId);

						bbyOrder.getCustomer().setC4CustomerID(sdpCustId);

					}
				} else {

					bbyOrder.getCustomer().setC4CustomerID(
							retrieveOrderVO.getSdpCustomerId());

				}

				if (checkNotNull(sdpId)
						&& checkNotNull(retrieveOrderVO.getSdpId())
						&& !sdpId.equals(retrieveOrderVO.getSdpId())
						&& checkNotNull(retrieveOrderVO.getOrderStatus())
						&& "4".equals(retrieveOrderVO.getOrderStatus())) {

					reqStatus = SdpDBConstants.ORDER_STATUS_DUPLICATE;

					svErrorCde = "20040017";

					errorMsg = "Duplicate SDP Order inprocess. LineItemId = "
							+ lineItemId;

				} else {

					reqStatus = SdpDBConstants.ORDER_STATUS_DUPLICATE;
					svErrorCde = "20040002";

					errorMsg = "SDP Order already exists with lineItemId = "
							+ lineItemId;
				}

				response = createOrderHelper.generateResponse(reqStatus,
						svErrorCde, errorMsg, retrieveOrderVO.getSdpOrderId(),
						sdpCustId, bbyOrder);

				return response;
			}

			// Check if customer data is present in request
			if (custId != null || custMailTxt != null || lastName != null
					|| firstName != null || midName != null) {
				hasCustomerData = true;
			}

			// Commented by Krapa: isNotNull always false
			// if (isNotNull(custId) || isNotNull(custMailTxt)
			// || isNotNull(lastName) || isNotNull(firstName) ||
			// isNotNull(midName)) {
			// hasCustomerData = true;
			// }

			if (hasCustomerData) {

				Logger
						.log("sdpCustId is null. Generating one and updating bbyOrder.");

				sdpCustId = createOrderHelper.insertSDPCustomer(conn,
						sdpCustId, custId, custMailTxt, lastName, firstName,
						midName, addrLn1, addrLn2, bldAddrLblTxt, city,
						country, ccExpDt, ccName, ccNum, ccType, phNo,
						phAddrLbl, pstCode, stateCde, dlvrEmailTxt, recStatCde,
						rewardZoneId);

				bbyOrder.getCustomer().setC4CustomerID(sdpCustId);
			}

			// Set SDP Order Id

			Logger.log("going to insert order");

			sdpOrderId = createOrderHelper.insertSDPOrder(conn, sdpOrderId,
					sdpId, triggerSku, externalId, parentSku, transTmsp,
					transId, transDt, storeId, regId, lineId, lineItemId,
					busKeyType, busKey, primSkuPrc, primSkuTax, primSkuTaxRate,
					prntSkuPrc, prntSkuTax, prntSkuTaxRate, qnty, valPkgId,
					srcSysId, confId, keyCode, masterItemId, vndrPrdId, vndrId,
					catalogId, sdpCustId, cnclRsnCode, transactionStatusCode,
					recStatCde, errorCde);

			bbyOrder.getBBYOfferArray(0).getVendorOfferArray(0)
					.getVendorOfferIdentifier().setConnect4SubscriptionOfferID(
							sdpOrderId);

			if (contractId != null) {
				Logger.log("going to insert insertSubscriptionData");
				createOrderHelper.insertSDPSubscription(conn, sdpOrderId,
						contractId, contractStCde, contractEndDt);
			}

			if (attrList != null && attrList.size() > 0) {
				for (Attribute attr : attrList) {
					createOrderHelper.insertSDPOrderDetails(conn, sdpOrderId,
							attr.getName(), attr.getValue());
				}
			}

			if (extIdList != null && extIdList.size() > 0) {
				for (ExternalID extId : extIdList) {
					createOrderHelper.insertSDPOrderDetails(conn, sdpOrderId,
							extId.getType(), extId.getID());
				}
			}

			// commit connection only if all the details were entered
			conn.commit();
			reqStatus = SdpDBConstants.ORDER_STATUS_SUCCESS;
			setAutoCommit(conn, true);
			releaseConnection(conn);

		} catch (DuplicateOrderExistsException e) {
			reqStatus = SdpDBConstants.ORDER_STATUS_DUPLICATE;
			svErrorCde = "20040002";
			errorMsg = "SDP Order already exists.";
			Logger.logStackTrace(e.fillInStackTrace());
			setAutoCommit(conn, true);
			releaseConnection(conn);
		} catch (SQLException e) {
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = "20040016";
			errorMsg = "SDP DB issue. " + e.getMessage();
			Logger.logStackTrace(e.fillInStackTrace());
			rollBack(conn);
			setAutoCommit(conn, true);
			releaseConnection(conn);
		} catch (SDPInternalException e) {
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = "20040001";
			errorMsg = e.getMessage();
			Logger.logStackTrace(e.fillInStackTrace());
			rollBack(conn);
			setAutoCommit(conn, true);
			releaseConnection(conn);
		} finally {
			setAutoCommit(conn, true);
			releaseConnection(conn);
			response = createOrderHelper.generateResponse(reqStatus,
					svErrorCde, errorMsg, sdpOrderId, sdpCustId, bbyOrder);
			// Logger.log(response.xmlText());
		}
		return response;
	}

	private boolean checkNotNull(String value) {
		return value != null & value.trim().length() > 0;
	}

	public UpdateOrderResponseDocument updateOrder(BBYOrder bbyOrder,
			String errorCode) {

		// Logger.log("SDPOrderDB.updateOrder()");
		UpdateOrderResponseDocument response = null;
		UpdateOrderHelper updateOrderHelper = new UpdateOrderHelper();
		Connection conn = null;

		try {
			if (bbyOrder != null) {
				populateBBYOrder(bbyOrder);
				errorCde = errorCode;
			} else {
				throw new SDPInternalException("20040003",
						"Method = updateOrder() :: Request message is null",
						null);
			}

			if (sdpOrderId != null && sdpOrderId.trim().length() > 0) {
				conn = RuntimeEnvironment.getConn();
				setAutoCommit(conn, false);

				if (sdpCustId != null && sdpCustId.trim().length() > 0) {
					updateOrderHelper.updateSDPCustomer(conn, sdpCustId,
							custId, custMailTxt, lastName, firstName, midName,
							addrLn1, addrLn2, bldAddrLblTxt, city, country,
							ccExpDt, ccName, ccNum, ccType, phNo, phAddrLbl,
							pstCode, stateCde, dlvrEmailTxt, recStatCde,
							rewardZoneId);
				}

				updateOrderHelper.updateSDPOrder(conn, sdpOrderId, sdpId,
						triggerSku, externalId, parentSku, transTmsp, transId,
						transDt, storeId, regId, lineId, lineItemId,
						busKeyType, busKey, primSkuPrc, primSkuTax,
						primSkuTaxRate, prntSkuPrc, prntSkuTax, prntSkuTaxRate,
						qnty, valPkgId, srcSysId, confId, keyCode,
						masterItemId, vndrPrdId, vndrId, catalogId, sdpCustId,
						cnclRsnCode, transactionStatusCode, recStatCde, errorCde);

				if (contractId != null && contractId.trim().length() > 0) {
					updateOrderHelper.updateSDPSubscription(conn, sdpOrderId,
							contractId, contractStCde, contractEndDt,
							recStatCde);
				}

				if (attrList != null && attrList.size() > 0) {
					for (Attribute attr : attrList) {
						updateOrderHelper.updateSDPOrderDetails(conn,
								sdpOrderId, trimString(attr.getName()),
								trimString(attr.getValue()), recStatCde);
					}
				}

				if (extIdList != null && extIdList.size() > 0) {
					for (ExternalID extId : extIdList) {
						updateOrderHelper.updateSDPOrderDetails(conn,
								sdpOrderId, trimString(extId.getType()),
								trimString(extId.getID()), recStatCde);
					}
				}

				conn.commit();

				reqStatus = SdpDBConstants.ORDER_STATUS_SUCCESS;
			} else {
				throw new SDPInternalException("20040004",
						"Method = updateOrder() :: Order Not found", null);
			}
			setAutoCommit(conn, true);
			releaseConnection(conn);

		} catch (SQLException e) {
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = "20040016";
			errorMsg = "Method = updateOrder() :: SDP DB issue. "
					+ e.getMessage();
			Logger.logStackTrace(e.fillInStackTrace());
			rollBack(conn);
			setAutoCommit(conn, true);
			releaseConnection(conn);
		} catch (SDPInternalException e) {
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = e.getErrorCode();
			// svErrorCde = "20040003";
			errorMsg = "Method = updateOrder() :: " + e.getMessage();
			Logger.logStackTrace(e.fillInStackTrace());
			rollBack(conn);
			setAutoCommit(conn, true);
			releaseConnection(conn);
		} finally {
			setAutoCommit(conn, true);
			releaseConnection(conn);
			response = updateOrderHelper.generateResponse(reqStatus,
					svErrorCde, errorMsg);
			// Logger.log(response.xmlText());
		}
		return response;
	}

	public RetrieveOrderResponseDocument retrieveOrder(
			RetrieveOrderRequestDocument requestDocument) {

		RetrieveOrderResponseDocument retrieveOrderResponse = null;
		Connection conn = null;
		try {
			conn = RuntimeEnvironment.getConn();
			RetrieveOrderHelper rtrvOrderHlp = new RetrieveOrderHelper();
			if (requestDocument == null) {
				throw new SDPInternalException("20040005",
						"Method = retrieveOrder() :: Request is null. ");
			}

			Enum searchCriteria = requestDocument.getRetrieveOrderRequest()
					.getSearchCollection().getSearch().getSearchCriteria();

			String searchCriteriaStr = null;

			String searchValue = requestDocument.getRetrieveOrderRequest()
					.getSearchCollection().getSearch().getSearchValue();

			if (searchCriteria == null || searchValue == null
					|| searchValue.trim().length() == 0) {
				throw new SDPInternalException("20040005",
						"Method = retrieveOrder() :: SearchCriteria is null. ");
			} else {
				searchCriteriaStr = searchCriteria.toString();
			}

			retrieveOrderResponse = rtrvOrderHlp.retrieveOrder(conn,
					searchCriteriaStr, searchValue);
			releaseConnection(conn);

		} catch (SDPInternalException e) {
			String reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			String svErrorCde = e.getErrorCode();
			String errorDscp = e.getErrorMessage();
			retrieveOrderResponse = RetrieveOrderHelper
					.generateRetrieveOrderResponse(reqStatus, svErrorCde,
							errorDscp);
			releaseConnection(conn);
		} finally {
			releaseConnection(conn);
		}
		return retrieveOrderResponse;
	}

	public RetrieveOrderByAttributesResponseDocument retrieveOrderByAttributes(
			RetrieveOrderByAttributesRequestDocument retrieveOrderRequest) {

		RetrieveOrderByAttributesResponseDocument retrieveOrderResponse = null;
		String transId = null;
		String transDt = null;
		String storeId = null;
		String regId = null;
		String lineId = null;
		String sku = null;
		String serialNum = null;

		try {
			RetrieveOrderHelper rtrvOrderHlp = new RetrieveOrderHelper();
			if (retrieveOrderRequest != null) {

				SearchCriteriaType searchCriteriaType = retrieveOrderRequest
						.getRetrieveOrderByAttributesRequest()
						.getSearchCollection().getSearchCriteria();

				FivePartKeyType fivePartKey = searchCriteriaType
						.getFivePartKey();

				if (fivePartKey != null) {

					if ((fivePartKey.getDate() != null && fivePartKey.getDate()
							.trim().length() > 0)
							&& (fivePartKey.getStoreID() != null && fivePartKey
									.getStoreID().trim().length() > 0)
							&& (fivePartKey.getRegisterID() != null && fivePartKey
									.getRegisterID().trim().length() > 0)
							&& (fivePartKey.getTransactionID() != null && fivePartKey
									.getTransactionID().trim().length() > 0)) {

						transDt = fivePartKey.getDate().trim();
						storeId = fivePartKey.getStoreID().trim();
						regId = fivePartKey.getRegisterID().trim();
						transId = fivePartKey.getTransactionID().trim();

						if (fivePartKey.getLineID() != null
								&& fivePartKey.getLineID().trim().length() > 0) {

							lineId = fivePartKey.getLineID().trim();

						} else if (searchCriteriaType.getSKU() != null 
								&& searchCriteriaType.getSKU().trim().length() > 0) {

							sku = searchCriteriaType.getSKU().trim();

							if (searchCriteriaType.getSerialNumber() != null
									&& searchCriteriaType.getSerialNumber()
											.trim().length() > 0) {

								serialNum = searchCriteriaType
										.getSerialNumber().trim();
							}
						} else {
							throw new SDPInternalException("20040005",
									"Method = retrieveOrderByAttributes() :: Invalid Request. --> LineId / SKU ");
						}

					}  else if ((fivePartKey.getStoreID() !=null 
							&& fivePartKey.getStoreID().trim().length()> 0) 
							&& (searchCriteriaType.getSerialNumber() !=null 
							&& searchCriteriaType.getSerialNumber().trim().length()> 0) 
							&& fivePartKey.getStoreID().trim().equalsIgnoreCase("960")) {
						storeId = fivePartKey.getStoreID().trim();
						serialNum = searchCriteriaType
						.getSerialNumber().trim();
					} else {
						throw new SDPInternalException(
								"20040005",
								"Method = retrieveOrderByAttributes() :: Invalid Request --> transId, transDt, storeId and regId required in request ");
					}
				} else {
					throw new SDPInternalException(
							"20040005",
							"Method = retrieveOrderByAttributes() :: Invalid SearchCriteria --> Five Part Key is missing ");
				}
			} else {
				throw new SDPInternalException("20040005",
						"Method = retrieveOrderByAttributes() :: Request is null. ");
			}

			retrieveOrderResponse = rtrvOrderHlp.retrieveOrderByAttributes(
					transId, transDt, storeId, regId, lineId, sku, serialNum);
			

		} catch (SDPInternalException e) {
			String reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			String svErrorCde = e.getErrorCode();
			String errorDscp = e.getErrorMessage();
			retrieveOrderResponse = RetrieveOrderHelper
					.generateRetrieveOrderByAttributesResponse(reqStatus,
							svErrorCde, errorDscp);
		}
		return retrieveOrderResponse;
	}

	public void createSDPReqRespLog(ReqResLogRequestDocument xmlRequestDoc)
			throws SDPInternalException, SQLException {

		String rqstMsg = null;
		String respMsg = null;
		String rqstType = null;
		String profileId = null;
		String transactionStatusCode = null;
		Connection conn = null;

		CreateOrderHelper createOrderHelper = new CreateOrderHelper();
		try {
			// Logger.log("Inside sdpOrderDb class :: \n\n :: Request xml
			// ::"+xmlRequestDoc);

			conn = RuntimeEnvironment.getConn();
			if (xmlRequestDoc.getReqResLogRequest().getLineItemId() != null
					&& xmlRequestDoc.getReqResLogRequest().getLineItemId()
							.trim().length() > 0) {
				lineItemId = xmlRequestDoc.getReqResLogRequest()
						.getLineItemId().trim();
			} else {
				lineItemId = null;
			}
			if (xmlRequestDoc.getReqResLogRequest().getSdpOrderId() != null
					&& xmlRequestDoc.getReqResLogRequest().getSdpOrderId()
							.trim().length() > 0) {
				sdpOrderId = xmlRequestDoc.getReqResLogRequest()
						.getSdpOrderId().trim();
			} else {
				sdpOrderId = null;
			}
			if (xmlRequestDoc.getReqResLogRequest().getSdpId() != null
					&& xmlRequestDoc.getReqResLogRequest().getSdpId().trim()
							.length() > 0) {
				sdpId = xmlRequestDoc.getReqResLogRequest().getSdpId().trim();
			} else {
				throw new SDPInternalException("",
						"Method = createSDPReqRespLog() :: SdpId in Request Xml is null");
			}
			if (xmlRequestDoc.getReqResLogRequest().getRequestType() != null
					&& xmlRequestDoc.getReqResLogRequest().getRequestType()
							.trim().length() > 0) {
				rqstType = xmlRequestDoc.getReqResLogRequest().getRequestType()
						.trim();
			} else {
				throw new SDPInternalException("",
						"Method = createSDPReqRespLog() :: RequestType in Request Xml is null");
			}

			if (xmlRequestDoc.getReqResLogRequest().getProfileId() != null
					&& xmlRequestDoc.getReqResLogRequest().getProfileId()
							.trim().length() > 0) {
				profileId = xmlRequestDoc.getReqResLogRequest().getProfileId()
						.trim();
			} else {
				profileId = "1";
			}
			if (xmlRequestDoc.getReqResLogRequest().getSourceSystemId() != null
					&& xmlRequestDoc.getReqResLogRequest().getSourceSystemId()
							.trim().length() > 0) {
				srcSysId = xmlRequestDoc.getReqResLogRequest()
						.getSourceSystemId().trim();
			} else {
				srcSysId = null;
			}

			if (xmlRequestDoc.getReqResLogRequest().getRequestMessage() != null
					&& xmlRequestDoc.getReqResLogRequest().getRequestMessage()
							.toString().trim().length() > 0) {
				rqstMsg = xmlRequestDoc.getReqResLogRequest()
						.getRequestMessage().toString().trim();
			} else {
				rqstMsg = null;
			}
			if (xmlRequestDoc.getReqResLogRequest().getResponseMessage() != null
					&& xmlRequestDoc.getReqResLogRequest().getResponseMessage()
							.toString().trim().length() > 0) {
				respMsg = xmlRequestDoc.getReqResLogRequest()
						.getResponseMessage().toString().trim();
			} else {
				respMsg = null;
			}

			if (xmlRequestDoc.getReqResLogRequest().getTransactionStatusCode() != null
					&& xmlRequestDoc.getReqResLogRequest().getTransactionStatusCode()
							.trim().length() > 0) {
				transactionStatusCode = xmlRequestDoc.getReqResLogRequest()
						.getTransactionStatusCode().trim();
			} else {
				transactionStatusCode = null;
			}

			createOrderHelper.insertSDPRequestResponseLog(conn, lineItemId,
					sdpOrderId, sdpId, rqstType, profileId, srcSysId, rqstMsg,
					respMsg, transactionStatusCode);

			conn.commit();
			releaseConnection(conn);
		} finally {
			releaseConnection(conn);
		}
	}

	public void updateSDPReqRespLog(ReqResLogRequestDocument xmlRequestDoc)
			throws SDPInternalException, SQLException {

		String rqstMsg = null;
		String respMsg = null;
		String rqstType = null;
		String profileId = null;
		String transactionStatusCode = null;
		Connection conn = null;

		UpdateOrderHelper updateOrderHelper = new UpdateOrderHelper();
		try {

			conn = RuntimeEnvironment.getConn();
			ReqResLogRequest reqResLogRequest = xmlRequestDoc
					.getReqResLogRequest();
			if (reqResLogRequest.getLineItemId() != null
					&& reqResLogRequest.getLineItemId().trim().length() > 0) {
				lineItemId = reqResLogRequest.getLineItemId().trim();
			} else {
				lineItemId = null;
			}
			if (reqResLogRequest.getSdpOrderId() != null
					&& reqResLogRequest.getSdpOrderId().trim().length() > 0) {
				sdpOrderId = reqResLogRequest.getSdpOrderId().trim();
			} else {
				sdpOrderId = null;
			}
			if (reqResLogRequest.getSdpId() != null
					&& reqResLogRequest.getSdpId().trim().length() > 0) {
				sdpId = reqResLogRequest.getSdpId().trim();
			} else {
				throw new SDPInternalException("",
						"Method = updateSDPReqRespLog() :: SdpId in Request Xml is null");
			}
			if (reqResLogRequest.getRequestType() != null
					&& reqResLogRequest.getRequestType().trim().length() > 0) {
				rqstType = reqResLogRequest.getRequestType().trim();
			} else {
				rqstType = null;
			}

			if (reqResLogRequest.getProfileId() != null
					&& reqResLogRequest.getProfileId().trim().length() > 0) {
				profileId = reqResLogRequest.getProfileId().trim();
			} else {
				profileId = "1";
			}
			if (reqResLogRequest.getSourceSystemId() != null
					&& reqResLogRequest.getSourceSystemId().trim().length() > 0) {
				srcSysId = reqResLogRequest.getSourceSystemId().trim();
			} else {
				srcSysId = null;
			}

			if (reqResLogRequest.getRequestMessage() != null
					&& reqResLogRequest.getRequestMessage().toString().trim()
							.length() > 0) {
				rqstMsg = reqResLogRequest.getRequestMessage().toString()
						.trim();
			} else {
				rqstMsg = null;
			}
			if (reqResLogRequest.getResponseMessage() != null
					&& reqResLogRequest.getResponseMessage().toString().trim()
							.length() > 0) {
				respMsg = reqResLogRequest.getResponseMessage().toString()
						.trim();
			} else {
				respMsg = null;
			}

			if (reqResLogRequest.getTransactionStatusCode() != null
					&& reqResLogRequest.getTransactionStatusCode().trim().length() > 0) {
				transactionStatusCode = reqResLogRequest.getTransactionStatusCode().trim();
			} else {
				throw new SDPInternalException("",
						"Method = updateSDPReqRespLog() :: orderStatusCode in Request Xml is null");
			}

			updateOrderHelper.updateSDPRequestResponseLog(conn, lineItemId,
					sdpOrderId, sdpId, rqstType, profileId, srcSysId, rqstMsg,
					respMsg, transactionStatusCode);

			conn.commit();
			releaseConnection(conn);
		} finally {
			releaseConnection(conn);
		}
	}

	public void createException(XmlObject requestMsg, ServiceResult srvResult,
			String sdpIdArg, String sdpOrderIdArg, String srcSysIdArg)
			throws SDPInternalException, XmlException, SQLException {

		ErrorDetail detail = null;
		String rqstMsg = null;
		String stckTrace = null;
		String errCde = null;

		Connection conn = RuntimeEnvironment.getConn();
		InsertExceptionHelper insertExceptionHelper = new InsertExceptionHelper();
		try {
			if (requestMsg != null) {

				rqstMsg = requestMsg.toString();
				// Commented by Krapa : request type changed to anytype from
				// bbyorder
				// populateBBYOrder(bbyOrder);
				if (srvResult != null) {
					recStatCde = srvResult.getStatusCode().toString();
					detail = srvResult.getErrorDetailList()
							.getErrorDetailList().get(0);
					if ((detail.getMoreDetail() == null || detail
							.getMoreDetail().trim().length() == 0)
							&& detail.getErrorDescription() != null) {
						stckTrace = trimString(detail.getErrorDescription()
								.getOriginal());
					} else {
						stckTrace = trimString(detail.getMoreDetail());
					}

					errCde = trimString(detail.getErrorCode());
				} else {
					throw new SDPInternalException("",
							"Method = InsertException() ::  ServiceResult is null");
				}
				// Profile Type-- 1 for BBYOrder

				if (sdpIdArg != null && sdpIdArg.trim().length() > 0) {
					sdpId = sdpIdArg;
				} else {
					throw new IllegalArgumentException(
							"Method = InsertException() :: SDPID is null");
				}

				if (sdpOrderIdArg != null && sdpOrderIdArg.trim().length() > 0) {
					sdpOrderId = sdpOrderIdArg;
				} else {
					sdpOrderId = null;
				}

				if (srcSysIdArg != null && srcSysIdArg.trim().length() > 0) {
					srcSysId = srcSysIdArg;
				} else {
					srcSysId = null;
				}

				insertExceptionHelper.insertSDPException(conn, sdpId,
						sdpOrderId, "1", rqstMsg, stckTrace, srcSysId, errCde);
				conn.commit();
				releaseConnection(conn);

			} else {
				throw new SDPInternalException("",
						"Method = InsertException() ::  Request is null");
			}
		} finally {
			releaseConnection(conn);
		}
	}

	public void createCommsatLog(String subscriptionOfferId, String emailId,
			String redemptionCode, String templateId, String status,
			String userId, String confirmationId, String transactionDate,
			String bbyId, String lineItemId, String regId) {

		new CommsatLogHelper().insertRecord(subscriptionOfferId,
				trimString(emailId), trimString(redemptionCode),
				trimString(templateId), trimString(status), trimString(userId),
				trimString(confirmationId), trimString(transactionDate),
				trimString(bbyId), trimString(lineItemId), trimString(regId));

	}

	public void createMerchantLinkTokenLog(String subscriptionOfferId,
			String vendorID, String bBYID, String custName, String ccToken,
			String cardType, String expirationDate, String address1,
			String address2, String city, String state, String zip,
			String processedFlag) {

		new MerchantLinkTokenLogHelper().insertRecord(subscriptionOfferId,
				trimString(vendorID), trimString(bBYID), trimString(custName),
				trimString(ccToken), trimString(cardType),
				trimString(expirationDate), trimString(address1),
				trimString(address2), trimString(city), trimString(state),
				trimString(zip), trimString(processedFlag));

	}

	private void releaseConnection(final Connection pConn) {
		RuntimeEnvironment.releaseConn(pConn);
	}
	
	private void rollBack(final Connection pConn) {
		RuntimeEnvironment.rollBack(pConn);
	}
	
	private void setAutoCommit(final Connection pConn, final boolean key) {
		RuntimeEnvironment.setAutoCommit(pConn,key);
	}

	private String trimString(String val) {
		if (val == null) {
			return val;
		}
		return val.trim();
	}

	public void createException(BBYOrder bbyOrderReq, ServiceResult srvResult)
			throws SDPInternalException {

		ErrorDetail detail = null;
		String rqstMsg = null;
		String stckTrace = null;
		String errCde = null;

		Connection conn = RuntimeEnvironment.getConn();

		InsertExceptionHelper insertExceptionHelper = new InsertExceptionHelper();

		try {
			if (bbyOrderReq != null) {
				rqstMsg = bbyOrderReq.toString();

				if (srvResult != null) {

					detail = srvResult.getErrorDetailList()
							.getErrorDetailList().get(0);

					stckTrace = trimString(detail.getMoreDetail());
					errCde = trimString(detail.getErrorCode());
				} else {
					throw new SDPInternalException("",
							"Method = InsertException() ::  ServiceResult is null");
				}
				// Profile Type-- 1 for BBYOrder

				// populate sdpid, sdporderid, sysid from bbyorder

				if (bbyOrderReq.getBBYOfferArray(0) != null) {
					if (bbyOrderReq.getBBYOfferArray(0).getVendorOfferArray(0) != null) {

						if (bbyOrderReq.getBBYOfferArray(0)
								.getVendorOfferArray(0)
								.getVendorOfferIdentifier() != null) {
							if (bbyOrderReq.getBBYOfferArray(0)
									.getVendorOfferArray(0)
									.getVendorOfferIdentifier()
									.getConnect4SubscriptionOfferID() != null
									&& bbyOrderReq.getBBYOfferArray(0)
											.getVendorOfferArray(0)
											.getVendorOfferIdentifier()
											.getConnect4SubscriptionOfferID()
											.trim().length() > 0) {
								sdpOrderId = bbyOrderReq.getBBYOfferArray(0)
										.getVendorOfferArray(0)
										.getVendorOfferIdentifier()
										.getConnect4SubscriptionOfferID()
										.trim();
							}
						} else {
							sdpOrderId = null;
						}

						if (bbyOrderReq.getBBYOfferArray(0)
								.getVendorOfferArray(0)
								.getBaseProductCollection()
								.getSubCategoryArray(0).getTypeArray(0)
								.getProductArray(0) != null) {
							if (bbyOrderReq.getBBYOfferArray(0)
									.getVendorOfferArray(0)
									.getBaseProductCollection()
									.getSubCategoryArray(0).getTypeArray(0)
									.getProductArray(0).getProductDetails() != null) {

								if (bbyOrderReq.getBBYOfferArray(0)
										.getVendorOfferArray(0)
										.getBaseProductCollection()
										.getSubCategoryArray(0).getTypeArray(0)
										.getProductArray(0).getProductDetails()
										.getMoreDetails().getAttributeList() != null) {

									int extSize = bbyOrderReq.getBBYOfferArray(
											0).getVendorOfferArray(0)
											.getBaseProductCollection()
											.getSubCategoryArray(0)
											.getTypeArray(0).getProductArray(0)
											.getProductDetails()
											.getMoreDetails()
											.getAttributeList().size();

									for (int i = 0; i < extSize; i++) {
										String attString = bbyOrderReq
												.getBBYOfferArray(0)
												.getVendorOfferArray(0)
												.getBaseProductCollection()
												.getSubCategoryArray(0)
												.getTypeArray(0)
												.getProductArray(0)
												.getProductDetails()
												.getMoreDetails()
												.getAttributeList().get(i)
												.getName().trim();

										if (attString.equalsIgnoreCase("SDPID")) {
											sdpId = bbyOrderReq
													.getBBYOfferArray(0)
													.getVendorOfferArray(0)
													.getBaseProductCollection()
													.getSubCategoryArray(0)
													.getTypeArray(0)
													.getProductArray(0)
													.getProductDetails()
													.getMoreDetails()
													.getAttributeList().get(i)
													.getValue().trim();
										}
									}
								}
							}
						} else {
							throw new SDPInternalException("",
									"Method = InsertException() :: SdpId is null");
						}

					}
				}

				if (bbyOrderReq.getTransactionInformation().getActingParty() != null) {
					if (bbyOrderReq.getTransactionInformation()
							.getActingParty().getPartyID() != null
							&& bbyOrderReq.getTransactionInformation()
									.getActingParty().getPartyID().trim()
									.length() > 0)
						srcSysId = bbyOrderReq.getTransactionInformation()
								.getActingParty().getPartyID().trim();
				} else {
					srcSysId = null;
				}

				insertExceptionHelper.insertSDPException(conn, sdpId,
						sdpOrderId, "1", rqstMsg, stckTrace, srcSysId, errCde);
				//
				// //UpdateOrder using BBYOrder If sdpOrderId is not null.
				// if(sdpOrderId != null && sdpOrderId.trim().length() > 0){
				// errorCde = trimString(errCde);
				// updateOrder(bbyOrderReq);
				// }

				conn.commit();

			} else {
				throw new SDPInternalException("",
						"Method = InsertException() ::  Request is null");
			}
		} catch (SQLException e) {
			releaseConnection(conn);
			throw new SDPInternalException("", "Method = InsertException() :: "
					+ e.getMessage());

		} finally {
			releaseConnection(conn);
		}
	}

	public ServiceResult updateOrderStatus(BBYOrder bbyOrder) {

		ServiceResult serviceResult = ServiceResult.Factory.newInstance();
		Connection conn = null;

		try {
			// Parse Request
			if (bbyOrder != null) {
				populateBBYOrder(bbyOrder);
			} else {
				throw new SDPInternalException("20040019",
						"Method = updateOrderStatus() :: bbyOrder is null",
						null);
			}

			conn = RuntimeEnvironment.getConn();

			PreparedStatement ps = null;

			String sql = "UPDATE SDP_ORDER SET ORD_STAT_CDE= ? , SDP_ID = ?, REC_UPD_TS =  CURRENT_TIMESTAMP  WHERE SDP_ORDER_ID = ?";

			CreateOrderHelper createOrderHelper = new CreateOrderHelper();

			// retrieves SDP_ID * Status
			RetrieveOrderVO retrieveOrderVO;

			retrieveOrderVO = createOrderHelper.retrieveSDPOrderDetails(conn,
					lineItemId);

			// Check id SDP Order already exist in database
			if (retrieveOrderVO != null) {

				if (checkNotNull(sdpId)
						&& checkNotNull(retrieveOrderVO.getSdpId())
						&& !sdpId.equals(retrieveOrderVO.getSdpId())
						&& checkNotNull(retrieveOrderVO.getOrderStatus())
						&& "4".equals(retrieveOrderVO.getOrderStatus())) {

					reqStatus = SdpDBConstants.ORDER_STATUS_DUPLICATE;

					svErrorCde = "20040017";

					errorMsg = "Duplicate SDP Order inprocess. LineItemId = "
							+ lineItemId + ", SDPId = "
							+ retrieveOrderVO.getSdpId();

					throw new SDPInternalException(svErrorCde, errorMsg, null);
				}

			}

			ps = conn.prepareStatement(sql);

			ps.setString(1, transactionStatusCode);
			ps.setString(2, sdpId);
			ps.setString(3, sdpOrderId);

			int result = ps.executeUpdate();

			serviceResult.setStatusCode(BigInteger.ZERO);
			releaseConnection(conn);

		} catch (SQLException e) {
			serviceResult.setStatusCode(BigInteger.ONE);

			serviceResult.setErrorCode("20040016");
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode("20040016");
			errorDtl
					.setMoreDetail("Method = updateOrderStatus(), SQLException :: "
							+ " :: " + e.getMessage());
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp
					.setOriginal("Method = updateOrderStatus(), SQLException :: "
							+ " :: " + e.getMessage());
			serviceResult.setErrorSeverity("critical");
			releaseConnection(conn);

		} catch (SDPInternalException e) {
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
			releaseConnection(conn);
		}finally{
			releaseConnection(conn);
		}

		return serviceResult;
	}

	public ServiceResult updateOrderStatus(String lineItemId2, String sdpId2,
			String transactionStatusCode2) {
		ServiceResult serviceResult = ServiceResult.Factory.newInstance();
		Connection conn = null;

		try {
			// Parse Request
			if ( checkNotNull(lineItemId2) && checkNotNull(sdpId2) &&checkNotNull(transactionStatusCode2)) {
				this.lineItemId = lineItemId2;
				this.sdpId = sdpId2;
				this.transactionStatusCode = transactionStatusCode2;
			} else {
				throw new SDPInternalException("20040019",
						"Method = updateOrderStatus() :: lineItemId or/and sdpId or/and transactionStatusCode is null",
						null);
			}

			conn = RuntimeEnvironment.getConn();

			PreparedStatement ps = null;

			String sql = "UPDATE SDP_ORDER SET ORD_STAT_CDE= ? , SDP_ID = ?, REC_UPD_TS =  CURRENT_TIMESTAMP  WHERE LN_ITEM_ID = ?";

			CreateOrderHelper createOrderHelper = new CreateOrderHelper();

			// retrieves SDP_ID * Status
			RetrieveOrderVO retrieveOrderVO;

			retrieveOrderVO = createOrderHelper.retrieveSDPOrderDetails(conn,
					lineItemId);

			// Check id SDP Order already exist in database
			if (retrieveOrderVO != null) {

				if (checkNotNull(sdpId)
						&& checkNotNull(retrieveOrderVO.getSdpId())
						&& !sdpId.equals(retrieveOrderVO.getSdpId())
						&& checkNotNull(retrieveOrderVO.getOrderStatus())
						&& "4".equals(retrieveOrderVO.getOrderStatus())) {

					reqStatus = SdpDBConstants.ORDER_STATUS_DUPLICATE;

					svErrorCde = "20040017";

					errorMsg = "Duplicate SDP Order inprocess. LineItemId = "
							+ lineItemId + ", SDPId = "
							+ retrieveOrderVO.getSdpId();

					throw new SDPInternalException(svErrorCde, errorMsg, null);
				}

			}

			ps = conn.prepareStatement(sql);

			ps.setString(1, transactionStatusCode);
			ps.setString(2, sdpId);
			ps.setString(3, lineItemId);

			int result = ps.executeUpdate();

			serviceResult.setStatusCode(BigInteger.ZERO);
			releaseConnection(conn);

		} catch (SQLException e) {
			serviceResult.setStatusCode(BigInteger.ONE);

			serviceResult.setErrorCode("20040016");
			ErrorDetailList detail = serviceResult.addNewErrorDetailList();
			ErrorDetail errorDtl = detail.addNewErrorDetail();
			errorDtl.setErrorCode("20040016");
			errorDtl
					.setMoreDetail("Method = updateOrderStatus(), SQLException :: "
							+ " :: " + e.getMessage());
			ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
			errorDsp
					.setOriginal("Method = updateOrderStatus(), SQLException :: "
							+ " :: " + e.getMessage());
			serviceResult.setErrorSeverity("critical");
			releaseConnection(conn);

		} catch (SDPInternalException e) {
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
			releaseConnection(conn);
		}finally{
			releaseConnection(conn);
		}

		return serviceResult;
	}
}
package com.bestbuy.sdp.catalog;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;

import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail.ErrorDescription;
import com.bestbuy.schemas.sdp.db.retrieveCatalog.RetrieveCatalogOfferRequestDocument;
import com.bestbuy.schemas.sdp.db.retrieveCatalog.RetrieveCatalogOfferResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveTemplateIDByDigitalAttr.RetrieveTemplateIDByDigitalAttrResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveTemplateIDByDigitalAttr.RetrieveTemplateIDByDigitalAttrResponseDocument.RetrieveTemplateIDByDigitalAttrResponse;
import com.bestbuy.schemas.sdp.db.retrieveTemplateIDBySKU.RetrieveTemplateIDBySKUResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveTemplateIDBySKU.RetrieveTemplateIDBySKUResponseDocument.RetrieveTemplateIDBySKUResponse;
import com.bestbuy.sdp.catalog.helper.CommsatTemplateProvider;
import com.bestbuy.sdp.catalog.helper.RetrieveCatalogOfferHelper;
import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.services.SdpDBConstants;

public class SDPCatalogDB {

	public RetrieveCatalogOfferResponseDocument retrieveCatalogBySKU(RetrieveCatalogOfferRequestDocument requestDocument){

		RetrieveCatalogOfferResponseDocument response = null;

		String skuId = null;
		String offerType = null;
		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		Connection conn = null;
		RetrieveCatalogOfferHelper rtrvCtlgOffHlp = new RetrieveCatalogOfferHelper();
		try{
			if(requestDocument != null){
				if(requestDocument.getRetrieveCatalogOfferRequest().getSkuID() != null &&
						requestDocument.getRetrieveCatalogOfferRequest().getSkuID().trim().length() > 0){
					skuId = requestDocument.getRetrieveCatalogOfferRequest().getSkuID().trim();
				}else{
					reqStatus = SdpDBConstants.FAILURE;
					svErrorCde = "20040009";
					errorDscp = "Method = retrieveCatalogBySKU() :: SkuId is mandatory." ;
					response = RetrieveCatalogOfferHelper.generateRetrieveCatalogResponse(reqStatus, svErrorCde, errorDscp);
					//System.out.println(response);
					return response;
				}
				if(requestDocument.getRetrieveCatalogOfferRequest().getOfferType() != null
						&& requestDocument.getRetrieveCatalogOfferRequest().getOfferType().trim().length() > 0){
					offerType = requestDocument.getRetrieveCatalogOfferRequest().getOfferType().trim();
				}
				conn = RuntimeEnvironment.getConn();
				response = rtrvCtlgOffHlp.retrieveCatalogBySku(skuId, offerType, conn);

				}else{
				reqStatus = SdpDBConstants.FAILURE;
				svErrorCde = "20040008";
				errorDscp = "Method = retrieveCatalogBySKU() :: Request is null";
				response = RetrieveCatalogOfferHelper.generateRetrieveCatalogResponse(reqStatus, svErrorCde, errorDscp);
			}
		}finally{
			releaseConnection(conn);
		}
		return response;
	}

	public RetrieveCatalogOfferResponseDocument retrieveCatalogByDigitalAttributes(RetrieveCatalogOfferRequestDocument requestDocument){

		RetrieveCatalogOfferResponseDocument response = null;
		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		String masterVendorId = null;
		String prodType = null;
		RetrieveCatalogOfferHelper rtrvCtlgOffHlp = new RetrieveCatalogOfferHelper();
		Connection conn  = null;
		try{
			if(requestDocument != null){
				if((requestDocument.getRetrieveCatalogOfferRequest().getMasterVendorId() != null
						&& requestDocument.getRetrieveCatalogOfferRequest().getMasterVendorId().trim().length() > 0)
						&& ( requestDocument.getRetrieveCatalogOfferRequest().getProductType() != null
								&&  requestDocument.getRetrieveCatalogOfferRequest().getProductType().trim().length() > 0)){
					masterVendorId = requestDocument.getRetrieveCatalogOfferRequest().getMasterVendorId().trim();
					prodType = requestDocument.getRetrieveCatalogOfferRequest().getProductType().trim();
				}else{
					reqStatus = SdpDBConstants.FAILURE;
					svErrorCde = "20040012";
					errorDscp ="Method = retrieveCatalogByDigitalAttributes() :: Vendor Id  and Prod Type are mandatory. " ;
					return RetrieveCatalogOfferHelper.generateRetrieveCatalogResponse(reqStatus, svErrorCde, errorDscp);
				}
				conn = RuntimeEnvironment.getConn();
				response = rtrvCtlgOffHlp.retrieveCatalogByDigitalAttributes(masterVendorId, prodType, conn);
				releaseConnection(conn);
			}else{
				reqStatus = SdpDBConstants.FAILURE;
				svErrorCde = "200400011";
				errorDscp = "Method = retrieveCatalogByDigitalAttributes() :: Request is null";
				response = RetrieveCatalogOfferHelper.generateRetrieveCatalogResponse(reqStatus, svErrorCde, errorDscp);
			}
		}finally{
			releaseConnection(conn);
		}
		return response;
	}

	public String getTemplateId(String sku, String dateString) throws SQLException {

		return new CommsatTemplateProvider().getTemplateId(sku, dateString);

	}

	public String getTemplateId(String masterVendorId, String prodType, String dateString) throws SQLException {

		return new CommsatTemplateProvider().getTemplateId(masterVendorId, prodType, dateString);

	}

	private void releaseConnection(final Connection pConn) {
	    RuntimeEnvironment.releaseConn(pConn);
	}

	public static RetrieveTemplateIDBySKUResponseDocument generateTempIdBySkuRes(String templateId, String reqStatus, String svErrorCde, String errorDscp){

		RetrieveTemplateIDBySKUResponseDocument  response = RetrieveTemplateIDBySKUResponseDocument.Factory.newInstance();
		RetrieveTemplateIDBySKUResponse  rtvTempIdResponse= response.addNewRetrieveTemplateIDBySKUResponse();

		ServiceResult serviceResult = null;
		serviceResult = rtvTempIdResponse.addNewServiceResult();

		// check status code
		if(reqStatus != null){
			if(reqStatus.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_SUCCESS)){
				rtvTempIdResponse.setTemplateID(templateId);
				serviceResult.setStatusCode(BigInteger.ZERO);
			}else if(reqStatus.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_FAILURE)){
				serviceResult.setStatusCode(BigInteger.ONE);
				serviceResult.setErrorCode(svErrorCde);
				ErrorDetailList detail = serviceResult.addNewErrorDetailList();
				ErrorDetail errorDtl = detail.addNewErrorDetail();
				errorDtl.setMoreDetail(errorDscp);
				errorDtl.setErrorCode(svErrorCde);
				ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
				errorDsp.setOriginal(errorDscp);
			}
		}else{
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

	public static RetrieveTemplateIDByDigitalAttrResponseDocument generateTempIdByDigitalAttrRes(String templateId, String reqStatus, String svErrorCde, String errorDscp){

		RetrieveTemplateIDByDigitalAttrResponseDocument  response = RetrieveTemplateIDByDigitalAttrResponseDocument.Factory.newInstance();
		RetrieveTemplateIDByDigitalAttrResponse  rtvTempIdResponse= response.addNewRetrieveTemplateIDByDigitalAttrResponse();

		ServiceResult serviceResult = null;
		serviceResult = rtvTempIdResponse.addNewServiceResult();

		// check status code
		if(reqStatus != null){
			if(reqStatus.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_SUCCESS)){
				rtvTempIdResponse.setTemplateID(templateId);
				serviceResult.setStatusCode(BigInteger.ZERO);
			}else if(reqStatus.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_FAILURE)){
				serviceResult.setStatusCode(BigInteger.ONE);
				serviceResult.setErrorCode(svErrorCde);
				ErrorDetailList detail = serviceResult.addNewErrorDetailList();
				ErrorDetail errorDtl = detail.addNewErrorDetail();
				errorDtl.setErrorCode(svErrorCde);
				errorDtl.setMoreDetail(errorDscp);
				ErrorDescription errorDsp = errorDtl.addNewErrorDescription();
				errorDsp.setOriginal(errorDscp);
			}
		}else{
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

}

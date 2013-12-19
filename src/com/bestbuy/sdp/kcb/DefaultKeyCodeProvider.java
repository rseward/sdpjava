package com.bestbuy.sdp.kcb;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;

import com.accenture.common.ex.ApplicationException;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail.ErrorDescription;
import com.bestbuy.schemas.sdp.tpa.kcb.ReleaseKeyCodeResponse;
import com.bestbuy.schemas.sdp.tpa.kcb.ReleaseKeyCodeResponseDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeResponse;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeResponseDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.UpdateKeyCodeResponse;
import com.bestbuy.schemas.sdp.tpa.kcb.UpdateKeyCodeResponseDocument;
import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.services.SdpDBConstants;

public class DefaultKeyCodeProvider implements KeyCodeProvider 
{	
	public KeyCode reserve(String vendorID, String productSKU, String orderID) throws ApplicationException,SQLException
	{
		Connection conn = null;
		try{
			conn = RuntimeEnvironment.getConn(RuntimeEnvironment.KCB_DATASOURCE_JNDI_NAME);
			KeyCodeBank kcb = new KeyCodeBank(conn);
			KeyCode kc = new KeyCode();
			kc.setVendorID(vendorID);
			kc.setSKU(productSKU);
			kc.setOrderID(orderID);
			
			return kcb.reserve(kc);
		}finally{
			RuntimeEnvironment.releaseConn(conn);
		}
	}
	
	public KeyCode reserveByMasterItemId(String vendorID, String masterItemId, String orderID, String status) throws ApplicationException,SQLException
	{
		Connection conn = null;
		try{
			conn = RuntimeEnvironment.getConn(RuntimeEnvironment.KCB_DATASOURCE_JNDI_NAME);
			KeyCodeBank kcb = new KeyCodeBank(conn);
			KeyCode kc = new KeyCode();
			kc.setVendorID(vendorID);
			kc.setMasterItemId(masterItemId);
			kc.setOrderID(orderID);
			kc.setStatus(status);
			return kcb.reserveByMasterItemId(kc);
		}finally{
			RuntimeEnvironment.releaseConn(conn);
		}
	}
	
	public boolean update(KeyCode keyCode) throws ApplicationException,SQLException
	{
		Connection conn = null;
		try{
			conn = RuntimeEnvironment.getConn(RuntimeEnvironment.KCB_DATASOURCE_JNDI_NAME);
			KeyCodeBank kcb = new KeyCodeBank(conn);
			
			return kcb.update(keyCode);
		}finally{
			RuntimeEnvironment.releaseConn(conn);
		}
	}
	
	public void release(KeyCode keyCode) throws ApplicationException,SQLException
	{
		Connection conn = null;
		try{
			conn = RuntimeEnvironment.getConn(RuntimeEnvironment.KCB_DATASOURCE_JNDI_NAME);
			KeyCodeBank kcb = new KeyCodeBank(conn);
			
			kcb.release(keyCode);
			return;	
		}finally{
			RuntimeEnvironment.releaseConn(conn);
		}
	}
	
	
	public static ReserveKeyCodeResponseDocument generateReserveKeyResponse(String reqStatus, String svErrorCde, String errorDscp){
		
		ReserveKeyCodeResponseDocument xmlResponseDoc = ReserveKeyCodeResponseDocument.Factory.newInstance();;
		ReserveKeyCodeResponse xmlResponse = xmlResponseDoc.addNewReserveKeyCodeResponse(); 
		
		ServiceResult serviceResult = null;
		serviceResult = xmlResponse.addNewServiceResult();
		
		// check status code
		if(reqStatus != null){
			if(reqStatus.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_SUCCESS)){
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
		return xmlResponseDoc;
	}
	
	public static UpdateKeyCodeResponseDocument generateUpdateKeyResponse(String reqStatus, String svErrorCde, String errorDscp){
		
		UpdateKeyCodeResponseDocument xmlResponseDoc = UpdateKeyCodeResponseDocument.Factory.newInstance();
		UpdateKeyCodeResponse xmlResponse = xmlResponseDoc.addNewUpdateKeyCodeResponse();
		
		ServiceResult serviceResult = null;
		serviceResult = xmlResponse.addNewServiceResult();
		
		// check status code
		if(reqStatus != null){
			if(reqStatus.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_SUCCESS)){
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
		return xmlResponseDoc;
	}
	
	public static ReleaseKeyCodeResponseDocument generateReleaseKeyResponse(String reqStatus, String svErrorCde, String errorDscp){
		
		ReleaseKeyCodeResponseDocument xmlResponseDoc = ReleaseKeyCodeResponseDocument.Factory.newInstance();
		ReleaseKeyCodeResponse xmlResponse = xmlResponseDoc.addNewReleaseKeyCodeResponse();
		
		ServiceResult serviceResult = null;
		serviceResult = xmlResponse.addNewServiceResult();
		
		// check status code
		if(reqStatus != null){
			if(reqStatus.equalsIgnoreCase(SdpDBConstants.ORDER_STATUS_SUCCESS)){
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
		return xmlResponseDoc;
	}
}
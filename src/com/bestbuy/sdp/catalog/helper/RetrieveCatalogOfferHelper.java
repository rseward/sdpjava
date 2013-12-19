package com.bestbuy.sdp.catalog.helper;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleTypes;

import com.accenture.xml.sdp.bby.om.attribute.ArrayOfSDPAttribute;
import com.accenture.xml.sdp.bby.om.attribute.Attribute;
import com.accenture.xml.sdp.bby.om.product.Product;
import com.accenture.xml.sdp.bby.om.product.ProductDetails;
import com.accenture.xml.sdp.bby.om.productCollection.ProductCollection;
import com.accenture.xml.sdp.bby.om.subCategory.SubCategory;
import com.accenture.xml.sdp.bby.om.type.Type;
import com.accenture.xml.sdp.bby.om.vendorOffer.VendorOffer;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult.ErrorDetailList.ErrorDetail.ErrorDescription;
import com.accenture.xml.sdp.bby.utilities.identifier.ArrayofExternalID;
import com.accenture.xml.sdp.bby.utilities.identifier.ExternalID;
import com.accenture.xml.sdp.bby.utilities.identifier.ProductIdentifier;
import com.accenture.xml.sdp.bby.utilities.identifier.VendorOfferIdentifier;
import com.bestbuy.schemas.sdp.db.retrieveCatalog.RetrieveCatalogOfferResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveCatalog.RetrieveCatalogOfferResponseDocument.RetrieveCatalogOfferResponse;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.services.SdpDBConstants;

public class RetrieveCatalogOfferHelper {


	public RetrieveCatalogOfferResponseDocument retrieveCatalogBySku(
			String skuId, String offerType, Connection conn) {


		/*
		// retrieve Catalog details
		// Commented By Krapa Nawab : Stored Procedures are going to be used to retrieve data from database
		String sqlCommand = "select a.ctlg_id, a.offr_typ, a.prm_sku_desc, a.rec_stat_cde, " +
							"a.prm_sku_id, a.prnt_sku_id, a.cat, a.sub_cat, a.vndr_trig_sku_id, " +
							"a.role, a.vndr_id, a.svc_prvdr_id, b.attr_key, b.attr_val " +
							"from sdp_catalog_details a join sdp_ctlg_workflow_attr b " +
							"on a.ctlg_id = b.ctlg_id " +
							"where a.prm_sku_id = ?";

		// Offer type is Optional field
		if (offerType != null && offerType.trim().length() > 0) {
			sqlCommand = sqlCommand + " and a.offr_typ = ?";
		}
		*/
		CallableStatement proc = null;

		ResultSet rs = null;
		// Create
		VendorOffer vndrOffer = VendorOffer.Factory.newInstance();

		VendorOfferIdentifier vndrOffrIden = vndrOffer.addNewVendorOfferIdentifier();

		ProductCollection prodCollection = vndrOffer.addNewBaseProductCollection();

		SubCategory subCat = prodCollection.addNewSubCategory();

		Type type = subCat.addNewType();

		Product product = type.addNewProduct();
		ProductIdentifier prodIden = product.addNewProductIdentifier();

		ArrayofExternalID extIdArry = prodIden.addNewExternalIDCollection();

		ProductDetails prodDetails = product.addNewProductDetails();
		ArrayOfSDPAttribute arrayOfAttr = prodDetails.addNewMoreDetails();

		ServiceResult svResult = ServiceResult.Factory.newInstance();

		RetrieveCatalogOfferResponseDocument response = RetrieveCatalogOfferResponseDocument.Factory.newInstance();
		RetrieveCatalogOfferResponse rtrCtlResponse = response.addNewRetrieveCatalogOfferResponse();

		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		boolean hasError = false;
		if(skuId.trim() != null && skuId.trim().length() > 0){

			try {
				if (offerType != null && offerType.trim().length() >0) {
					proc = conn.prepareCall("{call RTA.retrieveCatalogBySku(?,?,?) }");
				} else {
					proc = conn.prepareCall("{call RTA.retrieveCatalogBySku(?,?) }");
				}

				int i=0;

				proc.setString(++i, skuId);
				if (offerType != null && offerType.trim().length() >0) {
					proc.setString(++i, offerType);
				}
				proc.registerOutParameter(++i, OracleTypes.CURSOR);

				proc.executeQuery();
				rs = (ResultSet) proc.getObject(i);

				boolean foundRecords = false;

				if (rs != null ) {
					Attribute attr= null;
					while (rs.next()) {
						if(!foundRecords) {

							foundRecords = true;
							//updated column name TO DSP_NM
							vndrOffrIden.setName(rs.getString("dsp_nm"));
							vndrOffrIden.setConnect4CatalogueID(rs.getString("ctlg_id"));

							vndrOffer.setCategory(rs.getString("cat"));
							vndrOffer.setOfferSubCategory(rs.getString("sub_cat"));
							//Commented by Logesh. rec_stat_cde column is used to check if SKU is active
							//vndrOffer.setStatus(rs.getString("rec_stat_cde"));

							ExternalID extId = null;
							if(rs.getString("prm_sku_id") != null){
								extId = extIdArry.addNewExternalID();
								extId.setType("TriggerSku");
								extId.setID(rs.getString("prm_sku_id"));
							}

							if(rs.getString("vndr_trig_sku_id") != null){
								extId = extIdArry.addNewExternalID();
								extId.setType("VendorTriggerSku");
								extId.setID(rs.getString("vndr_trig_sku_id"));
							}
							
							if(rs.getString("prnt_sku_id") != null){
								attr = arrayOfAttr.addNewAttribute();
								attr.setName("PlanSku");
								attr.setValue(rs.getString("prnt_sku_id"));
							}

							if(rs.getString("vndr_id") != null){
								attr = arrayOfAttr.addNewAttribute();
								attr.setName("VendorID");
								attr.setValue(rs.getString("vndr_id"));
							}
							//commented by Krapa: Not needed no column added as adaptorId
		//					attr = arrayOfAttr.addNewAttribute();
		//					attr.setName("AdaptorID");
		//					attr.setValue(rs.getString("adaptor_id"));

						}
						if(rs.getString("attr_key") != null && rs.getString("attr_val") != null){
							attr = arrayOfAttr.addNewAttribute();
							attr.setName(rs.getString("attr_key"));
							attr.setValue(rs.getString("attr_val"));
						}
					}
				}

				if (foundRecords) {
					svResult.setStatusCode(BigInteger.ZERO);
					rtrCtlResponse.setServiceResult(svResult);

				} else {
					hasError = true;
					reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
					svErrorCde = "20040010";
					errorDscp ="Offer not found :: SkuId " + skuId ;
				}
			} catch (SQLException e) {
				hasError = true;
				reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
				svErrorCde = "20040016";
				errorDscp = "SDP DB issue. " + e.getMessage() ;
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}else{
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = "20040009";
			errorDscp = "SkuId is mandatory." ;
		}
		// build service result


		if (!hasError) {
			rtrCtlResponse.setVendorOffer(vndrOffer);
		}else{
			response = generateRetrieveCatalogResponse(reqStatus, svErrorCde, errorDscp);
		}

		//Logger.log(rtrCtlResponse);
		return response;
	}

	public RetrieveCatalogOfferResponseDocument retrieveCatalogByDigitalAttributes(
			String masterVndrId, String prodType, Connection conn) {

		String reqStatus = null;
		String svErrorCde = null;
		String errorDscp = null;
		boolean hasError = false;
		/*
		// retrieve Catalog details
		// Commented By Krapa Nawab : Stored Procedures are going to be used to retrieve data from database
		String sqlCommand = "select a.ctlg_id, a.offr_typ, a.prm_sku_desc, a.rec_stat_cde, " +
							"a.prm_sku_id, a.prnt_sku_id, a.cat, a.sub_cat, a.vndr_trig_sku_id, " +
							"a.role, a.vndr_id, a.svc_prvdr_id, b.attr_key, b.attr_val " +
							"from sdp_catalog_details a join sdp_ctlg_workflow_attr b " +
							"on a.ctlg_id = b.ctlg_id " +
							"where a.prm_sku_id = ?";

		// Offer type is Optional field
		if (offerType != null && offerType.trim().length() > 0) {
			sqlCommand = sqlCommand + " and a.offr_typ = ?";
		}
		*/
		CallableStatement proc = null;

		ResultSet rs = null;
		// Create
		VendorOffer vndrOffer = VendorOffer.Factory.newInstance();

		VendorOfferIdentifier vndrOffrIden = vndrOffer.addNewVendorOfferIdentifier();

		ProductCollection prodCollection = vndrOffer.addNewBaseProductCollection();

		SubCategory subCat = prodCollection.addNewSubCategory();

		Type type = subCat.addNewType();

		Product product = type.addNewProduct();
		ProductIdentifier prodIden = product.addNewProductIdentifier();

		ArrayofExternalID extIdArry = prodIden.addNewExternalIDCollection();

		ProductDetails prodDetails = product.addNewProductDetails();
		ArrayOfSDPAttribute arrayOfAttr = prodDetails.addNewMoreDetails();

		ServiceResult svResult = ServiceResult.Factory.newInstance();

		RetrieveCatalogOfferResponseDocument response = RetrieveCatalogOfferResponseDocument.Factory.newInstance();
		RetrieveCatalogOfferResponse rtrCtlResponse = response.addNewRetrieveCatalogOfferResponse();


		if((masterVndrId.trim() != null && masterVndrId.trim().length() > 0)
				&& (prodType.trim() != null && prodType.trim().length() > 0)){
			try {

					proc = conn.prepareCall("{call RTA.retrieveCatalogByDigitalAtrs(?,?,?) }");

				int i=0;

				proc.setString(++i, masterVndrId);
				proc.setString(++i, prodType);
				proc.registerOutParameter(++i, OracleTypes.CURSOR);

				proc.executeQuery();
				rs = (ResultSet) proc.getObject(i);

				boolean foundRecords = false;

				if (rs != null ) {
					Attribute attr= null;
					while (rs.next()) {
						if(!foundRecords) {

							foundRecords = true;
							//updated column name TO DSP_NM
							vndrOffrIden.setName(rs.getString("dsp_nm"));
							vndrOffrIden.setConnect4CatalogueID(rs.getString("ctlg_id"));

							vndrOffer.setCategory(rs.getString("cat"));
							vndrOffer.setOfferSubCategory(rs.getString("sub_cat"));
							//Commented by Logesh. rec_stat_cde column is used to check if SKU is active
							//vndrOffer.setStatus(rs.getString("rec_stat_cde"));

							ExternalID extId = null;
							if(rs.getString("prm_sku_id") != null){
								extId = extIdArry.addNewExternalID();
								extId.setType("TriggerSku");
								extId.setID(rs.getString("prm_sku_id"));
							}

							if(rs.getString("vndr_trig_sku_id") != null){
								extId = extIdArry.addNewExternalID();
								extId.setType("VendorTriggerSku");
								extId.setID(rs.getString("vndr_trig_sku_id"));
							}

							if(rs.getString("MSTR_VNDR_ID") != null){
								extId = extIdArry.addNewExternalID();
								extId.setType("MasterVendorId");
								extId.setID(rs.getString("MSTR_VNDR_ID"));
							}

							if(rs.getString("PROD_TYP") != null){
								extId = extIdArry.addNewExternalID();
								extId.setType("DigitalProductType ");
								extId.setID(rs.getString("PROD_TYP"));
							}

							if(rs.getString("vndr_id") != null){
								attr = arrayOfAttr.addNewAttribute();
								attr.setName("VendorID");
								attr.setValue(rs.getString("vndr_id"));
							}
							//commented by Krapa: Not needed no column added as adaptorId
		//					attr = arrayOfAttr.addNewAttribute();
		//					attr.setName("AdaptorID");
		//					attr.setValue(rs.getString("adaptor_id"));

						}
						if(rs.getString("attr_key") != null && rs.getString("attr_val") != null){
							attr = arrayOfAttr.addNewAttribute();
							attr.setName(rs.getString("attr_key"));
							attr.setValue(rs.getString("attr_val"));
						}
					}
				}

				if (foundRecords) {
					svResult.setStatusCode(BigInteger.ZERO);
					rtrCtlResponse.setServiceResult(svResult);
				} else {
					hasError = true;
					reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
					svErrorCde = "20040013";
					errorDscp = "Offer not found for :: Vendor :: " + masterVndrId +" :: Product Type ::"+ prodType ;
				}
			} catch (SQLException e) {
				hasError = true;
				svResult.setStatusCode(BigInteger.ONE);
				svResult.setErrorCode("SQLException :: " + e.getMessage());
				Logger.logStackTrace(e.fillInStackTrace());
			}
		}else{
			hasError = true;
			reqStatus = SdpDBConstants.ORDER_STATUS_FAILURE;
			svErrorCde = "20040012";
			errorDscp ="Vendor Id  and Prod Type are mandatory. " ;
		}

		// build service result


		if (!hasError) {
			rtrCtlResponse.setVendorOffer(vndrOffer);
		}else{
			response = generateRetrieveCatalogResponse(reqStatus, svErrorCde, errorDscp);
		}
		//Logger.log(response);
		return response;
	}

	public static RetrieveCatalogOfferResponseDocument generateRetrieveCatalogResponse(String reqStatus, String svErrorCde, String errorDscp){

		RetrieveCatalogOfferResponseDocument  response = RetrieveCatalogOfferResponseDocument.Factory.newInstance();
		RetrieveCatalogOfferResponse  rtvCtgResponse= response.addNewRetrieveCatalogOfferResponse();

		ServiceResult serviceResult = null;
		serviceResult = rtvCtgResponse.addNewServiceResult();

		// check status code
		if(reqStatus != null){
			if(reqStatus.equalsIgnoreCase(SdpDBConstants.SUCCESS)){
				serviceResult.setStatusCode(BigInteger.ZERO);
			}else if(reqStatus.equalsIgnoreCase(SdpDBConstants.FAILURE)){
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

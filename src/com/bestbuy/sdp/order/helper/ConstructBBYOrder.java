package com.bestbuy.sdp.order.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.accenture.xml.sdp.bby.cm.creditCard.CreditCard;
import com.accenture.xml.sdp.bby.cm.customer.Customer;
import com.accenture.xml.sdp.bby.cm.customer.Name;
import com.accenture.xml.sdp.bby.om.attribute.ArrayOfSDPAttribute;
import com.accenture.xml.sdp.bby.om.attribute.Attribute;
import com.accenture.xml.sdp.bby.om.bbyOffer.BBYOffer;
import com.accenture.xml.sdp.bby.om.bbyOrder.BBYOrder;
import com.accenture.xml.sdp.bby.om.billingInformation.BillingInformation;
import com.accenture.xml.sdp.bby.om.billingInformation.Contract;
import com.accenture.xml.sdp.bby.om.product.Product;
import com.accenture.xml.sdp.bby.om.product.ProductDetails;
import com.accenture.xml.sdp.bby.om.productCollection.ProductCollection;
import com.accenture.xml.sdp.bby.om.subCategory.SubCategory;
import com.accenture.xml.sdp.bby.om.type.Type;
import com.accenture.xml.sdp.bby.om.vendorOffer.VendorOffer;
import com.accenture.xml.sdp.bby.utilities.address.Address;
import com.accenture.xml.sdp.bby.utilities.address.Demographics;
import com.accenture.xml.sdp.bby.utilities.address.TelephoneNumber;
import com.accenture.xml.sdp.bby.utilities.address.UnParsedAddress;
import com.accenture.xml.sdp.bby.utilities.identifier.ArrayofExternalID;
import com.accenture.xml.sdp.bby.utilities.identifier.BBYOfferIdentifier;
import com.accenture.xml.sdp.bby.utilities.identifier.ExternalID;
import com.accenture.xml.sdp.bby.utilities.identifier.ProductIdentifier;
import com.accenture.xml.sdp.bby.utilities.identifier.VendorOfferIdentifier;
import com.accenture.xml.sdp.bby.utilities.transactionInformation.ActingParty;
import com.accenture.xml.sdp.bby.utilities.transactionInformation.TransactionInformation;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.services.exception.SDPInternalException;

public class ConstructBBYOrder {

	BBYOrder consBBYOrder(ResultSet rs) throws SQLException,SDPInternalException {
		BBYOrder bbyOrder = null;
		if (rs != null) {
			bbyOrder = BBYOrder.Factory.newInstance();
			BBYOffer bbyOffer = BBYOffer.Factory.newInstance();
			BBYOfferIdentifier bbyOfferIdentifier = BBYOfferIdentifier.Factory
					.newInstance();
			VendorOffer vendorOffer = VendorOffer.Factory.newInstance();

			BillingInformation billing = BillingInformation.Factory
					.newInstance();
			Address billAddr = Address.Factory.newInstance();
			UnParsedAddress unPrsAddr = UnParsedAddress.Factory.newInstance();
			Demographics demoGraph = Demographics.Factory.newInstance();

			VendorOfferIdentifier vndrOffrIden = VendorOfferIdentifier.Factory
					.newInstance();
			ProductCollection prdColl = ProductCollection.Factory.newInstance();
			SubCategory subCat = SubCategory.Factory.newInstance();
			Type type = Type.Factory.newInstance();
			Product product = Product.Factory.newInstance();
			ProductIdentifier prodIden = ProductIdentifier.Factory
					.newInstance();
			ProductDetails prodDetails = ProductDetails.Factory.newInstance();

			Customer customer = Customer.Factory.newInstance();

			// source

			TransactionInformation tranInfo = bbyOrder
					.addNewTransactionInformation();
			if (rs.getString("SRC_SYS_ID") != null) {
				ActingParty actPrty = ActingParty.Factory.newInstance();
				actPrty = tranInfo.addNewActingParty();
				actPrty.setPartyID(rs.getString("SRC_SYS_ID"));
			}
			// customer name and delivery email.
			customer = bbyOrder.addNewCustomer();
			customer.setC4CustomerID(rs.getString("SDP_CUST_ID"));
			customer.setECCustomerID(rs.getString("CUST_ID"));
			if (rs.getString("RWZ_ID") != null) {
				customer.setRewardZoneNumber(rs.getString("RWZ_ID"));
			}
			customer.setEmailAddress(rs.getString("CUST_EMAIL_TXT"));

			if (rs.getString("CUST_FRST_NM") != null
					|| rs.getString("CUST_LAST_NM") != null
					|| rs.getString("CUST_MID_NM") != null) {
				Name name = Name.Factory.newInstance();
				name = customer.addNewCustomerName();
				name.setFirstName(rs.getString("CUST_FRST_NM"));
				name.setMiddleName(rs.getString("CUST_MID_NM"));
				name.setLastName(rs.getString("CUST_LAST_NM"));
			}

			if (rs.getString("PH_ADDR_LBL") != null
					|| rs.getString("PH_NBR") != null) {
				TelephoneNumber phone = TelephoneNumber.Factory.newInstance();
				phone = customer.addNewTelephoneNumber();
				phone.setLabel(rs.getString("PH_ADDR_LBL"));
				if (rs.getString("PH_NBR") != null) {
					String phno = rs.getString("PH_NBR");
					long phNum = Long.parseLong(phno.trim());
					long areCde = phNum / 10000000;
					phNum = phNum % 10000000;
					long exchCde = phNum / 10000;
					phNum = phNum % 10000;
					phone.setAreaCode(String.valueOf(areCde));
					phone.setExchCode(String.valueOf(exchCde));
					phone.setLocalNumCode(String.valueOf(phNum));
				}
			}

			bbyOffer = bbyOrder.addNewBBYOffer();

			bbyOfferIdentifier = bbyOffer.addNewBBYOfferIdentifier();
			ArrayofExternalID bbyOfferExtIdArry = bbyOfferIdentifier
					.addNewExternalIDCollection();

			// Storeid, regid, transactionid , transaction date time ,
			// transaction date and line id

			ExternalID bbyOfferExtId = null;

			if (rs.getString("TRANS_ID") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("TransactionId");
				bbyOfferExtId.setID(rs.getString("TRANS_ID"));
			}

			if (rs.getString("RGST_ID") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("RegisterId");
				bbyOfferExtId.setID(rs.getString("RGST_ID"));
			}

			if (rs.getString("STOR_ID") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("StoreId");
				bbyOfferExtId.setID(rs.getString("STOR_ID"));
			}

			if (rs.getString("TRANS_TS") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("TransactionDateTime");
				bbyOfferExtId.setID(rs.getString("TRANS_TS"));
			}

			if (rs.getString("TRANS_DT") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("Date");
				java.sql.Date transDate = stringToDateConvert(rs
						.getString("TRANS_DT"));
				bbyOfferExtId.setID(String.valueOf(transDate));
			}

			if (rs.getString("LN_ID") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("LineID");
				bbyOfferExtId.setID(rs.getString("LN_ID"));
			}

			vendorOffer = bbyOffer.addNewVendorOffer();
			vendorOffer.setStatus(rs.getString("ORD_STAT_CDE"));

			// offerID
			vndrOffrIden = vendorOffer.addNewVendorOfferIdentifier();
			vndrOffrIden.setConnect4SubscriptionOfferID(rs
					.getString("SDP_ORDER_ID"));
			vndrOffrIden.setConnect4CatalogueID(rs.getString("CTLG_ID"));
			prdColl = vendorOffer.addNewBaseProductCollection();
			subCat = prdColl.addNewSubCategory();
			type = subCat.addNewType();
			product = type.addNewProduct();

			prodIden = product.addNewProductIdentifier();

			// Commented by Krapa: PROD_TYP is not present in any of the
			// table
			// prodIden.setConnect4ProductSubscriptionID(rs.getString("PROD_TYP"));
			ArrayofExternalID prodIdenExtIdArry = prodIden
					.addNewExternalIDCollection();

			// trigger sku and business key

			ExternalID prodIdenExtId = null;

			if (rs.getString("PRM_SKU_ID") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("TriggerSku");
				prodIdenExtId.setID(rs.getString("PRM_SKU_ID"));
			}

			if (rs.getString("BSNS_KEY_TYP") != null
					|| rs.getString("BSNS_KEY") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType(rs.getString("BSNS_KEY_TYP"));
				prodIdenExtId.setID(rs.getString("BSNS_KEY"));
			}

			if (rs.getString("CONF_ID") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("ConfirmationCode");
				prodIdenExtId.setID(rs.getString("CONF_ID"));
			}

			if (rs.getString("KEY_CDE") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("KeyCode");
				prodIdenExtId.setID(rs.getString("KEY_CDE"));
			}

			if (rs.getString("MSTR_ITM_ID") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("MasterItemId");
				prodIdenExtId.setID(rs.getString("MSTR_ITM_ID"));
			}
			// Commented by Krapa : digitalVendorId is same as vendorId
			// prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
			// prodIdenExtId.setType("DigitalVendorID");
			// prodIdenExtId.setID("");

			if (rs.getString("MSTR_VNDR_ID") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("MasterVendorId");
				prodIdenExtId.setID(rs.getString("MSTR_VNDR_ID"));
			}

			if (rs.getString("PROD_TYP") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("DigitalProductType");
				prodIdenExtId.setID(rs.getString("PROD_TYP"));
			}

			if (rs.getString("VNDR_PROD_ID") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("VendorTriggerSku");
				prodIdenExtId.setID(rs.getString("VNDR_PROD_ID"));
			}

			if (rs.getString("VNDR_PROD_ID") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("VendorProductID");
				prodIdenExtId.setID(rs.getString("VNDR_PROD_ID"));
			}

			// quantity
			prodDetails = product.addNewProductDetails();

			ArrayOfSDPAttribute prdMoreDtl = prodDetails.addNewMoreDetails();
			Attribute prdArrtList = null;

			if (rs.getString("SDP_ID") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("SDPID");
				prdArrtList.setValue(rs.getString("SDP_ID"));
			}

			if (rs.getString("PRNT_SKU_ID") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("PlanSku");
				prdArrtList.setValue(rs.getString("PRNT_SKU_ID"));
			}

			if (rs.getString("PRNT_SKU_PRC") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("PlanExtPrice");
				prdArrtList.setValue(rs.getString("PRNT_SKU_PRC"));
			}

			if (rs.getString("PRNT_SKU_TAX") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("PlanTaxAmount");
				prdArrtList.setValue(rs.getString("PRNT_SKU_TAX"));
			}

			if (rs.getString("PRNT_SKU_TAX_RATE") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("PlanTaxRate");
				prdArrtList.setValue(rs.getString("PRNT_SKU_TAX_RATE"));
			}

			if (rs.getString("DLVR_EMAIL_TXT") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("DeliveryEmail");
				prdArrtList.setValue(rs.getString("DLVR_EMAIL_TXT"));
			}

			if (rs.getString("LN_ITEM_ID") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("LineItemId");
				prdArrtList.setValue(rs.getString("LN_ITEM_ID"));
			}

			if (rs.getString("PRM_SKU_PRC") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("TriggerExtPrice");
				prdArrtList.setValue(rs.getString("PRM_SKU_PRC"));
			}

			if (rs.getString("PRM_SKU_TAX") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("TriggerTaxAmount");
				prdArrtList.setValue(rs.getString("PRM_SKU_TAX"));
			}

			if (rs.getString("PRM_SKU_TAX_RATE") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("TriggerTaxRate");
				prdArrtList.setValue(rs.getString("PRM_SKU_TAX_RATE"));
			}

			if (rs.getString("VAL_PKG_ID") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("ValuePackageID");
				prdArrtList.setValue(rs.getString("VAL_PKG_ID"));
			}

			if (rs.getString("VNDR_ID") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("VendorID");
				prdArrtList.setValue(rs.getString("VNDR_ID"));
			}

			if (rs.getString("QTY") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("Quantity");
				prdArrtList.setValue(rs.getString("QTY"));
			}

			if (rs.getString("CNCL_REAS_CDE") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("CancelReasonCode");
				prdArrtList.setValue(rs.getString("CNCL_REAS_CDE"));
			}
			
			// contract id and date

			billing = vendorOffer.addNewBillingInformation();

			if (rs.getString("CNTRCT_ID") != null
					|| rs.getString("CRCD_EXP_DT") != null) {
				Contract contract = Contract.Factory.newInstance();
				contract = billing.addNewContract();
				contract.setContractId(rs.getString("CNTRCT_ID"));

				if (rs.getString("CNTRCT_END_DT") != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(rs.getDate("CNTRCT_END_DT").getTime());
					contract.setEndDate(cal);
				}
			}
			if (rs.getString("CRCD_NBR") != null
					|| rs.getString("CRCD_TYP") != null
					|| rs.getString("CRCD_EXP_DT") != null) {
				CreditCard ccard = CreditCard.Factory.newInstance();
				ccard = billing.addNewCreditCard();

				if (rs.getString("CRCD_NM") != null) {
					Name cName = ccard.addNewCreditCardOwner();
					cName.setFullName(rs.getString("CRCD_NM"));
				}
				if (rs.getString("CRCD_NBR") != null) {
					String s = rs.getString("CRCD_NBR");
					long crcdNum = Long.parseLong(s.trim());
					ccard.setCardNumber(crcdNum);
				}

				ccard.setType(rs.getString("CRCD_TYP"));
				ccard.setExpDate(rs.getString("CRCD_EXP_DT"));
			}

			if (rs.getString("ADDR_LINE1_TXT") != null
					|| rs.getString("ADDR_LINE2_TXT") != null
					|| rs.getString("BLG_ADDR_LBL_TXT") != null) {
				billAddr = billing.addNewAddress();
				unPrsAddr = billAddr.addNewUnParsedAddress();
				unPrsAddr.setAddressLine1(rs.getString("ADDR_LINE1_TXT"));
				unPrsAddr.setAddressLine2(rs.getString("ADDR_LINE2_TXT"));
				unPrsAddr.setLabel(rs.getString("BLG_ADDR_LBL_TXT"));

				if (rs.getString("CITY_TXT") != null
						|| rs.getString("CTRY_TXT") != null
						|| rs.getString("STATE_CDE") != null
						|| rs.getString("POSTAL_CDE") != null) {
					demoGraph = unPrsAddr.addNewDemographics();
					demoGraph.setCity(rs.getString("CITY_TXT"));
					demoGraph.setCountry(rs.getString("CTRY_TXT"));
					demoGraph.setState(rs.getString("STATE_CDE"));
					demoGraph.setZipCode(rs.getString("POSTAL_CDE"));
				}
			}

		}
		return bbyOrder;

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

package com.bestbuy.sdp.subscription;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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

public class BBYOrderConstruct {

	BBYOrder consBBYOrder(HashMap<String, String>  map) throws SQLException,SDPInternalException {
		BBYOrder bbyOrder = null;
		if (map != null) {
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
			
			//TODO
			if (map.get("SRC_SYS_ID") != null) {
				ActingParty actPrty = ActingParty.Factory.newInstance();
				actPrty = tranInfo.addNewActingParty();
				actPrty.setPartyID(map.get("SRC_SYS_ID"));
			}
			// customer name and delivery email.
			customer = bbyOrder.addNewCustomer();
			//customer.setC4CustomerID(map.get("SDP_CUST_ID"));
			customer.setECCustomerID(map.get("ECCustomerID"));
			
			if (map.get("RewardZoneNumber") != null) {
				customer.setRewardZoneNumber(map.get("RewardZoneNumber"));
			}
			customer.setEmailAddress(map.get("Email"));

			if (map.get("FirstName") != null
					|| map.get("LastName") != null
					|| map.get("MiddleName") != null) {
				Name name = Name.Factory.newInstance();
				name = customer.addNewCustomerName();
				name.setFirstName(map.get("FirstName"));
				name.setMiddleName(map.get("MiddleName"));
				name.setLastName(map.get("LastName"));
			}

			if (map.get("PhoneAddressLabel") != null
					|| map.get("AreaCode") != null) {
				TelephoneNumber phone = TelephoneNumber.Factory.newInstance();
				phone = customer.addNewTelephoneNumber();
				phone.setLabel(map.get("PhoneAddressLabel"));
				if (map.get("AreaCode") != null) {
					phone.setAreaCode(map.get("AreaCode"));
					phone.setExchCode(map.get("ExchCode"));
					phone.setLocalNumCode(map.get("LocalNumCode"));
				}
			}

			bbyOffer = bbyOrder.addNewBBYOffer();

			bbyOfferIdentifier = bbyOffer.addNewBBYOfferIdentifier();
			ArrayofExternalID bbyOfferExtIdArry = bbyOfferIdentifier
					.addNewExternalIDCollection();

			// Storeid, regid, transactionid , transaction date time ,
			// transaction date and line id

			ExternalID bbyOfferExtId = null;

			if (map.get("TransactionID") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("TransactionId");
				bbyOfferExtId.setID(map.get("TransactionID"));
			}

			if (map.get("RegisterID") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("RegisterId");
				bbyOfferExtId.setID(map.get("RegisterID"));
			}

			if (map.get("StoreID") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("StoreId");
				bbyOfferExtId.setID(map.get("StoreID"));
			}

			if (map.get("Date") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("TransactionDateTime");
				bbyOfferExtId.setID(convertFormate(map.get("Date").substring(0, 10)));
			}

			if (map.get("Date") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("Date");
				//TODO
				//java.sql.Date transDate = stringToDateConvert(map
				//		.get("Date"));
				bbyOfferExtId.setID(map.get("Date").substring(0, 10));
			}

			//TODO - Need Chech
			if (map.get("GroupID") != null) {
				bbyOfferExtId = bbyOfferExtIdArry.addNewExternalID();
				bbyOfferExtId.setType("LineID");
				bbyOfferExtId.setID(map.get("GroupID"));
			}

			vendorOffer = bbyOffer.addNewVendorOffer();
			//14 Meaning Migrated
			if((map.get("OfferStatusSubscription").equals("9")) && (map.get("OfferStatusVariant").equals("8"))){
				vendorOffer.setStatus("4");
			}else if((map.get("OfferStatusSubscription").equals("9")) && (map.get("OfferStatusVariant").equals("5"))){
				vendorOffer.setStatus("5");
			}else if((map.get("OfferStatusSubscription").equals("10")) && (map.get("OfferStatusVariant").equals("10"))){
				vendorOffer.setStatus("6");
			}else if((map.get("OfferStatusSubscription").equals("8")) && (map.get("OfferStatusVariant").equals("4"))){
				vendorOffer.setStatus("7");
			}else if((map.get("OfferStatusSubscription").equals("8")) && (map.get("OfferStatusVariant").equals("8"))){
				vendorOffer.setStatus("4");
			}else if((map.get("OfferStatusSubscription").equals("8")) && (map.get("OfferStatusVariant").equals("10"))){
				vendorOffer.setStatus("6");
			}else{
				vendorOffer.setStatus(map.get("OfferStatusSubscription"));
			}


			// offerID
			vndrOffrIden = vendorOffer.addNewVendorOfferIdentifier();
			//TODO
			//vndrOffrIden.setConnect4SubscriptionOfferID(map
			//		.get("SDP_ORDER_ID"));
			//vndrOffrIden.setConnect4CatalogueID(map.get("CTLG_ID"));
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

			if (map.get("TriggerSku") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("TriggerSku");
				prodIdenExtId.setID(map.get("TriggerSku"));
			}

			//TODO
			if (map.get("SerialNumber") != null ){
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("SerialNumber");
				prodIdenExtId.setID(map.get("SerialNumber"));
			}

			if (map.get("REGID") != null ){
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("REGID");
				prodIdenExtId.setID(map.get("REGID"));
			}
			
			if (map.get("PSPID") != null ){
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("PSPID");
				prodIdenExtId.setID(map.get("PSPID"));
			}
			
			if (map.get("ConfirmationCode") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("ConfirmationCode");
				prodIdenExtId.setID(map.get("ConfirmationCode"));
			}

			if (map.get("KeyCode") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("KeyCode");
				prodIdenExtId.setID(map.get("KeyCode"));
			}

			// Commented by Krapa : digitalVendorId is same as vendorId
			// prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
			// prodIdenExtId.setType("DigitalVendorID");
			// prodIdenExtId.setID("");


			if (map.get("VendorTriggerSku") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("VendorTriggerSku");
				prodIdenExtId.setID(map.get("VendorTriggerSku"));
			}

			if (map.get("VendorProductID") != null) {
				prodIdenExtId = prodIdenExtIdArry.addNewExternalID();
				prodIdenExtId.setType("VendorProductID");
				prodIdenExtId.setID(map.get("VendorProductID"));
			}

			// quantity
			prodDetails = product.addNewProductDetails();

			ArrayOfSDPAttribute prdMoreDtl = prodDetails.addNewMoreDetails();
			Attribute prdArrtList = null;

			if (map.get("SDPID") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("SDPID");
				prdArrtList.setValue(map.get("SDPID"));
			}

			if (map.get("PlanSku") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("PlanSku");
				prdArrtList.setValue(map.get("PlanSku"));
			}

			if (map.get("PlanExtPric") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("PlanExtPrice");
				prdArrtList.setValue(map.get("PlanExtPric"));
			}

			//TODO
			if (map.get("PlanTaxAmount") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("PlanTaxAmount");
				prdArrtList.setValue(map.get("PlanTaxAmount"));
			}

			//TODO
			if (map.get("PlanTaxRate") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("PlanTaxRate");
				prdArrtList.setValue(map.get("PlanTaxRate"));
			}

			if (map.get("Email") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("DeliveryEmail");
				prdArrtList.setValue(map.get("Email"));
			}

			if (map.get("lineItemId") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("LineItemId");
				prdArrtList.setValue(map.get("lineItemId"));
			}

			if (map.get("TriggerExtPrice") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("TriggerExtPrice");
				prdArrtList.setValue(map.get("TriggerExtPrice"));
			}

			//TODO
			if (map.get("TriggerTaxAmount") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("TriggerTaxAmount");
				prdArrtList.setValue(map.get("TriggerTaxAmount"));
			}

			//TODO
			if (map.get("TriggerTaxRate") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("TriggerTaxRate");
				prdArrtList.setValue(map.get("TriggerTaxRate"));
			}

			if (map.get("ValuePkgID") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("ValuePackageID");
				prdArrtList.setValue(map.get("ValuePkgID"));
			}

			if (map.get("VendorID") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("VendorID");
				prdArrtList.setValue(map.get("VendorID"));
			}

			if (map.get("Quantity") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("Quantity");
				prdArrtList.setValue(map.get("Quantity"));
			}

			if (map.get("CancelReasonCode") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("CancelReasonCode");
				prdArrtList.setValue(map.get("CancelReasonCode"));
			}
			
			if (map.get("CancelReasonText") != null) {
				prdArrtList = prdMoreDtl.addNewAttribute();
				prdArrtList.setName("CancelReasonText");
				prdArrtList.setValue(map.get("CancelReasonText"));
			}
			// contract id and date

			billing = vendorOffer.addNewBillingInformation();

			if (map.get("ContractID") != null
					|| map.get("ContractEndDate") != null) {
				Contract contract = Contract.Factory.newInstance();
				contract = billing.addNewContract();
				contract.setContractId(map.get("ContractID"));

				if (map.get("ContractEndDate") != null) {
					Calendar cal = Calendar.getInstance();

					cal.setTimeInMillis(stringToDateTimeConvert(map.get("ContractEndDate")).getTime());

					contract.setEndDate(cal);



				}
			}
			if (map.get("CreditCardNumber") != null
					|| map.get("CreditCardType") != null
					|| map.get("CreditCardExpirationDate") != null) {
				CreditCard ccard = CreditCard.Factory.newInstance();
				ccard = billing.addNewCreditCard();

				if (map.get("CreditCardName") != null) {
					Name cName = ccard.addNewCreditCardOwner();
					cName.setFullName(map.get("CreditCardName"));
				}
				if (map.get("CreditCardNumber") != null) {
					String s = map.get("CreditCardNumber");
					long crcdNum = Long.parseLong(s.trim());
					ccard.setCardNumber(crcdNum);
				}

				ccard.setType(map.get("CreditCardType"));
				ccard.setExpDate(map.get("CreditCardExpirationDate"));
			}

			if (map.get("AddressLine1") != null
					|| map.get("AddressLine2") != null
					|| map.get("BlgAddressLabel") != null) {
				billAddr = billing.addNewAddress();
				unPrsAddr = billAddr.addNewUnParsedAddress();
				unPrsAddr.setAddressLine1(map.get("AddressLine1 "));
				unPrsAddr.setAddressLine2(map.get("AddressLine2"));
				unPrsAddr.setLabel(map.get("BlgAddressLabel"));

				if (map.get("City") != null
						|| map.get("Country") != null
						|| map.get("State") != null
						|| map.get("PostalCode") != null) {
					demoGraph = unPrsAddr.addNewDemographics();
					demoGraph.setCity(map.get("City"));
					demoGraph.setCountry(map.get("Country"));
					demoGraph.setState(map.get("State"));
					demoGraph.setZipCode(map.get("PostalCode"));
				}
			}

		}
		return bbyOrder;

	}

	private java.util.Date stringToDateTimeConvert(String date)

	throws SDPInternalException {

	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	java.util.Date parsedUtilDate = null;

	try {

	parsedUtilDate = formatter.parse(date);


	} catch (ParseException e) {

	Logger.logStackTrace(e.fillInStackTrace());

	throw new SDPInternalException("20040006",

	"Please valid timestamp - >yyyy-MM-dd");


	}

	return parsedUtilDate;

	}

	// Note timestamp format to be in yyyy-MM-dd
	private String convertFormate(String date)
			throws SDPInternalException {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		String sqlTimstamp =null;
		
		try {
			java.util.Date parsedUtilDate = formatter1.parse(date);
			sqlTimstamp= formatter.format(parsedUtilDate);
		} catch (ParseException e) {
			Logger.logStackTrace(e.fillInStackTrace());
			throw new SDPInternalException(" ",
					"Please valid timestamp - >yyyy-MM-dd hh:mm:ss ");

		}
		
		return sqlTimstamp;
	}
}

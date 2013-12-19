package com.bestbuy.sdp.order.bean;

import java.util.ArrayList;
import java.util.List;

import com.accenture.xml.sdp.bby.cm.creditCard.CreditCard;
import com.accenture.xml.sdp.bby.cm.customer.Customer;
import com.accenture.xml.sdp.bby.cm.customer.Name;
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
import com.accenture.xml.sdp.bby.utilities.identifier.BBYOfferIdentifier;
import com.accenture.xml.sdp.bby.utilities.identifier.ExternalID;
import com.accenture.xml.sdp.bby.utilities.identifier.ProductIdentifier;
import com.accenture.xml.sdp.bby.utilities.identifier.VendorOfferIdentifier;
import com.accenture.xml.sdp.bby.utilities.transactionInformation.ActingParty;
import com.accenture.xml.sdp.bby.utilities.transactionInformation.TransactionInformation;
import com.bestbuy.sdp.common.util.Logger;

/**
 * This class contains two constructors ,first construct is no-arg construct a empty constructor
 * Second construct accepts BBYOrder xml as a parameter
 * and populate variables by extracting information from BBYOrder xml
 * or null if no information present for particular variable
 *
 * @author a148045
 *
 */
public class BBYOrderBean {

	private String sdpOrderId = null;
	private String sdpId = null;
	private String triggerSku = null;
	private String externalId = null;
	private String parentSku = null;
	private String transId = null;
	private String transDt = null;
	private String transTmstp = null;
	private String storeId = null;
	private String regId = null;
	private String lineId = null;
	private String lineItemId = null;
	private String busKey = null;
	private String busKeyType = null;
	private String primSkuPrc = null;
	private String primSkuTax = null;
	private String primSkuTaxRate = null;
	private String prntSkuPrc = null;
	private String prntSkuTax = null;
	private String prntSkuTaxRate = null;
	private String qnty = null;
	private String valPkgId = null;
	private String srcSysId = null;
	private String confId = null;
	private String keyCode = null;
	private String masterItemId = null;
	private String digitialVndrId = null;
	private String vndrPrdId = null;
	private String vndrId = null;
	private String prodType = null;
	private String catalogId = null;
	private String cnclRsnCode = null;
	private String orderStatusCode = null;
	private String recStatCde = null;
	private String errorCde = null;
	private String sdpCustId = null;
	private String custId = null;
	private String custMailTxt = null;
	private String lastName = null;
	private String firstName = null;
	private String midName = null;
	private String addrLn1 = null;
	private String addrLn2 = null;
	private String bldAddrLblTxt = null;
	private String city = null;
	private String state = null;
	private String country = null;
	private String ccExpDt = null;
	private String ccName = null;
	private String ccNum = null;
	private String ccType = null;
	private String phNo = null;
	private String phAddrLbl = null;
	private String pstCode = null;
	private String stateCde = null;
	private String dlvrEmailTxt = null;
	private String contractId = null;
	private String contractEndDt = null;
	private String contractStCde = null;
	private String rewardZoneId = null;


	private List<Attribute> attrList = null;
	private List<ExternalID> extIdList = null;

	/**
	 *
	 */
	public BBYOrderBean() {

	}

	/**
	 * This constructor accepts BBYOrder xml as a parameter,
	 * extracts data from BBYOrder xml
	 * and populates BBYOrderBean variables with data or null if no data found
	 *
	 * @param bbyOrder
	 */
	public BBYOrderBean(BBYOrder bbyOrder){

		BBYOffer bbyOffer = null;
		BBYOfferIdentifier bbyOfferIdentifier = null;
		VendorOffer vendorOffer = null;
		BillingInformation billing = null;
		CreditCard ccard = null;
		Address billAddr = null;
		UnParsedAddress unPrsAddr = null;
		Demographics demoGraph = null;

		TransactionInformation tranInfo = null;
		ActingParty actPrty = null;

		Contract contract = null;
		VendorOfferIdentifier vndrOffrIden = null;
		ProductCollection prdColl = null;
		SubCategory subCat = null;
		Type type = null;
		Product product = null;
		ProductIdentifier prodIden = null;
		ProductDetails prodDetails = null;

		Customer customer = null;
		TelephoneNumber phone = null;
		Name name = null;

		attrList = new ArrayList<Attribute>();
		extIdList = new ArrayList<ExternalID>();

		// source
		if(bbyOrder.getTransactionInformation() != null){
			tranInfo = bbyOrder.getTransactionInformation();
			actPrty = tranInfo.getActingParty();
			if(actPrty != null){
				srcSysId = actPrty.getPartyID();
			}
		}

		// customer name and delivery email.
		if(bbyOrder.getCustomer() != null) {
			customer = bbyOrder.getCustomer();
			sdpCustId = customer.getC4CustomerID();
			custId = customer.getECCustomerID();
			rewardZoneId = customer.getRewardZoneNumber();
			custMailTxt = customer.getEmailAddress();

			if(customer.getCustomerName() != null){
				name = customer.getCustomerName();
				firstName = name.getFirstName();
				midName = name.getMiddleName();
				lastName = name.getLastName();
			}
			if( customer.getTelephoneNumber() != null){
				phone = customer.getTelephoneNumber();
				phAddrLbl = phone.getLabel();
				phNo = trimString(phone.getAreaCode()) + trimString(phone.getExchCode()) + trimString(phone.getLocalNumCode());
			}
		}


		bbyOffer = bbyOrder.getBBYOfferArray(0);
		if(bbyOffer != null){
			bbyOfferIdentifier = bbyOffer.getBBYOfferIdentifier();
			if(bbyOfferIdentifier != null){
				// Storeid, regid, transactionid and transaction date
				if(bbyOfferIdentifier.getExternalIDCollection() != null){
					int extListSize = bbyOfferIdentifier.getExternalIDCollection()
					.getExternalIDList().size();

					if(extListSize > 0){
						String extIdString = null;
						for(int i = 0; i < extListSize; i++){
							ExternalID extId = bbyOfferIdentifier.getExternalIDCollection()
									.getExternalIDList().get(i);

							extIdString = extId.getType().trim();

							if(extIdString.equalsIgnoreCase("TransactionId")){
								transId = extId.getID();
							}else if (extIdString.equalsIgnoreCase("RegisterId")){
								regId = extId.getID();
							}else if (extIdString.equalsIgnoreCase("StoreId")){
								storeId = extId.getID();
							}else if (extIdString.equalsIgnoreCase("TransactionDateTime")){
								transTmstp = extId.getID();
							}else if (extIdString.equalsIgnoreCase("Date")){
								transDt = extId.getID();
							}else if (extIdString.equalsIgnoreCase("LineID")){
								lineId = extId.getID();
							}
						}
					}
				}

			}
		}

		vendorOffer = bbyOffer.getVendorOfferArray(0);
		orderStatusCode = vendorOffer.getStatus();

		// offerID
		vndrOffrIden = vendorOffer.getVendorOfferIdentifier();
		sdpOrderId = vndrOffrIden.getConnect4SubscriptionOfferID();
		catalogId = vndrOffrIden.getConnect4CatalogueID();

		prdColl = vendorOffer.getBaseProductCollection();
		subCat = prdColl.getSubCategoryArray(0);
		type = subCat.getTypeArray(0);
		product = type.getProductArray(0);

		if(product != null){
			prodIden = product.getProductIdentifier();

			if(prodIden != null){
				prodType = prodIden.getConnect4ProductSubscriptionID();

				// trigger sku and business key
				if(prodIden.getExternalIDCollection() != null){
					int extSize = prodIden.getExternalIDCollection()
						.getExternalIDList().size();

					if(extSize > 0){
						String extIdString = null;
						for(int i = 0; i < extSize; i++){
							ExternalID extId = prodIden.getExternalIDCollection()
									.getExternalIDList().get(i);

							extIdString = extId.getType().trim();

							if(extIdString.equalsIgnoreCase("TriggerSku")){
								triggerSku = extId.getID();
							}else if (extIdString.equalsIgnoreCase("SerialNumber")
									|| extIdString.equalsIgnoreCase("REGID")
									|| extIdString.equalsIgnoreCase("PSPID")){
								busKeyType = extId.getType();
								busKey = extId.getID();
							}else if (extIdString.equalsIgnoreCase("ConfirmationCode")){
								confId = extId.getID();
							}else if (extIdString.equalsIgnoreCase("KeyCode")){
								keyCode = extId.getID();
							}else if (extIdString.equalsIgnoreCase("MasterItemId")){
								masterItemId = extId.getID();
							}
							//Commented by Logesh -- Digital Vendor id is same as Digital Id.
							//}else if (extIdString.equalsIgnoreCase("DigitalVendorID")){
							//	digitialVndrId = extId.getID();}
							/*else if (extIdString.equalsIgnoreCase("DigitalID")){
								digitalId = extId.getID();
							}*/else if (extIdString.equalsIgnoreCase("VendorTriggerSku")
								||extIdString.equalsIgnoreCase("VendorProductID")
								||extIdString.equalsIgnoreCase("VendorDigitalID")){
							vndrPrdId = extId.getID();
							}else{
								extIdList.add(extId);
							}
						}
					}
				}
			}

			// quantity
			prodDetails = product.getProductDetails();

			if(prodDetails != null){
				if(prodDetails.getMoreDetails() != null){
					int atrSize = prodDetails.getMoreDetails().getAttributeList().size();
					if(atrSize > 0) {
						String attString = null;
						for (int i = 0; i < atrSize; i++){
							Attribute attribute = prodDetails.getMoreDetails()
													.getAttributeList().get(i);

							//Logger.log("-->" + attribute.getName().trim()+"-");
							attString = attribute.getName().trim();
							if(attString.equalsIgnoreCase("SDPID")){
								sdpId = attribute.getValue();
							}else if (attString.equalsIgnoreCase("PlanSku")){
								parentSku = attribute.getValue();
							}else if (attString.equalsIgnoreCase("PlanExtPrice")){
								prntSkuPrc = attribute.getValue();
							}else if (attString.equalsIgnoreCase("PlanTaxAmount")){
								prntSkuTax = attribute.getValue();
							}else if (attString.equalsIgnoreCase("PlanTaxRate")){
								prntSkuTaxRate = attribute.getValue();
							}else if (attString.equalsIgnoreCase("DeliveryEmail")){
								dlvrEmailTxt = attribute.getValue();
							}else if (attString.equalsIgnoreCase("LineItemId")){
								lineItemId = attribute.getValue();
							}else if (attString.equalsIgnoreCase("TriggerExtPrice")){
								primSkuPrc = attribute.getValue();
							}else if (attString.equalsIgnoreCase("TriggerTaxAmount")){
								primSkuTax = attribute.getValue();
							}else if (attString.equalsIgnoreCase("TriggerTaxRate")){
								primSkuTaxRate = attribute.getValue();
							}else if (attString.equalsIgnoreCase("ValuePackageID")){
								valPkgId = attribute.getValue();
							}else if (attString.equalsIgnoreCase("VendorID")){
								vndrId = attribute.getValue();
							}else if (attString.equalsIgnoreCase("CancelReasonCode")){
								cnclRsnCode = attribute.getValue();
							}else if (attString.equalsIgnoreCase("Quantity")){
								qnty = attribute.getValue();
							}else{
								attrList.add(attribute);
							}
						}
					}
				}
			}
		}

		// contract id and date
		if(vendorOffer.getBillingInformation() != null){
			billing = vendorOffer.getBillingInformation();
			contract = billing.getContract();
			if (contract != null) {
				contractId = contract.getContractId();
				contractStCde = vendorOffer.getStatus();
				if(contractStCde.equalsIgnoreCase("7") 
						&& (cnclRsnCode !=null && cnclRsnCode.trim().length() >0)){
					contractStCde = cnclRsnCode;
				}

				if(contract.getEndDate() != null){
					contractEndDt = contract.getEndDate().toString();
				}

			}



			if( billing.getCreditCard() != null){
				ccard = billing.getCreditCard();
				if(ccard.getCreditCardOwner() != null){
					Name cName = ccard.getCreditCardOwner();
					ccName = cName.getFullName();
				}
				if(ccard.getCardNumber() != 0){
					ccNum = String.valueOf(ccard.getCardNumber());
				}else{
					ccNum = null;
				}

				ccType = ccard.getType();
				ccExpDt = ccard.getExpDate();

			}

			if(billing.getAddress() != null && billing.getAddress() != null
					&& billing.getAddress().getUnParsedAddress() != null ){
				billAddr = billing.getAddress();
					unPrsAddr = billAddr.getUnParsedAddress();
					addrLn1 = unPrsAddr.getAddressLine1();
					addrLn2 = unPrsAddr.getAddressLine2();
					bldAddrLblTxt = unPrsAddr.getLabel();

					if(unPrsAddr.getDemographics() != null){
						demoGraph = unPrsAddr.getDemographics();
						city = demoGraph.getCity();
						country = demoGraph.getCountry();
						stateCde = demoGraph.getState();
						pstCode = demoGraph.getZipCode();
					}
			}

		}
	}


	@Override
	public String toString() {
		String str = "\n\tsdpOrderId - " + trimString(sdpOrderId) +
		"\n\tsdpId - " + trimString(sdpId) +
		"\n\ttriggerSku - " + trimString(triggerSku) +
		"\n\texternalId - " + trimString(externalId) +
		"\n\tparentSku - " + trimString(parentSku) +
		"\n\ttransId - " + trimString( transId) +
		"\n\ttransDt - " + trimString( transDt) +
		"\n\ttransTmstp - " + trimString(transTmstp) +
		"\n\tstoreId - " + trimString( storeId) +
		"\n\tregId - " + trimString(regId) +
		"\n\tlineId - " + trimString( lineId) +
		"\n\tlineItemId - " + trimString( lineItemId) +
		"\n\tbusKey - " + trimString( busKey) +
		"\n\tprimSkuPrc - " + trimString( primSkuPrc) +
		"\n\tprimSkuTax - " + trimString( primSkuTax) +
		"\n\tprimSkuTaxRate - " + trimString( primSkuTaxRate) +
		"\n\tprntSkuPrc - " + trimString( prntSkuPrc) +
		"\n\tprntSkuTax - " + trimString( prntSkuTax) +
		"\n\tprntSkuTaxRate - " + trimString( prntSkuTaxRate) +
		"\n\tqnty - " + trimString( qnty) +
		"\n\tvalPkgId - " + trimString( valPkgId) +
		"\n\tsrcSysId - " + trimString( srcSysId) +
		"\n\tconfId - " + trimString( confId) +
		"\n\tkeyCode - " + trimString( keyCode) +
		"\n\tmasterItemId - " + trimString( masterItemId) +
		"\n\tdigitialVndrId - " + trimString(digitialVndrId) +
		"\n\tvndrPrdId - " + trimString( vndrPrdId) +
		"\n\tvndrId - " + trimString( vndrId) +
		"\n\tprodType - " + trimString(prodType) +
		"\n\tcatalogId - " + trimString(catalogId) +
		"\n\tcnclRsnCode - " + trimString( cnclRsnCode) +
		"\n\torderStatusCode - " + trimString( orderStatusCode) +
		"\n\trecStatCde - " + trimString( recStatCde) +
		"\n\terrorCde - " + trimString(errorCde) +
		"\n\tsdpCustId - " + trimString(sdpCustId) +
		"\n\tcustId - " + trimString(custId) +
		"\n\tcustMailTxt - " + trimString(custMailTxt) +
		"\n\tlastName - " + trimString(lastName) +
		"\n\tfirstName - " + trimString(firstName) +
		"\n\tmidName - " + trimString(midName) +
		"\n\taddrLn1 - " + trimString(addrLn1) +
		"\n\taddrLn2 - " + trimString(addrLn2) +
		"\n\tbldAddrLblTxt - " + trimString( bldAddrLblTxt) +
		"\n\tcity - " + trimString( city) +
		"\n\tstate - " + trimString(state) +
		"\n\tcountry - " + trimString( country) +
		"\n\tccExpDt - " + trimString(ccExpDt) +
		"\n\tccName - " + trimString(ccName) +
		"\n\tccNum - " + trimString(ccNum) +
		"\n\tccType - " + trimString( ccType) +
		"\n\tphNo - " + trimString( phNo) +
		"\n\tphAddrLbl - " + trimString( phAddrLbl) +
		"\n\tpstCode - " + trimString(pstCode) +
		"\n\tstateCde - " + trimString(stateCde) +
		"\n\tdlvrEmailTxt - " + trimString(dlvrEmailTxt) +
		"\n\tcontractId - " + trimString( contractId) +
		"\n\tcontractEndDt - " + trimString( contractEndDt) +
		"\n\tcontractStCde - " + trimString( contractStCde );

		return str.toString();
	}

	private String trimString(String val) {
		if (val == null) {
			return val;
		}
		return val.trim();
	}

	/**
	 * @return the sdpOrderId
	 */
	public String getSdpOrderId() {
		return trimString(sdpOrderId);
	}

	/**
	 * @param sdpOrderId the sdpOrderId to set
	 */
	public void setSdpOrderId(String sdpOrderId) {
		this.sdpOrderId = sdpOrderId;
	}

	/**
	 * @return the sdpId
	 */
	public String getSdpId() {
		return trimString(sdpId);
	}

	/**
	 * @param sdpId the sdpId to set
	 */
	public void setSdpId(String sdpId) {
		this.sdpId = sdpId;
	}

	/**
	 * @return the triggerSku
	 */
	public String getTriggerSku() {
		return trimString(triggerSku);
	}

	/**
	 * @param triggerSku the triggerSku to set
	 */
	public void setTriggerSku(String triggerSku) {
		this.triggerSku = triggerSku;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return trimString(externalId);
	}

	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @return the parentSku
	 */
	public String getParentSku() {
		return trimString(parentSku);
	}

	/**
	 * @param parentSku the parentSku to set
	 */
	public void setParentSku(String parentSku) {
		this.parentSku = parentSku;
	}

	/**
	 * @return the transId
	 */
	public String getTransId() {
		return trimString(transId);
	}

	/**
	 * @param transId the transId to set
	 */
	public void setTransId(String transId) {
		this.transId = transId;
	}

	/**
	 * @return the transDt
	 */
	public String getTransDt() {
		return trimString(transDt);
	}

	/**
	 * @param transDt the transDt to set
	 */
	public void setTransDt(String transDt) {
		this.transDt = transDt;
	}

	/**
	 * @return the transTmstp
	 */
	public String getTransTmstp() {
		return trimString(transTmstp);
	}

	/**
	 * @param transTmstp the transTmstp to set
	 */
	public void setTransTmstp(String transTmstp) {
		this.transTmstp = transTmstp;
	}

	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return trimString(storeId);
	}

	/**
	 * @param storeId the storeId to set
	 */
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	/**
	 * @return the regId
	 */
	public String getRegId() {
		return trimString(regId);
	}

	/**
	 * @param regId the regId to set
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}

	/**
	 * @return the lineId
	 */
	public String getLineId() {
		return trimString(lineId);
	}

	/**
	 * @param lineId the lineId to set
	 */
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	/**
	 * @return the lineItemId
	 */
	public String getLineItemId() {
		return trimString(lineItemId);
	}

	/**
	 * @param lineItemId the lineItemId to set
	 */
	public void setLineItemId(String lineItemId) {
		this.lineItemId = lineItemId;
	}

	/**
	 * @return the busKey
	 */
	public String getBusKey() {
		return trimString(busKey);
	}

	/**
	 * @param busKey the busKey to set
	 */
	public void setBusKey(String busKey) {
		this.busKey = busKey;
	}

	/**
	 * @return the primSkuPrc
	 */
	public String getPrimSkuPrc() {
		return trimString(primSkuPrc);
	}

	/**
	 * @param primSkuPrc the primSkuPrc to set
	 */
	public void setPrimSkuPrc(String primSkuPrc) {
		this.primSkuPrc = primSkuPrc;
	}

	/**
	 * @return the primSkuTax
	 */
	public String getPrimSkuTax() {
		return trimString(primSkuTax);
	}

	/**
	 * @param primSkuTax the primSkuTax to set
	 */
	public void setPrimSkuTax(String primSkuTax) {
		this.primSkuTax = primSkuTax;
	}

	/**
	 * @return the primSkuTaxRate
	 */
	public String getPrimSkuTaxRate() {
		return trimString(primSkuTaxRate);
	}

	/**
	 * @param primSkuTaxRate the primSkuTaxRate to set
	 */
	public void setPrimSkuTaxRate(String primSkuTaxRate) {
		this.primSkuTaxRate = primSkuTaxRate;
	}

	/**
	 * @return the prntSkuPrc
	 */
	public String getPrntSkuPrc() {
		return trimString(prntSkuPrc);
	}

	/**
	 * @param prntSkuPrc the prntSkuPrc to set
	 */
	public void setPrntSkuPrc(String prntSkuPrc) {
		this.prntSkuPrc = prntSkuPrc;
	}

	/**
	 * @return the prntSkuTax
	 */
	public String getPrntSkuTax() {
		return trimString(prntSkuTax);
	}

	/**
	 * @param prntSkuTax the prntSkuTax to set
	 */
	public void setPrntSkuTax(String prntSkuTax) {
		this.prntSkuTax = prntSkuTax;
	}

	/**
	 * @return the prntSkuTaxRate
	 */
	public String getPrntSkuTaxRate() {
		return trimString(prntSkuTaxRate);
	}

	/**
	 * @param prntSkuTaxRate the prntSkuTaxRate to set
	 */
	public void setPrntSkuTaxRate(String prntSkuTaxRate) {
		this.prntSkuTaxRate = prntSkuTaxRate;
	}

	/**
	 * @return the qnty
	 */
	public String getQnty() {
		return trimString(qnty);
	}

	/**
	 * @param qnty the qnty to set
	 */
	public void setQnty(String qnty) {
		this.qnty = qnty;
	}

	/**
	 * @return the valPkgId
	 */
	public String getValPkgId() {
		return trimString(valPkgId);
	}

	/**
	 * @param valPkgId the valPkgId to set
	 */
	public void setValPkgId(String valPkgId) {
		this.valPkgId = valPkgId;
	}

	/**
	 * @return the srcSysId
	 */
	public String getSrcSysId() {
		return trimString(srcSysId);
	}

	/**
	 * @param srcSysId the srcSysId to set
	 */
	public void setSrcSysId(String srcSysId) {
		this.srcSysId = srcSysId;
	}

	/**
	 * @return the confId
	 */
	public String getConfId() {
		return trimString(confId);
	}

	/**
	 * @param confId the confId to set
	 */
	public void setConfId(String confId) {
		this.confId = confId;
	}

	/**
	 * @return the keyCode
	 */
	public String getKeyCode() {
		return trimString(keyCode);
	}

	/**
	 * @param keyCode the keyCode to set
	 */
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}

	/**
	 * @return the masterItemId
	 */
	public String getMasterItemId() {
		return trimString(masterItemId);
	}

	/**
	 * @param masterItemId the masterItemId to set
	 */
	public void setMasterItemId(String masterItemId) {
		this.masterItemId = masterItemId;
	}

	/**
	 * @return the digitialVndrId
	 */
	public String getDigitialVndrId() {
		return trimString(digitialVndrId);
	}

	/**
	 * @param digitialVndrId the digitialVndrId to set
	 */
	public void setDigitialVndrId(String digitialVndrId) {
		this.digitialVndrId = digitialVndrId;
	}

	/**
	 * @return the vndrPrdId
	 */
	public String getVndrPrdId() {
		return trimString(vndrPrdId);
	}

	/**
	 * @param vndrPrdId the vndrPrdId to set
	 */
	public void setVndrPrdId(String vndrPrdId) {
		this.vndrPrdId = vndrPrdId;
	}

	/**
	 * @return the vndrId
	 */
	public String getVndrId() {
		return trimString(vndrId);
	}

	/**
	 * @param vndrId the vndrId to set
	 */
	public void setVndrId(String vndrId) {
		this.vndrId = vndrId;
	}

	/**
	 * @return the prodType
	 */
	public String getProdType() {
		return trimString(prodType);
	}

	/**
	 * @param prodType the prodType to set
	 */
	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	/**
	 * @return the catalogId
	 */
	public String getCatalogId() {
		return trimString(catalogId);
	}

	/**
	 * @param catalogId the catalogId to set
	 */
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	/**
	 * @return the cnclRsnCode
	 */
	public String getCnclRsnCode() {
		return trimString(cnclRsnCode);
	}

	/**
	 * @param cnclRsnCode the cnclRsnCode to set
	 */
	public void setCnclRsnCode(String cnclRsnCode) {
		this.cnclRsnCode = cnclRsnCode;
	}

	/**
	 * @return the orderStatusCode
	 */
	public String getOrderStatusCode() {
		return trimString(orderStatusCode);
	}

	/**
	 * @param orderStatusCode the orderStatusCode to set
	 */
	public void setOrderStatusCode(String orderStatusCode) {
		this.orderStatusCode = orderStatusCode;
	}

	/**
	 * @return the recStatCde
	 */
	public String getRecStatCde() {
		return trimString(recStatCde);
	}

	/**
	 * @param recStatCde the recStatCde to set
	 */
	public void setRecStatCde(String recStatCde) {
		this.recStatCde = recStatCde;
	}

	/**
	 * @return the errorCde
	 */
	public String getErrorCde() {
		return trimString(errorCde);
	}

	/**
	 * @param errorCde the errorCde to set
	 */
	public void setErrorCde(String errorCde) {
		this.errorCde = errorCde;
	}

	/**
	 * @return the sdpCustId
	 */
	public String getSdpCustId() {
		return trimString(sdpCustId);
	}

	/**
	 * @param sdpCustId the sdpCustId to set
	 */
	public void setSdpCustId(String sdpCustId) {
		this.sdpCustId = sdpCustId;
	}

	/**
	 * @return the custId
	 */
	public String getCustId() {
		return trimString(custId);
	}

	/**
	 * @param custId the custId to set
	 */
	public void setCustId(String custId) {
		this.custId = custId;
	}

	/**
	 * @return the custMailTxt
	 */
	public String getCustMailTxt() {
		return trimString(custMailTxt);
	}

	/**
	 * @param custMailTxt the custMailTxt to set
	 */
	public void setCustMailTxt(String custMailTxt) {
		this.custMailTxt = custMailTxt;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return trimString(lastName);
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return trimString(firstName);
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the midName
	 */
	public String getMidName() {
		return trimString(midName);
	}

	/**
	 * @param midName the midName to set
	 */
	public void setMidName(String midName) {
		this.midName = midName;
	}

	/**
	 * @return the addrLn1
	 */
	public String getAddrLn1() {
		return trimString(addrLn1);
	}

	/**
	 * @param addrLn1 the addrLn1 to set
	 */
	public void setAddrLn1(String addrLn1) {
		this.addrLn1 = addrLn1;
	}

	/**
	 * @return the addrLn2
	 */
	public String getAddrLn2() {
		return trimString(addrLn2);
	}

	/**
	 * @param addrLn2 the addrLn2 to set
	 */
	public void setAddrLn2(String addrLn2) {
		this.addrLn2 = addrLn2;
	}

	/**
	 * @return the bldAddrLblTxt
	 */
	public String getBldAddrLblTxt() {
		return trimString(bldAddrLblTxt);
	}

	/**
	 * @param bldAddrLblTxt the bldAddrLblTxt to set
	 */
	public void setBldAddrLblTxt(String bldAddrLblTxt) {
		this.bldAddrLblTxt = bldAddrLblTxt;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return trimString(city);
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return trimString(state);
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return trimString(country);
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the ccExpDt
	 */
	public String getCcExpDt() {
		return trimString(ccExpDt);
	}

	/**
	 * @param ccExpDt the ccExpDt to set
	 */
	public void setCcExpDt(String ccExpDt) {
		this.ccExpDt = ccExpDt;
	}

	/**
	 * @return the ccName
	 */
	public String getCcName() {
		return trimString(ccName);
	}

	/**
	 * @param ccName the ccName to set
	 */
	public void setCcName(String ccName) {
		this.ccName = ccName;
	}

	/**
	 * @return the ccNum
	 */
	public String getCcNum() {
		return trimString(ccNum);
	}

	/**
	 * @param ccNum the ccNum to set
	 */
	public void setCcNum(String ccNum) {
		this.ccNum = ccNum;
	}

	/**
	 * @return the ccType
	 */
	public String getCcType() {
		return trimString(ccType);
	}

	/**
	 * @param ccType the ccType to set
	 */
	public void setCcType(String ccType) {
		this.ccType = ccType;
	}

	/**
	 * @return the phNo
	 */
	public String getPhNo() {
		return trimString(phNo);
	}

	/**
	 * @param phNo the phNo to set
	 */
	public void setPhNo(String phNo) {
		this.phNo = phNo;
	}

	/**
	 * @return the phAddrLbl
	 */
	public String getPhAddrLbl() {
		return trimString(phAddrLbl);
	}

	/**
	 * @param phAddrLbl the phAddrLbl to set
	 */
	public void setPhAddrLbl(String phAddrLbl) {
		this.phAddrLbl = phAddrLbl;
	}

	/**
	 * @return the pstCode
	 */
	public String getPstCode() {
		return trimString(pstCode);
	}

	/**
	 * @param pstCode the pstCode to set
	 */
	public void setPstCode(String pstCode) {
		this.pstCode = pstCode;
	}

	/**
	 * @return the stateCde
	 */
	public String getStateCde() {
		return trimString(stateCde);
	}

	/**
	 * @param stateCde the stateCde to set
	 */
	public void setStateCde(String stateCde) {
		this.stateCde = stateCde;
	}

	/**
	 * @return the dlvrEmailTxt
	 */
	public String getDlvrEmailTxt() {
		return trimString(dlvrEmailTxt);
	}

	/**
	 * @param dlvrEmailTxt the dlvrEmailTxt to set
	 */
	public void setDlvrEmailTxt(String dlvrEmailTxt) {
		this.dlvrEmailTxt = dlvrEmailTxt;
	}

	/**
	 * @return the contractId
	 */
	public String getContractId() {
		return trimString(contractId);
	}

	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	/**
	 * @return the contractEndDt
	 */
	public String getContractEndDt() {
		return trimString(contractEndDt);
	}

	/**
	 * @param contractEndDt the contractEndDt to set
	 */
	public void setContractEndDt(String contractEndDt) {
		this.contractEndDt = contractEndDt;
	}

	/**
	 * @return the contractStCde
	 */
	public String getContractStCde() {
		return trimString(contractStCde);
	}

	/**
	 * @param contractStCde the contractStCde to set
	 */
	public void setContractStCde(String contractStCde) {
		this.contractStCde = contractStCde;
	}

	/**
	 * @return the attrList
	 */
	public List<Attribute> getAttrList() {
		return attrList;
	}

	/**
	 * @param attrList the attrList to set
	 */
	public void setAttrList(List<Attribute> attrList) {
		this.attrList = attrList;
	}

	/**
	 * @return the extIdList
	 */
	public List<ExternalID> getExtIdList() {
		return extIdList;
	}

	/**
	 * @param extIdList the extIdList to set
	 */
	public void setExtIdList(List<ExternalID> extIdList) {
		this.extIdList = extIdList;
	}

	public String getBusKeyType() {
		return trimString(busKeyType);
	}

	public void setBusKeyType(String busKeyType) {
		this.busKeyType = busKeyType;
	}

	public String getRewardZoneId() {
		return trimString(rewardZoneId);
	}

	public void setRewardZoneId(String rewardZoneId) {
		this.rewardZoneId = rewardZoneId;
	}

}

package com.bestbuy.sdp.provisioning.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

import com.bestbuy.schemas.sdp.tpa.kcb.KeyCodeAttributeType;
import com.bestbuy.schemas.sdp.tpa.kcb.KeyCodeAttributesType;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeRequest;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeRequestDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeResponse;
import com.bestbuy.schemas.sdp.tpa.kcb.ReserveKeyCodeResponseDocument;
import com.bestbuy.schemas.sdp.tpa.kcb.ReservedKeyCodeType;
import com.bestbuy.sdp.common.util.Logger;

public class KeyCodeTestSuite {

	static final String REQ_FILE = "01_ReserveKeyCodeRequest.xml";
	static final String RSP_FILE = "02_ReserveKeyCodeResponse.xml";

	@Test
	public void testReserveXmlGeneration() throws Throwable {

		// Write Request
		File file = new File(REQ_FILE);
		FileWriter fw = new FileWriter(file);

		ReserveKeyCodeRequestDocument xmlRequestDoc = ReserveKeyCodeRequestDocument.Factory.newInstance();
		ReserveKeyCodeRequest xmlRequest  = xmlRequestDoc.addNewReserveKeyCodeRequest();
		xmlRequest.setOrderID("ORDERID_VALUE");
		xmlRequest.setProductSKU("PRODUCT_SKU");
		xmlRequest.setVendorID("VND_ID");

		fw.write(xmlRequestDoc.toString());
		fw.flush();
		fw.close();
		// Write Response
		file = new File(RSP_FILE);
		fw = new FileWriter(file);

		ReserveKeyCodeResponseDocument xmlResponseDoc = ReserveKeyCodeResponseDocument.Factory.newInstance();
		ReserveKeyCodeResponse xmlResponse = xmlResponseDoc.addNewReserveKeyCodeResponse();
		ReservedKeyCodeType xmlKeyCode = xmlResponse.addNewKeyCode();
		xmlKeyCode.setValue("KEY_CODE");
		KeyCodeAttributesType xmlAttributes = xmlKeyCode.addNewAttributes();
		KeyCodeAttributeType xmlAttribute = xmlAttributes.addNewAttribute();
		xmlAttribute.setName("ATTRIBUTE_1");
		xmlAttribute.setValue("VALUE_1");

		fw.write(xmlResponseDoc.toString());
		fw.flush();
		fw.close();
	}

	@Test
	public void testReserveXmlParsing() throws Throwable{
		File file = new File(REQ_FILE);

		ReserveKeyCodeRequestDocument xmlRequestDoc = ReserveKeyCodeRequestDocument.Factory.parse(file);

		assertTrue(xmlRequestDoc.validate());

		//Logger.log(xmlRequestDoc.toString());
	}
}

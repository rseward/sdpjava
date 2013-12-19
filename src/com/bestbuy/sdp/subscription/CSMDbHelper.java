package com.bestbuy.sdp.subscription;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import oracle.jdbc.driver.OracleTypes;

import com.accenture.xml.sdp.bby.om.bbyOrder.BBYOrder;
import com.accenture.xml.sdp.bby.serviceResult.ServiceResult;
import com.bestbuy.schemas.sdp.db.createOrder.CreateOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.lookupVendorID.LookupVendorIDRequestDocument;
import com.bestbuy.schemas.sdp.db.lookupVendorID.LookupVendorIDResponseDocument;
import com.bestbuy.schemas.sdp.db.lookupVendorID.LookupVendorIDResponseDocument.LookupVendorIDResponse;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderResponseDocument;
import com.bestbuy.schemas.sdp.db.retrieveOrder.RetrieveOrderResponseDocument.RetrieveOrderResponse;
import com.bestbuy.sdp.common.util.Logger;
import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.order.SDPOrderDB;
import com.bestbuy.sdp.services.exception.SDPInternalException;

public class CSMDbHelper {


	public RetrieveOrderResponseDocument retrieveOrder(Connection conn,
			String keyType, String keyValue, boolean migrateSubscription) throws SDPInternalException{

		if (isNull(keyType) || isNull(keyValue)) {
			throw new SDPInternalException("", "Invalid Request");
		}

		
		RetrieveOrderResponseDocument response = RetrieveOrderResponseDocument.Factory
				.newInstance();
		RetrieveOrderResponse rtrOdrResponse = response
				.addNewRetrieveOrderResponse();

		CallableStatement proc = null;

		BBYOrder bbyOrder = null;

		try {
			if("LineItemId".equalsIgnoreCase(keyType)) {
				proc = conn.prepareCall("{call CSM.lookupByLineItemId(?,?,?,?) }");
			} else if("ContractId".equalsIgnoreCase(keyType)) {
				proc = conn.prepareCall("{call CSM.lookupByContractId(?,?,?,?) }");
			}else {
				throw new SDPInternalException("","Invalid  Search Criteria  --> LineItemID or ContractID.");
			}
				

			proc.setString(1, keyValue);

			proc.registerOutParameter(2, OracleTypes.VARCHAR);
			proc.registerOutParameter(3, OracleTypes.VARCHAR);
			proc.registerOutParameter(4, OracleTypes.VARCHAR);
			proc.executeQuery();
			
			String subscriptionId = proc.getString(2);
			String subOfferId = proc.getString(3);
			String subSrvcVarntId = proc.getString(4);
			
			if (isNull(subscriptionId)
					|| isNull(subSrvcVarntId)
					|| isNull(subOfferId)) {
				throw new SDPInternalException("", "Subscription Not Found");
			}
			HashMap<String,String> attr = new HashMap();
			attr.putAll(lookupSrvVarntAttrs(conn,subSrvcVarntId));
			attr.putAll(lookupSrvVarntExternalIds(conn,subSrvcVarntId));
			attr.putAll(lookupSubOfferAttrs(conn,subOfferId));
			attr.putAll(lookupSubOfferExternalIds(conn,subOfferId));
			attr.putAll(lookupSubOfferStatus(conn,subOfferId));
			attr.putAll(lookupSubExternalIds(conn,subscriptionId));
			attr.putAll(lookupPartyAttrs(conn,subscriptionId));
			attr.putAll(lookupContranctEndDate(conn,subscriptionId));
			
			
			BBYOrderConstruct bbyC = new BBYOrderConstruct();
			
			bbyOrder = bbyC.consBBYOrder(attr);
			
			SDPOrderDB sdpOrderBD = new SDPOrderDB();
			rtrOdrResponse.setBBYOrder(bbyOrder);
			CreateOrderResponseDocument createOrderRsp = sdpOrderBD.createOrder(bbyOrder);
			
			

			// SDP_ORDER_ID
			rtrOdrResponse.getBBYOrder()
					.getBBYOfferArray(0)
					.getVendorOfferArray(0)
					.getVendorOfferIdentifier()
					.setConnect4SubscriptionOfferID(
							String.valueOf(createOrderRsp.
									getCreateOrderResponse().getActivationOrderID()));
			
			//SDP CUSTOMER ID
			rtrOdrResponse.getBBYOrder()
					.getCustomer()
					.setC4CustomerID(
							createOrderRsp.getCreateOrderResponse().getSDPCustomerId());

			
			updateSubscriptionOfferStatus(subOfferId, "11", conn);
			ServiceResult serviceResult = null;
			serviceResult = rtrOdrResponse.addNewServiceResult();

			serviceResult.setStatusCode(BigInteger.ZERO);
		} catch (SQLException e) {
			if (e.getMessage().indexOf("ORA-01403") != -1) {
				throw new SDPInternalException("20040007", "Method = retrieveOrder() :: Order Not found ::\n"+e.getMessage());
			}
			e.printStackTrace();
			throw new SDPInternalException("20040016", "Method  :: SDP DB issue.\n"+e.getMessage());
			
		}
		return response;
	}

	private void updateSubscriptionOfferStatus(String subOfferId, String status, Connection conn) throws SQLException {
		CallableStatement proc = null;

		proc = conn
				.prepareCall("{call CSM.updateSubOfferStatus(?,?) }");

		proc.setString(1, subOfferId);

		proc.setString(2, status);
		proc.executeQuery();
		
	}
	
	public LookupVendorIDResponseDocument retrieveVendorIdByContractID(LookupVendorIDRequestDocument requestDoc)throws SDPInternalException  {
		CallableStatement proc = null;
		LookupVendorIDResponseDocument responseDoc = LookupVendorIDResponseDocument.Factory.newInstance();
		LookupVendorIDResponse response = responseDoc.addNewLookupVendorIDResponse();
//		response.addNewLookupVendorIDResponse();
		String vendorID = null;
		String sdpOrderId = null;
		String contractID = null;
		String serialNumber = null;
		Connection conn = null;
		try {
//			Connection conn = RuntimeEnvironment
			//.getConn();
			
			contractID = requestDoc.getLookupVendorIDRequest().getContractId();
			serialNumber = requestDoc.getLookupVendorIDRequest().getSerialNumber();
			
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call LOOKUPVENDORID(?,?,?) }");

			proc.setString(1, contractID);
			proc.registerOutParameter(2, OracleTypes.VARCHAR);
			proc.registerOutParameter(3, OracleTypes.VARCHAR);
			proc.executeQuery();			
			if(proc.getString(2) != null){
				vendorID=proc.getString(2);
				sdpOrderId = proc.getString(3);
			}else{
				vendorID=serialNumber.substring(0, 3)+"00";
				sdpOrderId="0";
			}
			response.setSDPOrderId(sdpOrderId);
			response.setVendorId(vendorID);
			RuntimeEnvironment.releaseConn(conn);
		} catch (SQLException e) {
			if (e.getMessage().indexOf("ORA-01403") != -1) {
				vendorID=serialNumber.substring(0, 5);
				sdpOrderId="0";
				response.setSDPOrderId(sdpOrderId);
				response.setVendorId(vendorID);
				return responseDoc;
			}	
			RuntimeEnvironment.releaseConn(conn);
				throw new SDPInternalException("", e.getMessage(),e);
		}finally {
			RuntimeEnvironment.releaseConn(conn);
		}
		return responseDoc;
		
	}

	private boolean isNull(String val) {
		if (val == null || val.trim().length() == 0) {
			return true;
		}
		return false;
	}

	private Map<String, String> lookupContranctEndDate(
			Connection conn, String subscriptionId) throws SQLException {

		CallableStatement proc = null;
		ResultSet rs = null;

		HashMap<String,String> attr = new HashMap<String, String>();

		proc = conn
				.prepareCall("{call CSM.lookupContranctEndDate(?,?) }");

		proc.setString(1, subscriptionId);

		proc.registerOutParameter(2, OracleTypes.CURSOR);
		proc.executeQuery();
		rs = (ResultSet) proc.getObject(2);

		if (rs != null) {
			while (rs.next()) {
				attr.put(rs.getString("datetype_name"), rs.getString("date_value"));
			}
		}
		return attr;
	}

	private Map<String, String> lookupPartyAttrs(
			Connection conn, String subscriptionId) throws SQLException {

		CallableStatement proc = null;
		ResultSet rs = null;

		HashMap<String,String> attr = new HashMap<String, String>();

		proc = conn
				.prepareCall("{call CSM.lookupPartyAttrs(?,?) }");

		proc.setString(1, subscriptionId);

		proc.registerOutParameter(2, OracleTypes.CURSOR);
		proc.executeQuery();
		rs = (ResultSet) proc.getObject(2);

		if (rs != null) {
			while (rs.next()) {
				attr.put(rs.getString("attribute_detail_name"), rs.getString("attribute_value"));
			}
		}
		return attr;
	}

	private Map<String, String> lookupSubExternalIds(
			Connection conn, String subscriptionId) throws SQLException {

		CallableStatement proc = null;
		ResultSet rs = null;

		HashMap<String,String> attr = new HashMap<String, String>();

		proc = conn
				.prepareCall("{call CSM.lookupSubExternalIds(?,?) }");

		proc.setString(1, subscriptionId);

		proc.registerOutParameter(2, OracleTypes.CURSOR);
		proc.executeQuery();
		rs = (ResultSet) proc.getObject(2);

		if (rs != null) {
			while (rs.next()) {
				attr.put(rs.getString("externalidtype_name"), rs.getString("externalid_value"));
			}
		}
		return attr;	
	}

//	private Map<String, String> lookupSubOfferStatus(
//			Connection conn, String subOfferId) throws SQLException {
//
//
//		CallableStatement proc = null;
//
//		HashMap<String,String> attr = new HashMap<String, String>();
//
//		proc = conn
//				.prepareCall("{call CSM.lookupSubOfferStatus(?,?) }");
//
//		proc.setString(1, subOfferId);
//
//		proc.registerOutParameter(2, OracleTypes.VARCHAR);
//		proc.executeQuery();
//
//		attr.put("OfferStatus", proc.getString(2));
//		return attr;		
//	}

	private Map<String, String> lookupSubOfferStatus(Connection conn,
			String subOfferId) throws SQLException {

		CallableStatement proc = null;
		HashMap<String, String> attr = new HashMap<String, String>();
		proc = conn.prepareCall("{call CSM.lookupSubOfferStatus(?,?) }");
		proc.setString(1, subOfferId);
		proc.registerOutParameter(2, OracleTypes.VARCHAR);
		proc.executeQuery();
		attr.put("OfferStatusSubscription", proc.getString(2));
		attr.put("OfferStatusVariant", getStatusFromSDPVariant(conn, subOfferId));
		return attr;
	}

	public String getStatusFromSDPVariant(Connection conn, String subOfferId)
			throws SQLException {
		CallableStatement proc = null;
		proc = conn.prepareCall("{call CSM.getStatusFromSDPVariant(?,?) }");
		proc.setString(1, subOfferId);
		proc.registerOutParameter(2, OracleTypes.VARCHAR);
		proc.executeQuery();
		return proc.getString(2);
	}
	
	private Map<String, String> lookupSubOfferExternalIds(
			Connection conn, String subOfferId) throws SQLException {
		
		CallableStatement proc = null;
		ResultSet rs = null;

		HashMap<String,String> attr = new HashMap<String, String>();

		proc = conn
				.prepareCall("{call CSM.lookupSubOfferExternalIds(?,?) }");

		proc.setString(1, subOfferId);

		proc.registerOutParameter(2, OracleTypes.CURSOR);
		proc.executeQuery();
		rs = (ResultSet) proc.getObject(2);

		if (rs != null) {
			while (rs.next()) {
				attr.put(rs.getString("externalidtype_name"), rs.getString("externalid_value"));
			}
		}
		return attr;	

	}

	private Map<String, String> lookupSubOfferAttrs(
			Connection conn, String subOfferId) throws SQLException {
		CallableStatement proc = null;
		ResultSet rs = null;

		HashMap<String,String> attr = new HashMap<String, String>();

		proc = conn
				.prepareCall("{call CSM.lookupSubOfferAttrs(?,?) }");

		proc.setString(1, subOfferId);

		proc.registerOutParameter(2, OracleTypes.CURSOR);
		proc.executeQuery();
		rs = (ResultSet) proc.getObject(2);

		if (rs != null) {
			while (rs.next()) {
				attr.put(rs.getString("attribute_detail_name"), rs.getString("ATTRIBUTE_VALUE"));
			}
		}
		return attr;	


	}

	private Map<String, String> lookupSrvVarntExternalIds(
			Connection conn, String subSrvcVarntId) throws SQLException {
		CallableStatement proc = null;
		ResultSet rs = null;

		HashMap<String,String> attr = new HashMap<String, String>();

		proc = conn
				.prepareCall("{call CSM.lookupSrvVarntExternalIds(?,?) }");

		proc.setString(1, subSrvcVarntId);

		proc.registerOutParameter(2, OracleTypes.CURSOR);
		proc.executeQuery();
		rs = (ResultSet) proc.getObject(2);

		if (rs != null) {
			while (rs.next()) {
				attr.put(rs.getString("externalidtype_name"), rs.getString("externalid_value"));
			}
		}
		return attr;
	}

	private Map<String, String> lookupSrvVarntAttrs(
			Connection conn, String subSrvcVarntId) throws SQLException {
		CallableStatement proc = null;
		ResultSet rs = null;

		HashMap<String,String> attr = new HashMap<String, String>();

		proc = conn
				.prepareCall("{call CSM.lookupSrvVarntAttrs(?,?) }");

		proc.setString(1, subSrvcVarntId);

		proc.registerOutParameter(2, OracleTypes.CURSOR);
		proc.executeQuery();
		rs = (ResultSet) proc.getObject(2);

		if (rs != null) {
			while (rs.next()) {
				attr.put(rs.getString("attribute_detail_name"), rs.getString("ATTRIBUTE_VALUE"));
			}
		}
		return attr;	
	}

	
}

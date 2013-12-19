package com.bestbuy.sdp.kcb;

import java.io.*;
import java.util.*;

import com.accenture.common.ex.ApplicationException;
import com.accenture.common.util.StringUtil;

/**
 * Represents an instance of a software key, as defined for insertion into and retrieval from
 * the <code>KeyCodeBank</code>.
 */
public class KeyCode implements Comparable<KeyCode> {
	private String vendorID;
	private String keyCodeValue;
	private Properties properties = new Properties();
	private String sku;
	private String masterItemId;
	private String keyCodeActual;
	private String orderID;
	private Status status = Status.FREE;
	public enum OrdStatus { FREE, RESERVED, ACTIVATED, IGNORE, CANCELLED};
	
	private static final String SN_PROP_NAME = "SerialNumber";
	
	public KeyCode(){}
	
	public String getVendorID() { return this.vendorID; }
	public void setVendorID(String vendorID) {	this.vendorID = vendorID; }
	
	/**
	 * @return the key code itself
	 */
	public String getValue() { return this.keyCodeValue; }
	public String getKeyCode(){ return this.getValue(); }
	
	public void setValue(String keyCodeValue) { this.keyCodeValue = keyCodeValue; }
	public void setKeyCode(String keyCodeValue){ this.setValue(keyCodeValue); }

	public String getSerialNumber() { return this.getProperty(SN_PROP_NAME); }
	public void setSerialNumber(String serialNumber) { this.setProperty(SN_PROP_NAME, serialNumber); }

	public Properties getProperties() { return this.properties; }
	public void setProperties(Properties properties) {
		// Protect against null
		if (properties == null)
			this.properties = new Properties();
		else
			this.properties = properties; 
	}

	public String getProperty(String propertyName) {
		if(this.properties == null)
			return null;
		return this.properties.getProperty(propertyName);
	}

	public void setProperty(String name, String value) {
		if(this.properties == null)
			this.properties = new Properties();
		this.properties.put(name, value);
	}
	
	public String getSKU() { return sku; }
	public void setSKU(String sku) { this.sku = sku; }
	
	public String getKeyCodeActual() { return keyCodeActual; }
	public void setKeyCodeActual(String keyCodeActual) { this.keyCodeActual = keyCodeActual; }
	
	public String getOrderID() { return orderID; }
	public void setOrderID(String orderID) { this.orderID = orderID; }
	
	public Status getStatus() { return this.status; }
	public void setStatus(Status status) { this.status = status; }
	public void setStatus(String status){ this.status = Status.getInstance(status); }	
	
	/**
	 * Parses a single line of text into a KeyCode instance.
	 * <p>
	 * The line must consist of the Vendor ID, SKU, Key Code, and (optionally) Serial Number, separated
	 * by commas.  Whitespace will be ignored.</p>
	 * <p>
	 * <b>Example (including SN):</b><br/>
	 * <code>KCB, 871200, 1234-5678-90AB, KCB0000000001</code><br/><br/>
	 * ...where "KCB" is the vendor ID, "871200" is the SKU, "1234-5678-90AB" is the key code, and
	 * "KCB0000000001" is the serial number.
	 * </p>
	 * <p>The serial number can be omitted by passing only three columns, or by having nothing following
	 * the third comma:<br/>
	 * <b>Examples (no SN):</b><br/>
	 * (a) <code>KCB, 871200, 1234-5678-90AB</code><br/>
	 * (b) <code>KCB, 871200, 1234-5678-90AB, </code>
	 * @param csv
	 * @return
	 */
	public static KeyCode parse(String csv){
		if(csv == null || csv.length() == 0)
			throw new IllegalArgumentException("Unable to parse - input is null or empty");
		// Check for disallowed characters (anything non-alphanumeric, plus whitespace, dash, and comma)
		if(!csv.matches("[\\s\\w\\n','-'-'_'%]*"))
			throw new IllegalArgumentException("Unable to parse - illegal characters detected in string:\n"+csv);

		// Delimit the columns by comma
		String columns[] = csv.split(",");
		// There must be at least 3 columns, and at most 4
		if(columns == null)
			throw new IllegalArgumentException("Unable to parse - no comma-separated values found in string:\n"+csv);
		if(columns.length < 3 || columns.length > 4)
			throw new IllegalArgumentException("Unable to parse - found "+columns.length+" columns when 3 or 4 were expected in string:\n"+csv);
		
		KeyCode kc = new KeyCode();
		kc.setVendorID(columns[0].trim());
		kc.setSKU(columns[1].trim());
		kc.setValue(columns[2].trim());
		if(columns.length >= 4)
			kc.setSerialNumber(columns[3].trim());
		
		return kc;
	}
	
	/**
	 * Accepts a specifically-formatted file and parses its comma-separated values into a
	 * collection of KeyCode instances.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Collection<KeyCode> parse(File file) throws IOException
	{
		if(file == null || file.length() == 0)
			throw new IllegalArgumentException("Unable to parse - file is null or empty");
		
		BufferedReader br = new BufferedReader (new FileReader(file));
		String line;
		KeyCode kc;
		Collection<KeyCode> parsedKeys = new HashSet<KeyCode>();
		
		/** Check the first line for column headings **/
		if(br == null)
			return parsedKeys;
		else{
			// Get the first line
			line = br.readLine().trim();
			if(line == null || line.length() == 0)
				throw new IllegalArgumentException("Unable to parse first line - null or empty");
			// Check for disallowed characters (anything non-alphanumeric, plus whitespace, dash, and comma)
			if(!line.matches("[\\s\\w\\n','-]*"))
				throw new IllegalArgumentException("Unable to parse first line - illegal characters detected:\n"+line);

			// Delimit the columns by comma
			String columns[] = line.split(",");
			// There must be at least 3 columns, and at most 4
			if(columns == null)
				throw new IllegalArgumentException("Unable to parse first line - no comma-separated values found:\n"+line);
			if(columns.length < 3 || columns.length > 4)
				throw new IllegalArgumentException("Unable to parse first line - found "+columns.length+" columns when 3 or 4 were expected:\n"+line);
			
			// Check for column headings (checking one or two should be sufficient)
			String columnA = columns[0].toUpperCase();
			String columnB = columns[1].toUpperCase();
			if(columnA.startsWith("V") && (columnA.equals("VENDOR") || 
			   columnA.equals("VENDOR ID") || 
			   columnA.equals("VND")) ||
			   columnB.equals("SKU") )
			{
				// first line is column headings - skip it
			}else{
				// parse it as usual
				kc = KeyCode.parse(line);
				parsedKeys.add(kc);
			}
		}
		
		// Read (and validate) the rest of the input file, line-by-line
		while((line = br.readLine()) != null){
			line = line.trim();
			kc = KeyCode.parse(line);
			parsedKeys.add(kc);
		}		
		return parsedKeys;
	}
	
	/**
	 * Checks the KeyCode instance member values against validation rules, throwing an 
	 * <code>ApplicationException</code> the first invalid value.
	 * <p>
	 * <b>Validation Rules:</b>
	 * <ul>
	 * <li><b>Vendor ID: </b>10 or fewer alphanumeric characters</li>
	 * <li><b>SKU: </b> 7 numeric characters</li>
	 * <li><b>Key code: </b> (no validations yet)</li>
	 * <li><b>Serial number: exactly 19 alphanumeric characters</b></li>
	 * </ul>
	 * </p>
	 * @return true if valid
	 * @throws ApplicationException if the instance is invalid
	 */
	public boolean validate() throws ApplicationException{
		// Validate vendor ID 
		if(this.getVendorID() == null 
				|| this.getVendorID().length() == 0 
				|| this.getVendorID().length() > 10)
			throw new ApplicationException("KeyCode invalid, illegal VendorID value: "+this.getVendorID(),1);
		// Validate SKU
		// TODO
		// Validate Key Code (?)
		// TODO
		// Validate Serial Number (only if present)
		// TODO
		return true;
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();

		if(this.getOrderID() != null && this.getOrderID().length() > 0){
			sb.append(this.getOrderID()).append(',');
		}
		sb.append(this.getVendorID()).append(',');
		sb.append(this.getSKU()).append(',');
		sb.append(this.getValue());
		if(this.getSerialNumber() != null && this.getSerialNumber().length() > 0){
			sb.append(',').append(this.getSerialNumber());
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o){
		if(o == this)
			return true;
		if(!(o instanceof KeyCode))
			return false;
		KeyCode kc = (KeyCode)o;
		
		return	(StringUtil.equals(kc.getSKU(), this.getSKU())) &&
				(StringUtil.equals(kc.getOrderID(), this.getOrderID())) &&
				(StringUtil.equals(kc.getKeyCode(), this.getKeyCode())) &&
				(StringUtil.equals(kc.getVendorID(), this.getVendorID())) &&
				(StringUtil.equals(kc.getKeyCodeActual(), this.getKeyCodeActual())) &&
				(kc.getProperties().equals(this.getProperties()));
				// Status is purposely omitted
	}

	@Override
	public int hashCode(){		
		int result = 7;
		result = 13 * result + StringUtil.hashCode(this.getSKU());
		result = 13 * result + StringUtil.hashCode(this.getOrderID());
		result = 13 * result + StringUtil.hashCode(this.getKeyCode());
		result = 13 * result + StringUtil.hashCode(this.getVendorID());
		result = 13 * result + StringUtil.hashCode(this.getKeyCodeActual());
		result = 13 * result + this.getProperties().hashCode();
		// Status is purposely omitted
		return result;
	}

	/**
	 * Note: This class has a natural ordering that is inconsistent with equals.
	 * <p>
	 * Specifically, ordering will be evaluated based on the Vendor ID, SKU, and
	 * Key Code values, in that order.</p>
	 */
	public int compareTo(KeyCode kc) {		
		int result;
		// Sort order: Vendor, SKU, Key-Code
		// Compare vendors
		result = this.getVendorID().compareTo(kc.getVendorID());
		if(result != 0)
			return result;
		// Compare: SKU
		result = this.getSKU().compareTo(kc.getSKU());
		if(result != 0)
			return result;
		// Compare: Key-Code
		return this.getKeyCode().compareTo(kc.getKeyCode());
	}
	
	/**
	 * The <b><code>Status</code></b> of a <code>KeyCode</code> instance indicates its progress
	 * in the activation process.
	 * <p>
	 * The initial state of a KeyCode is <code>FREE</code>, indicating
	 * its availability for assignment to a subscription.  If it is selected during the activation
	 * process, its state will transition to <code>RESERVED</code> and finally to <code>ACTIVATED</code>.</p>
	 */
	public static class Status{
		public static final Status FREE = new Status("Free");
		public static final Status RESERVED = new Status("Reserved");
		public static final Status ACTIVATED = new Status("Activated");
		public static final Status IGNORE = new Status("Ignore");
		public static final Status CANCELLED = new Status("Cancelled");
		
		private String value;
		private Status(String value){ this.value = value; }
		
		public static Status getInstance(String status){
			if(RESERVED.toString().equalsIgnoreCase(status))
				return RESERVED;
			if(ACTIVATED.toString().equalsIgnoreCase(status))
				return ACTIVATED;
			if(FREE.toString().equalsIgnoreCase(status))
				return FREE;
			if(IGNORE.toString().equalsIgnoreCase(status))
				return IGNORE;
			if(CANCELLED.toString().equalsIgnoreCase(status))
				return CANCELLED;
			// No match found: throw exception
			throw new IllegalArgumentException("Input unrecognized: unable to convert ["+status+"] to KeyCode.Status");
		}
		
		public String toString(){ return this.value; }
		
		public boolean equals(Object o){ 
			if(o == this)
				return true;
			if(!(o instanceof KeyCode.Status))
				return false;
			KeyCode.Status s = (KeyCode.Status)o;
			return (StringUtil.equals(s.value, this.value));
		}
		
		public int hashCode(){
			return 13 + StringUtil.hashCode(this.value);
		}
	}

	public String getMasterItemId() {
		return masterItemId;
	}

	public void setMasterItemId(String masterItemId) {
		this.masterItemId = masterItemId;
	}
}
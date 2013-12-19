package com.bestbuy.sdp.provisioning.test;

import java.util.*;

import static com.accenture.common.util.FileUtil.*;
import static com.accenture.common.util.IntegerUtil.zeroPad;

import com.bestbuy.sdp.kcb.KeyCode;
import com.bestbuy.sdp.provisioning.*;
import com.accenture.common.util.RandomString;

public class KeyCodeDummyData {
	
	// Suppress the default constructor
	private KeyCodeDummyData() {}
	
	static final String ADB_ID = "ADB00";
	static final String KSP_ID = "KSP00";
	static final String LJK_ID = "LJK00";
	static final String RXO_ID = "RXO00";
	static final String TRN_ID = "TRN00";
	static final String WBR_ID = "WBR00";
	static final String[] ADB_SKUS = new String[] { };
	static final String[] KSP_SKUS = new String[] { "8870778", "8870796", "9315653", "9315662" };
	static final String[] LJK_SKUS = new String[] { };
	static final String[] RXO_SKUS = new String[] { };
	static final String[] TRN_SKUS = new String[] { "8870929", "8870938", "9315671", "9315699" };
	static final String[] WBR_SKUS = new String[] { "8870947", "8869236", "9315706", "9315715" };
	
	static final HashMap<String, String[]> VENDOR_SETS;
	static {
		VENDOR_SETS = new HashMap<String, String[]>(6);
		VENDOR_SETS.put(ADB_ID, ADB_SKUS);
		VENDOR_SETS.put(KSP_ID, KSP_SKUS);
		VENDOR_SETS.put(LJK_ID, LJK_SKUS);
		VENDOR_SETS.put(RXO_ID, RXO_SKUS);
		VENDOR_SETS.put(TRN_ID, TRN_SKUS);
		VENDOR_SETS.put(WBR_ID, WBR_SKUS);
	}

	static Random r = new Random();
	static RandomString kc = new RandomString(5);
	static RandomString sn = new RandomString(17);
	
	static String randomSku() {
		final String[] PREFIXES = new String[] { "88", "89", "91" };

		StringBuffer sku = new StringBuffer();

		String s = PREFIXES[(r.nextInt(PREFIXES.length - 1))];
		sku.append(s);
		s = zeroPad(r.nextInt(99999), 5);
		sku.append(s);

		return sku.toString();
	}

	private static String randomKeyCode() {

		StringBuffer keyCode = new StringBuffer();

		int max = 3;
		for (int a = 0; a < max; a++) {
			keyCode.append(kc.nextString());
			if (a < max - 1)
				keyCode.append('-');
		}

		return keyCode.toString();
	}

	private static String randomSerialNumber() {
		StringBuffer sb = new StringBuffer();
		return sb.append("SN").append(sn.nextString()).toString();
	}

	static final String URL = "jdbc:oracle:thin:@localhost:1521:SDPDEV";
	static final String USER = "CNF_TPA_SCH01";

	static final int MIN_KEYS_PER_SKU = 100;
	static final int MAX_KEYS_PER_SKU = 100;
	
	private static SortedSet<KeyCode> generate(HashMap<String, String[]> vendorMap){
		Set<String> vendorSet = vendorMap.keySet();
		Iterator<String> iter = vendorSet.iterator();

		String vendorID;
		String[] vendorSKUs;
		KeyCode k = null;
		int iRandomSize;
		SortedSet<KeyCode> keyCodes = new TreeSet<KeyCode>();
		
		// For each vendor...
		while (iter.hasNext()) {
			vendorID = iter.next();
			vendorSKUs = VENDOR_SETS.get(vendorID);
			// For each SKU for this vendor...
			for (int a = 0; a < vendorSKUs.length; a++) {
				// Randomly choose how many entries to generate
				if(MIN_KEYS_PER_SKU == MAX_KEYS_PER_SKU)
					iRandomSize = MAX_KEYS_PER_SKU;
				else
					iRandomSize = MIN_KEYS_PER_SKU + r.nextInt(Math.abs(MAX_KEYS_PER_SKU - MIN_KEYS_PER_SKU));
				// Load random keycodes for the SKU
				for (int b = 0; b < iRandomSize; b++) {
					k = new KeyCode();
					k.setVendorID(vendorID);
					k.setSKU(vendorSKUs[a]);
					k.setValue(randomKeyCode());
					k.setSerialNumber(randomSerialNumber());
					keyCodes.add(k);
				}
			}
		}
		return keyCodes;
	}
	
	private static void write(String fileName) throws java.io.IOException{
		SortedSet<KeyCode> keyCodes = generate(VENDOR_SETS);
		Iterator<KeyCode> iter = keyCodes.iterator();
		
		java.io.Writer w = getWriter(fileName);
		KeyCode kc;
		int count = 0;
		while(iter.hasNext()){
			kc = iter.next();
			w.append(kc.getVendorID());
			w.append(", ");
			w.append(kc.getSKU());
			w.append(", ");
			w.append(kc.getValue());
			if(kc.getSerialNumber() != null && kc.getSerialNumber().length() > 0){
				w.append(", ");
				w.append(kc.getSerialNumber());
			}
			w.append('\n');
			// flush the writer every so often
			if(++count == 100){
				w.flush();
				count = 0;
			}
		}
		closeWriter(w);
	}

	public static void main(String args[]) throws Throwable {
		write("allVendors-dummyData.csv");
	}
}

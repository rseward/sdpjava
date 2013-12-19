package com.bestbuy.sdp.provisioning.test;

import java.io.*;
import java.util.Collection;

import com.bestbuy.sdp.kcb.KeyCode;
import com.bestbuy.sdp.kcb.KeyCodeBank;

import static com.accenture.common.util.DatabaseUtil.getConnection;

public class KeyCodeBankLoader {

	static final String LOCAL_URL = "jdbc:oracle:thin:@localhost:1521:SDPDEV";
	static final String LOCAL_USER= "CNF_TPA_SCH01";
	
	static final String PTB_URL = "jdbc:oracle:thin:@dld25db-crs:20020:ot01g4db01";
	static final String PTB_USER= "CNF_TPA_ONL31";
	static final String PTB_PASS= "Snipp3r";
	
	static final String REAL_DATA = "C:\\01 best-buy\\test data and xml\\key-code-bank\\_standardized-load-files\\real-test-data";
	static final String DUMMY_DATA= "C:\\01 best-buy\\test data and xml\\key-code-bank\\_standardized-load-files\\dummy-data";
	
	public static void main(String args[]){
		try{
			KeyCodeBankTest.wipeKCB(getConnection(PTB_URL,PTB_USER,PTB_PASS));
			//load(REAL_DATA);
			load(DUMMY_DATA);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void load(String fileName) throws java.sql.SQLException, IOException
	{
		File file = new File(fileName);
		if(file.isDirectory()){
			File[] files = file.listFiles();
			
			KeyCodeBank kcb = new KeyCodeBank(getConnection(PTB_URL,PTB_USER,PTB_PASS));
			Collection<KeyCode> keyCodes = null;
			// Load every .csv file into the KeyCode Bank
			for(int a=0; a < files.length; a++){
				if(files[a].getName().endsWith(".csv")){
					// Load every .csv file into the KeyCode Bank
					keyCodes = KeyCode.parse(files[a]);
					kcb.load(keyCodes);
				}
			}
		}		
	}
	
}

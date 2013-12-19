package com.bestbuy.sdp.common.util;

public class Utility {

	public static void sleepThread(long numOfMilliSeconds){
		
		try {
			Thread.sleep(numOfMilliSeconds);
		} catch (InterruptedException e) {
			Logger.logStackTrace(e.fillInStackTrace());
		}	
	}
}

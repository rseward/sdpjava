package com.bestbuy.sdp.common.util;


public class Logger {

	public static void log(String msg) {
		//System.out.println(msg);
	}

	public static void logStackTrace(Throwable fillInStackTrace) {
		fillInStackTrace.printStackTrace();
	}

	public static void log(Object object) {
		//System.out.println(object);
	}
}

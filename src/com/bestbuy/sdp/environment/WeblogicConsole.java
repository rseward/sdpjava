package com.bestbuy.sdp.environment;

import java.util.Date;
import java.text.SimpleDateFormat;

public class WeblogicConsole {

	/* Mimic the default WebLogic console message format for dates */
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a z");

	private WeblogicConsole()
	{}

	public static void debug(String message)
	{
		//System.out.println('<' +sdf.format(new Date())+"> <Debug> "+message);
	}

	public static void notice(String message)
	{
		//System.out.println('<' +sdf.format(new Date())+"> <Notice> "+message);
	}

	public static void warn(String message)
	{
		//System.out.println('<' +sdf.format(new Date())+"> <Warning> "+message);
	}

	public static void error(String message)
	{
		//System.out.println('<' +sdf.format(new Date())+"> <Error> "+message);
	}
}

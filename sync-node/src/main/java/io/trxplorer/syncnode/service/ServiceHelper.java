package io.trxplorer.syncnode.service;

import java.util.regex.Pattern;

public class ServiceHelper {

	public static boolean isTimeStampValid(String inputString)
	{ 
		
		 Pattern p = Pattern.compile("^\\d{4}[-]?\\d{1,2}[-]?\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}[.]?\\d{1,6}$");
		 
		 
		 return p.matcher(inputString).matches();
	}
	

	
}

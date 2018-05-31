package io.trxplorer.webapp.utils;

import org.apache.commons.lang3.StringUtils;

public class TransactionHelper {

	
	public static String getTrxAmount(String amount) {
	
		if (StringUtils.isNotBlank(amount)) {
			return String.format("%f", (double)Long.valueOf(amount)/1000000l);
		}
			return ""; 
	}
	
	public static String getTrxAmount(long value) {
			return String.format("%f", (double)Long.valueOf(value)/1000000l);

	}
}

package io.trxplorer.webapp.utils;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class TransactionHelper {

	
	public static String getTrxAmount(String amount) {
		
		
		if (StringUtils.isNotBlank(amount)) {
			return NumberFormat.getNumberInstance(Locale.US).format((double)Long.valueOf(amount)/1000000l);
		}
			return ""; 
	}
	
	public static String getTrxAmount(long value) {
			return  NumberFormat.getNumberInstance(Locale.US).format((double)Long.valueOf(value)/1000000l);

	}
}

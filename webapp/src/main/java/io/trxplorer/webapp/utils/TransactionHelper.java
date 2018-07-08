package io.trxplorer.webapp.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class TransactionHelper {

	private final static BigDecimal TRX= BigDecimal.valueOf(1000000l);
	
	public static String getTrxAmount(String amount) {
		
		
		if (StringUtils.isNotBlank(amount)) {
			
			return NumberFormat.getNumberInstance(Locale.US).format(new BigDecimal(amount).divide(TRX,6, RoundingMode.DOWN));
		}
			return ""; 
	}
	
	public static String getTrxAmount(long value) {
			return  NumberFormat.getNumberInstance(Locale.US).format(new BigDecimal(value).divide(TRX,6, RoundingMode.DOWN));

	}
}

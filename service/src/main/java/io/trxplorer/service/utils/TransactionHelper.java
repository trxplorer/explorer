package io.trxplorer.service.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class TransactionHelper {

	private final static long TRX= 1000000l;
	
	public static String getTrxAmount(String amount) {
		
		
		if (StringUtils.isNotBlank(amount)) {
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
			nf.setMaximumFractionDigits(7);
			return nf.format(new BigDecimal(amount).divide(new BigDecimal(TRX)));
		}
			return ""; 
	}
	
	public static String getTrxAmount(long value) {
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
			nf.setMaximumFractionDigits(7);
			return  nf.format(new BigDecimal(value).divide(new BigDecimal(TRX)));

	}

	
}

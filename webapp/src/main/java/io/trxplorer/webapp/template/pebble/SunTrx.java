package io.trxplorer.webapp.template.pebble;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.mitchellbosecke.pebble.extension.Filter;

public class SunTrx implements Filter{

	private final static long TRX= 1000000l;
	
	@Override
	public List<String> getArgumentNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args) {
		
		

		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
		nf.setMaximumFractionDigits(7);
		
		if (input instanceof Long ) {
			
			return nf.format(new BigDecimal((Long)input).divide(new BigDecimal(TRX)));
			
		}else {

			String amount = (String) input;
			if (StringUtils.isNotBlank(amount)) {
				
				
				return nf.format(new BigDecimal(amount).divide(new BigDecimal(TRX)));
			}
			
		}

			return ""; 
		
	}

}

package io.trxplorer.webapp.template.pebble;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.mitchellbosecke.pebble.extension.Filter;

public class NumberLocale implements Filter{

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
			
			return nf.format((Long)input);
			
		}else {

			String amount = (String) input;
			if (StringUtils.isNotBlank(amount)) {
				
				
				return NumberFormat.getNumberInstance(Locale.US).format(amount);
			}
			
		}

			return ""; 
		
	}

}

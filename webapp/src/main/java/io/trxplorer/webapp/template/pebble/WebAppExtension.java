package io.trxplorer.webapp.template.pebble;

import java.util.HashMap;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;

public class WebAppExtension extends AbstractExtension{

	
	@Override
	public Map<String, Filter> getFilters() {
		
		HashMap<String,Filter> map = new HashMap<>();
		
		map.put("suntrx", new SunTrx());
		map.put("numberlocale", new NumberLocale());
		
		return map;
	}
	
}

package io.trxplorer.searchengine;

import com.google.inject.Inject;
import com.typesafe.config.Config;

public class SearchEngineConfig{

	private Config config;
	 
	
	@Inject
	public SearchEngineConfig(Config config) {
		this.config = config;
	}

	public boolean isIndexEnabled() {
		return this.config.getBoolean("index.enabled");
	}
}

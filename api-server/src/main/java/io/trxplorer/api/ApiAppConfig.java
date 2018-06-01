package io.trxplorer.api;

import java.util.Random;

import com.google.inject.Inject;
import com.typesafe.config.Config;

public class ApiAppConfig{

	private Config config;
	
	private int generatedNodeId; 
	
	@Inject
	public ApiAppConfig(Config config) {
		this.config = config;
		Random r = new Random();
		this.generatedNodeId = r.nextInt(1000000-1000)+1000;
	}
	
	public String getBaseUrl() {
		return this.config.getString("application.baseUrl");
	}
	
	public int getNodeId() {
		int id = this.config.getInt("node.id");
		return id==-1 ? this.generatedNodeId : id;
	}
	
	public StatusConfig getStatus(){
		return new StatusConfig(this.config.getConfig("status"));
	}
	
	public SearchEngineConfig getSearchEngine() {
		return new SearchEngineConfig(this.config.getConfig("searchengine"));
	}

	
	public boolean isTronMainNet() {
		return this.config.getBoolean("tron.mainNet");
	}
	
	public static class StatusConfig {
		
		private Config config;

		public StatusConfig(Config config) {
			this.config = config;
		}
		
		public int getOkBlocksLate() {
			return this.config.getInt("ok");
		}

		public int getKoBlocksLate() {
			return this.config.getInt("ko");
		}
	}
	
	public static class SearchEngineConfig {
		
		private Config config;

		public SearchEngineConfig(Config config) {
			this.config = config;
		}
		
		public boolean isEnabled() {
			return this.config.getBoolean("enabled");
		}
		
		public String getEndpoint() {
			return this.config.getString("endpoint");
		}
	}
	
}

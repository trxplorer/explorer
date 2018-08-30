package io.trxplorer.syncnode.job;

import org.jooby.quartz.Scheduled;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.syncnode.SyncNodeConfig;
import io.trxplorer.syncnode.service.MarketSyncService;

@Singleton
@DisallowConcurrentExecution
public class MarketJob {

	private MarketSyncService marketService;
	private SyncNodeConfig config;
	
	@Inject
	public MarketJob(MarketSyncService marketService,SyncNodeConfig config) {
		this.marketService = marketService;
		this.config = config;
	}
	
	@Scheduled("5m")
	public void syncMarkets() {
		
		if (!this.config.isMartketJobEnabled()) {
			return;
		}
		
		this.marketService.syncMarkets();

	}
	
	
}

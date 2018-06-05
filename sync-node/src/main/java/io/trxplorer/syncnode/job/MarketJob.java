package io.trxplorer.syncnode.job;

import org.jooby.quartz.Scheduled;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.syncnode.service.MarketSyncService;

@Singleton
@DisallowConcurrentExecution
public class MarketJob {

	private MarketSyncService marketService;
	
	@Inject
	public MarketJob(MarketSyncService marketService) {
		this.marketService = marketService;
	}
	
	@Scheduled("5m")
	public void syncMarkets() {
		
		this.marketService.syncMarkets();

	}
	
	
}

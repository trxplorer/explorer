package io.trxplorer.syncnode.job;

import org.jooby.quartz.Scheduled;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.syncnode.service.WitnessSyncService;

@Singleton
@DisallowConcurrentExecution
public class WitnessSyncJob {

	private WitnessSyncService witnessSyncService;


	@Inject
	public WitnessSyncJob(WitnessSyncService witnessSyncService) {
		this.witnessSyncService = witnessSyncService;
	}
	
	@Scheduled("30s")
	public void syncWitness() {
		
		
		this.witnessSyncService.syncWitnesses();
		
	}
	
	
	
	
}

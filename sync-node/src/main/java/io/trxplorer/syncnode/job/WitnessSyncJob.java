package io.trxplorer.syncnode.job;

import org.jooby.quartz.Scheduled;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.syncnode.SyncNodeConfig;
import io.trxplorer.syncnode.service.WitnessSyncService;

@Singleton
@DisallowConcurrentExecution
public class WitnessSyncJob {

	private WitnessSyncService witnessSyncService;
	private SyncNodeConfig config;


	@Inject
	public WitnessSyncJob(WitnessSyncService witnessSyncService,SyncNodeConfig config) {
		this.witnessSyncService = witnessSyncService;
		this.config = config;
	}
	
	@Scheduled("15s")
	public void syncWitness() {
		
		if (!this.config.isWitnessJobEnabled()) {
			return;
		}
		
		this.witnessSyncService.syncWitnesses();
		
	}
	
	
	
	
}

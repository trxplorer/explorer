package io.trxplorer.syncnode.job;

import org.jooby.quartz.Scheduled;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.syncnode.service.ForkService;

@Singleton
@DisallowConcurrentExecution
public class ForkCheckJob {


	private ForkService forkService;
	

	@Inject
	public ForkCheckJob(ForkService forkService) {
	
		this.forkService = forkService;
	}
	
	@Scheduled("10m")
	public void checkFork() {
		
		this.forkService.doCheck(this.forkService.getMaxSyncedBlockNum());
		

	}
	

}

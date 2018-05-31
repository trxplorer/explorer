package io.trxplorer.syncnode.job;

import org.jooby.quartz.Scheduled;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.syncnode.service.BlockService;
import io.trxplorer.syncnode.service.WitnessService;

@Singleton
@DisallowConcurrentExecution
public class ConsolidationJob {

	private BlockService blockService;
	private WitnessService witnessService;

	@Inject
	public ConsolidationJob(BlockService blockService,WitnessService witnessService) {
		this.blockService = blockService;
		this.witnessService = witnessService;
	}
	
	@Scheduled("1m")
	public void consolidateBlocks() {
		

		//this.blockService.updateMissingBlocksHash();
		
		this.blockService.updateMissingWitnessId();
		
	}
	
	@Scheduled("1m")
	public void consolidateWitness() {
		
		this.witnessService.updateMissingAccountId();
		
	}
	
	
}

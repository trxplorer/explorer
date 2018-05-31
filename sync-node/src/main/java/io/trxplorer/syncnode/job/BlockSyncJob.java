package io.trxplorer.syncnode.job;

import org.jooby.quartz.Scheduled;
import org.quartz.DisallowConcurrentExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.common.overlay.client.WalletGrpcClient;
import org.tron.core.config.Parameter.ChainConstant;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.syncnode.service.BlockService;
import io.trxplorer.syncnode.service.BlockSyncService;
import io.trxplorer.syncnode.service.ServiceException;
import io.trxplorer.troncli.TronCli;

@Singleton
@DisallowConcurrentExecution
public class BlockSyncJob {

	private TronCli client;
	private BlockSyncService blockSyncService;
	private static final Logger logger = LoggerFactory.getLogger(BlockSyncJob.class);
	
	private Long lastBlockNum = null;
	
	@Inject
	public BlockSyncJob(BlockSyncService syncBlockService,BlockService blockService,TronCli tronCli) {
		this.client = tronCli;
		this.blockSyncService = syncBlockService;
	}
	
	//@Scheduled(ChainConstant.BLOCK_PRODUCED_INTERVAL+"ms")
	public void validateBlockChainSync() throws ServiceException {

		this.lastBlockNum = client.getLastBlock().getBlockHeader().getRawData().getNumber();
		
		logger.info("current block:"+this.lastBlockNum);
		if (this.blockSyncService.isInitialSync()) {
			logger.info("Initial import ... that might take a moment, grab a coffe ...");
			this.blockSyncService.createInitSync(this.lastBlockNum);
		}else {
			this.blockSyncService.validateBlockChainSync(this.lastBlockNum);			
		}


		
		this.blockSyncService.syncBlocks();	
	}

	@Scheduled(ChainConstant.BLOCK_PRODUCED_INTERVAL+"ms")
	public void syncNodeBlocks() throws ServiceException {
		
		this.lastBlockNum = client.getLastBlock().getBlockHeader().getRawData().getNumber();
		
		logger.info("current block:"+this.lastBlockNum);
		
		if (this.blockSyncService.isInitialSync()) {
			logger.info("Initial import ... that might take a moment, grab a coffe ...");
		}


		this.blockSyncService.syncNode(this.lastBlockNum);
		
	}

	
	
}

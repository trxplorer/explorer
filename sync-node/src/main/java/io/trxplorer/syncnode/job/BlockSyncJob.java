package io.trxplorer.syncnode.job;

import org.jooby.quartz.Scheduled;
import org.quartz.DisallowConcurrentExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.core.config.Parameter.ChainConstant;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.syncnode.SyncNodeConfig;
import io.trxplorer.syncnode.service.BlockService;
import io.trxplorer.syncnode.service.BlockSyncService;
import io.trxplorer.syncnode.service.ServiceException;
import io.trxplorer.troncli.TronFullNodeCli;
import io.trxplorer.troncli.TronSolidityNodeCli;

@Singleton
@DisallowConcurrentExecution
public class BlockSyncJob {

	private TronFullNodeCli fullNodeClient;
	private TronSolidityNodeCli solidityNodeClient;
	private BlockSyncService blockSyncService;
	private SyncNodeConfig config;
	private static final Logger logger = LoggerFactory.getLogger(BlockSyncJob.class);
	

	
	@Inject
	public BlockSyncJob(BlockSyncService syncBlockService,BlockService blockService,TronFullNodeCli tronFullNodeCli,TronSolidityNodeCli tronSolidityCli,SyncNodeConfig config) {
		this.fullNodeClient = tronFullNodeCli;
		this.blockSyncService = syncBlockService;
		this.solidityNodeClient = tronSolidityCli;
		this.config = config;
	}
	
	//@Scheduled(ChainConstant.BLOCK_PRODUCED_INTERVAL+"ms")
	public void validateBlockChainSync() throws ServiceException {

		Long lastBlockNum = fullNodeClient.getLastBlock().getBlockHeader().getRawData().getNumber();
		
		logger.info("current block:"+lastBlockNum);
		if (this.blockSyncService.isInitialSync()) {
			logger.info("Initial import ... that might take a moment, grab a coffe ...");
			this.blockSyncService.createInitSync(lastBlockNum);
		}else {
			this.blockSyncService.validateBlockChainSync(lastBlockNum);			
		}


		
		this.blockSyncService.syncNodeBlocks();	
	}

	@Scheduled(ChainConstant.BLOCK_PRODUCED_INTERVAL+"ms")
	public void syncFullNodeBlocks() throws ServiceException {
		
		if (!this.config.isBlockJobEnabled()) {
			return;
		}
		
		Long lastBlockNum = fullNodeClient.getLastBlock().getBlockHeader().getRawData().getNumber();
		
		logger.info("current full node block:"+lastBlockNum);
		
		if (this.blockSyncService.isInitialSync()) {
			logger.info("Initial import ... that might take a moment, grab a coffe ...");
		}


		this.blockSyncService.syncNodeFull(lastBlockNum);
		
	}
	
	
	@Scheduled(ChainConstant.BLOCK_PRODUCED_INTERVAL+10+"ms")
	public void syncSolidityNodeBlocks() throws ServiceException {
		
		if (!this.config.isBlockJobEnabled()) {
			return;
		}
		
		Long lastBlockNum = solidityNodeClient.getLastBlock().getBlockHeader().getRawData().getNumber();
		
		logger.info("current solidity node block:"+lastBlockNum);
		

		this.blockSyncService.syncNodeSolidity(lastBlockNum);
		
	}
	
	@Scheduled("1m")
	public void reimportErrorBlock() {
		
		this.blockSyncService.reimportErrorBlock();
		
	}
	
	
}

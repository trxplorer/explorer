package io.trxplorer.syncnode.service;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.InsertFinalStep;
import org.jooq.Query;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.protos.Protocol.Block;

import com.google.common.collect.ComparisonChain;
import com.google.inject.Inject;

import io.trxplorer.model.tables.pojos.SyncNode;
import io.trxplorer.model.tables.records.SyncBlockRecord;
import io.trxplorer.syncnode.SyncNodeConfig;
import io.trxplorer.troncli.TronFullNodeCli;

public class BlockSyncService {

	private DSLContext dslContext;

	private final long FIX_SYNC_BLOCK_POOL_SIZE = 100;

	private BlockService blockService;

	private SyncNodeConfig config;

	private TronFullNodeCli fullNodeCli;

	private static final Logger logger = LoggerFactory.getLogger(BlockSyncService.class);
	
	@Inject
	public BlockSyncService(DSLContext dslContext,SyncNodeConfig config,BlockService blockService,TronFullNodeCli fullNodeCli) {
		this.dslContext = dslContext;
		this.blockService = blockService;
		this.config = config;
		this.fullNodeCli = fullNodeCli;
	}
	
	/**
	 * Decide which blocks will be assigned to this node
	 * @param currentBlockNum
	 */
	public void prepareFullNodeSync(long currentBlockNum) {
		
		SyncNode syncNode = this.dslContext.select(SYNC_NODE.fields()).from(SYNC_NODE).where(SYNC_NODE.NODE_ID.eq(config.getNodeId()))
		.fetchOneInto(SyncNode.class);
		
		if (syncNode.getStartFullDate()!=null && syncNode.getEndFullDate()==null) {
			logger.info("=> Previous fullnode sync didn't seem to went well ... resyncing the batch ...");
			syncFullNodeNodeBlocks();
		}
		
		Long maxBlock = this.dslContext.select(DSL.max(SYNC_NODE.SYNC_END_FULL)).from(SYNC_NODE).fetchOneInto(Long.class);
		
		Long syncStart = 0l;
		Long syncStop = currentBlockNum;
		
		if (maxBlock==null) {
			maxBlock=0l;
		}
		
		if (maxBlock==0l) {
			syncStart = 0l;
			syncStop = (long)config.getSyncBatchSize();		
		}else {
			syncStart = maxBlock;
			syncStop = maxBlock + config.getSyncBatchSize();
		}
		
		if (syncStop>currentBlockNum) {
			syncStop = currentBlockNum;
		}
		
		this.dslContext.update(SYNC_NODE)
		.set(SYNC_NODE.SYNC_START_FULL,syncStart)
		.set(SYNC_NODE.SYNC_END_FULL,syncStop)
		.where(SYNC_NODE.NODE_ID.eq(config.getNodeId()))
		.execute();
		
		
	}

	public void syncFullNodeNodeBlocks() {
		
		SyncNode syncNode = this.dslContext.select(SYNC_NODE.fields()).from(SYNC_NODE).where(SYNC_NODE.NODE_ID.eq(config.getNodeId()))
		.fetchOneInto(SyncNode.class);
		logger.info("==> Ready to sync full node block from {} to {} ...",syncNode.getSyncStartFull(),syncNode.getSyncEndFull());
		
		this.dslContext.update(SYNC_NODE)
		.set(SYNC_NODE.START_FULL_DATE,Timestamp.valueOf(LocalDateTime.now()))
		.set(SYNC_NODE.END_FULL_DATE,DSL.val((Timestamp)null))
		.where(SYNC_NODE.NODE_ID.eq(config.getNodeId()))
		.execute();

		syncBlocks(syncNode.getSyncStartFull(), syncNode.getSyncEndFull());

		this.dslContext.update(SYNC_NODE)
		.set(SYNC_NODE.END_FULL_DATE,Timestamp.valueOf(LocalDateTime.now()))
		.where(SYNC_NODE.NODE_ID.eq(config.getNodeId()))
		.execute();

	}
	
	public void syncSolidityNodeBlocks() {
		
		SyncNode syncNode = this.dslContext.select(SYNC_NODE.fields()).from(SYNC_NODE).where(SYNC_NODE.NODE_ID.eq(config.getNodeId()))
		.fetchOneInto(SyncNode.class);
		logger.info("==> Ready to sync solidity block from {} to {} ...",syncNode.getSyncStartSolidity(),syncNode.getSyncEndSolidity());
		
		
		if (syncNode!=null && syncNode.getSyncStartSolidity()!=null &&  syncNode.getSyncEndSolidity()!=null) {
			
			this.dslContext.update(SYNC_NODE)
			.set(SYNC_NODE.START_SOLIDITY_DATE,Timestamp.valueOf(LocalDateTime.now()))
			.set(SYNC_NODE.END_SOLIDITY_DATE,DSL.val((Timestamp)null))
			.where(SYNC_NODE.NODE_ID.eq(config.getNodeId()))
			.execute();
		
			confirmBlocks(syncNode.getSyncStartSolidity(), syncNode.getSyncEndSolidity());

			this.dslContext.update(SYNC_NODE)
			.set(SYNC_NODE.END_SOLIDITY_DATE,Timestamp.valueOf(LocalDateTime.now()))
			.where(SYNC_NODE.NODE_ID.eq(config.getNodeId()))
			.execute();			
		}


	}
	
	public boolean prepareSolidityNodeSync(long currentBlockNum) {
		
		SyncNode syncNode = this.dslContext.select(SYNC_NODE.fields()).from(SYNC_NODE).where(SYNC_NODE.NODE_ID.eq(config.getNodeId()))
		.fetchOneInto(SyncNode.class);
		
		if (syncNode.getStartSolidityDate()!=null && syncNode.getEndSolidityDate()==null) {
			logger.info("=> Previous solidity node sync didn't seem to went well ... resyncing the batch ...");
			syncSolidityNodeBlocks();
		}
		
		Long maxBlock = this.dslContext.select(DSL.max(SYNC_NODE.SYNC_END_SOLIDITY)).from(SYNC_NODE).fetchOneInto(Long.class);
		
		Long maxBlockAvailable = this.dslContext.select(DSL.max(BLOCK.NUM)).from(BLOCK).fetchOneInto(Long.class);
		

		
		Long syncStart = 0l;
		Long syncStop = currentBlockNum;
		
		if (maxBlock==null) {
			maxBlock=0l;
		}

		
		if (maxBlock==0l) {
			syncStart = 0l;
			syncStop = (long)config.getSyncBatchSize();		
		}else {
			syncStart = maxBlock;
			syncStop = maxBlock + config.getSyncBatchSize();
		}
		
		if (syncStop>currentBlockNum) {
			syncStop = currentBlockNum;
		}
		
		// wait for block to be available first before confirming
		if (maxBlockAvailable==null) {
			logger.info("===> skiping sync for now, no max block available");
			return false;
		}
		if (maxBlockAvailable!=null && maxBlockAvailable<syncStop) {
			logger.info("===> skiping sync for now: syncstop = {}, max block available = {}",syncStop,maxBlockAvailable);
			return false;
		}
		
		this.dslContext.update(SYNC_NODE)
		.set(SYNC_NODE.SYNC_START_SOLIDITY,syncStart)
		.set(SYNC_NODE.SYNC_END_SOLIDITY,syncStop)
		.where(SYNC_NODE.NODE_ID.eq(config.getNodeId()))
		.execute();
		
		return true;
	}
	
	public void syncBlocks(long start,long stop) {
		
		List<Block> blocks = new ArrayList<>(fullNodeCli.getBlocks(start, stop));
		
		Collections.sort(blocks, (b1,b2)->{
			 return ComparisonChain.start().compare(b1.getBlockHeader().getRawData().getNumber(),b2.getBlockHeader().getRawData().getNumber()).result();
		});
		
		Iterator<Block> it = blocks.iterator();
		
		while(it.hasNext()) {
			Block block = it.next();
			logger.info("==> Syncing block: {}",block.getBlockHeader().getRawData().getNumber());
			try {
				this.blockService.importBlock(block);
			}catch(Exception e) {
				logger.error("Could not import block {}",block.getBlockHeader().getRawData().getNumber(),e);
			}
			
			
		}
		
	}
	
	public void confirmBlocks(long start,long stop) {
		
		for (long i = start; i < stop; i++) {
			logger.info("==> Confirming block: {}",i);
			try {
				this.blockService.confirmBlock(i);
			}catch(Exception e) {
				logger.error("Could not confirm block {}",i,e);
			}
			
		}
		
	}
	
	public void syncNodeFull(long currentBlockNum) throws ServiceException {
		
		this.prepareFullNodeSync(currentBlockNum);
		this.syncFullNodeNodeBlocks();
		
	}
	
	public void syncNodeSolidity(long currentBlockNum) throws ServiceException  {
		
		boolean ok = this.prepareSolidityNodeSync(currentBlockNum);
		if (ok) {
			this.syncSolidityNodeBlocks();
		}
		
	}
	
	/**
	 * Operates a basic checksum between existing data and last blockchain data. If
	 * the checksum doesn't match fix db data
	 * 
	 * @param fromBlockNum
	 */
	public void validateBlockChainSync(long fromBlockNum) {

		if (fromBlockNum == 0) {
			return;
		}

		long lastCheckSum = (fromBlockNum * (fromBlockNum + 1)) / 2;

		Long dbCheckSum = this.dslContext.select(DSL.sum(SYNC_BLOCK.BLOCK_NUM)).from(SYNC_BLOCK)
				.where(SYNC_BLOCK.BLOCK_NUM.lt(ULong.valueOf(fromBlockNum + 1))).fetchOneInto(Long.class);

		if (dbCheckSum == null) {
			dbCheckSum = 0l;
		}

		if (lastCheckSum != dbCheckSum) {
			long stopBlockNum = fixBlockChainSync(fromBlockNum);
			// continue validation
			this.validateBlockChainSync(stopBlockNum);
		}

	}

	/**
	 * Add missing blocks
	 * 
	 * @param fromBlockNum
	 */
	public long fixBlockChainSync(long fromBlockNum) {

		ArrayList<ULong> blockNums = new ArrayList<>();

		long currentBlockNum = fromBlockNum;

		long stopBlockNum = (fromBlockNum - FIX_SYNC_BLOCK_POOL_SIZE) < 0 ? 0 : fromBlockNum - FIX_SYNC_BLOCK_POOL_SIZE;

		while (currentBlockNum >= stopBlockNum) {

			blockNums.add(ULong.valueOf(currentBlockNum));

			currentBlockNum--;
		}
		
		List<ULong> existingBlocks = this.dslContext.select(SYNC_BLOCK.BLOCK_NUM).from(SYNC_BLOCK)
				.where(SYNC_BLOCK.BLOCK_NUM.in(blockNums)).fetchInto(ULong.class);

		blockNums.removeAll(existingBlocks);

		ArrayList<SyncBlockRecord> records = new ArrayList<>();

		for (ULong blocNum : blockNums) {

			SyncBlockRecord record = new SyncBlockRecord();
			record.setBlockNum(blocNum);
			records.add(record);
		}
		//TODO: use insert and ignore duplicate errors?
		//this.dslContext.insertInto(SYNC_BLOCK).set(SYNC_BLOCK.BLOCK_NUM, ULong.valueOf(0)).
		this.dslContext.batchInsert(records).execute();
		

		return stopBlockNum;
	}

	public void startBlockSync(Long number) {
		this.dslContext.update(SYNC_BLOCK).set(SYNC_BLOCK.DATE_START, Timestamp.valueOf(LocalDateTime.now()))
				.where(SYNC_BLOCK.BLOCK_NUM.eq(ULong.valueOf(number))).execute();
	}

	public void endBlockSync(Long number) {
		this.dslContext.update(SYNC_BLOCK).set(SYNC_BLOCK.DATE_END, Timestamp.valueOf(LocalDateTime.now()))
				.where(SYNC_BLOCK.BLOCK_NUM.eq(ULong.valueOf(number))).execute();
	}
	
	
	
	public void createInitSync(long blockNum) {
		
		List<Query> queries = new ArrayList<>();

		for (long i = 0; i < blockNum; i++) {
			InsertFinalStep<SyncBlockRecord> insert = DSL.insertInto(SYNC_BLOCK).set(SYNC_BLOCK.BLOCK_NUM, ULong.valueOf(i)).onDuplicateKeyIgnore();
			
			queries.add(insert);			
		}
		
		this.dslContext.batch(queries).execute();
		
	}
	
	public void syncNodeBlocks() throws ServiceException {
		
		//TODO:  use config for batch size
		List<Long> blocksToSync = lockBlockSync(10);
		
		for(Long blockNumToSync:blocksToSync) {
			
			logger.info("==> sync block: {}",blockNumToSync);
			this.startBlockSync(blockNumToSync);
			
			//this.blockService.importBlock(blockNumToSync);
			
			this.endBlockSync(blockNumToSync);
		}
		
	}
	
	

	
	public List<Long> lockBlockSync(int batchSize) {
		
		List<Long> result = new ArrayList<>();
		
		SelectConditionStep<Record1<ULong>> maxBlock = DSL.select(DSL.max(SYNC_BLOCK.BLOCK_NUM).add(1)).from(SYNC_BLOCK).where(SYNC_BLOCK.DATE_LOCKED.isNull().and(SYNC_BLOCK.DATE_START.isNull()));
		
		Table<Record1<ULong>> blocksToLock = DSL.select(SYNC_BLOCK.BLOCK_NUM)
		.from(SYNC_BLOCK).where(SYNC_BLOCK.BLOCK_NUM.lt(maxBlock).and(SYNC_BLOCK.DATE_LOCKED.isNull()).and(SYNC_BLOCK.DATE_START.isNull()))
		.orderBy(SYNC_BLOCK.BLOCK_NUM.desc())
		.limit(batchSize).asTable("tmp1");
		
		// Overcome mysql subquery 'limit' limitation  
		SelectJoinStep<Record1<ULong>> subSubQuery = DSL.select(blocksToLock.field(SYNC_BLOCK.BLOCK_NUM.getName(),ULong.class)).from(blocksToLock);
		
		// Lock block batch for this node 
		int nb = this.dslContext.update(SYNC_BLOCK)
		.set(SYNC_BLOCK.DATE_LOCKED, Timestamp.valueOf(LocalDateTime.now()))
		.set(SYNC_BLOCK.NODE_ID,this.config.getNodeId())
		.where(SYNC_BLOCK.BLOCK_NUM.in(subSubQuery)).execute();
		
		result = this.dslContext.select(SYNC_BLOCK.BLOCK_NUM)
		.from(SYNC_BLOCK)
		.where(SYNC_BLOCK.DATE_LOCKED.isNotNull().and(SYNC_BLOCK.DATE_START.isNull()).and(SYNC_BLOCK.NODE_ID.eq(config.getNodeId())))
		.orderBy(SYNC_BLOCK.BLOCK_NUM.desc())
		.limit(batchSize)
		.fetchInto(Long.class);
		

		return result;
	}

	public boolean isInitialSync() {

		Integer genesisBlockExists = this.dslContext.select(DSL.count()).from(BLOCK)
				.where(BLOCK.NUM.eq(ULong.valueOf(1))).fetchOneInto(Integer.class);

		if (genesisBlockExists == 0) {
			return true;
		}

		return false;
	}
	

}

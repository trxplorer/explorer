package io.trxplorer.syncnode.service;

import static io.trxplorer.model.Tables.*;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.BlockHeader.raw;

import com.google.inject.Inject;

import io.trxplorer.troncli.TronFullNodeCli;

public class ForkService {

	private DSLContext dslContext;
	private TronFullNodeCli tronFullNodeCli;

	@Inject
	public ForkService(DSLContext dslContext,TronFullNodeCli tronService) {
		this.dslContext = dslContext;
		this.tronFullNodeCli = tronService;
	}
	
	
	public void doCheck(Long blockNum) {
		
		System.out.println("Checking block:"+blockNum);
		
		io.trxplorer.model.tables.pojos.Block syncedBlock = this.getBlock(blockNum);
		
		
		Block tronBlock = this.tronFullNodeCli.getBlockByNum(syncedBlock.getNum().longValue());
		
		if (!isSameBlock(syncedBlock, tronBlock)) {
			
			//delete invalid blocks
			System.out.println("=> deleting invalid block:"+blockNum);
			deleteBlock(blockNum);
			
			if (blockNum>1) {
				doCheck(blockNum-1);	
			}
			
		}
		
	}
	
	public void deleteBlock(Long num) {
		this.dslContext.deleteFrom(SYNC_BLOCK).where(SYNC_BLOCK.BLOCK_NUM.eq(ULong.valueOf(num))).execute();
		this.dslContext.deleteFrom(BLOCK).where(BLOCK.NUM.eq(ULong.valueOf(num))).execute();
	}
	
	public Long getMaxSyncedBlockNum() {
		return this.dslContext.select(DSL.max(BLOCK.NUM)).from(BLOCK).fetchOneInto(Long.class);
	}
	
	public io.trxplorer.model.tables.pojos.Block getBlock(Long blockNum) {

		return this.dslContext.select(BLOCK.NUM,BLOCK.TIMESTAMP,BLOCK.TX_COUNT).from(BLOCK).where(BLOCK.NUM.eq(ULong.valueOf(blockNum))).fetchOneInto(io.trxplorer.model.tables.pojos.Block.class);
	}
	
	private boolean isSameBlock(io.trxplorer.model.tables.pojos.Block syncedBlock,Block tronBlock) {
		
		raw blockRawData = tronBlock.getBlockHeader().getRawData();
		
		return syncedBlock.getTimestamp().equals(blockRawData.getTimestamp())&&
				syncedBlock.getNum().longValue() == blockRawData.getNumber() &&
				syncedBlock.getTxCount().longValue() == blockRawData.getNumber();
	}
}

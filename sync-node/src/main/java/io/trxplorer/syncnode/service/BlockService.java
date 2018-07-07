package io.trxplorer.syncnode.service;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.Wallet;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.Transaction;

import com.google.inject.Inject;

import io.trxplorer.model.tables.records.BlockRecord;
import io.trxplorer.troncli.TronFullNodeCli;
import io.trxplorer.troncli.TronSolidityNodeCli;

public class BlockService {

	private DSLContext dslContext;
	private TransactionService txService;
	private TronFullNodeCli tronFullNodeCli;
	private TronSolidityNodeCli tronSolidityNodeCli;
	
	@Inject
	public BlockService(DSLContext dslContext,TronFullNodeCli tronFullNodeCli,TronSolidityNodeCli tronSolidityNodeCli,TransactionService txService) {
		this.dslContext = dslContext;
		this.txService = txService;
		this.tronFullNodeCli = tronFullNodeCli;
		this.tronSolidityNodeCli = tronSolidityNodeCli;
	}
	
	/**
	 * Import a new block into db
	 * @param blockNum
	 * @throws ServiceException 
	 */
	public void importBlock(Long blockNum) throws ServiceException {
		
		try {
		Block block = this.tronFullNodeCli.getBlockByNum(blockNum);
		
		BlockRecord record = new BlockRecord();
		
		String parentHash = Sha256Hash.wrap(block.getBlockHeader().getRawData().getParentHash()).toString();
	
		//Sha256Hash.of(block.getBlockHeader().getRawData().toByteArray()).toString()
	
		record.setTxCount(UInteger.valueOf(block.getTransactionsCount()));
		record.setWitnessAddress(Wallet.encode58Check(block.getBlockHeader().getRawData().getWitnessAddress().toByteArray()));
		record.setNum(ULong.valueOf(block.getBlockHeader().getRawData().getNumber()));
		//record.setHash(Sha256Hash.wrap(Sha256Hash.of(block.getBlockHeader().getRawData().toByteArray()).getBytes()).toString());
		record.setParentHash(parentHash);
		record.setTimestamp(Timestamp.valueOf(Instant.ofEpochMilli(block.getBlockHeader().getRawData().getTimestamp()).atOffset(ZoneOffset.UTC).toLocalDateTime()));
		record.setSize(UInteger.valueOf(block.getSerializedSize()));

		
		//store block
		record.attach(this.dslContext.configuration());
		record.store();
		
		if (blockNum>0) {
			this.dslContext.update(BLOCK).set(BLOCK.HASH,parentHash).where(BLOCK.NUM.eq(ULong.valueOf(blockNum-1))).execute();
		}
		
		// Create transactions
		for(Transaction transaction:block.getTransactionsList()) {
			this.txService.importTransaction(transaction, record);
		}
		}catch(ServiceException e) {
			
			this.dslContext.insertInto(BLOCK_ERROR)
			.set(BLOCK_ERROR.BLOCK,ULong.valueOf(blockNum))
			.set(BLOCK_ERROR.ERROR,e.getMessage())
			.execute();
			
			throw new ServiceException("Could not import block:"+blockNum);
			
		}

	}
	
	
	
	public io.trxplorer.model.tables.pojos.Block getBlockByNum(long num){
		
		dslContext.select(BLOCK.fields())
		.from(BLOCK)
		.where(BLOCK.NUM.eq(ULong.valueOf(num)));
		
		
		return null;
	}

	public void updateMissingBlocksHash() {
		
		//set blocks hash
		io.trxplorer.model.tables.Block b1 = BLOCK.as("b1");
		Table<Record2<String, ULong>> b2 = DSL.select(BLOCK.PARENT_HASH,BLOCK.NUM).from(BLOCK).asTable("b2");
		this.dslContext.update(b1).set(b1.HASH,DSL.select(b2.field(BLOCK.PARENT_HASH)).from(b2).where(b2.field(BLOCK.NUM.getName(),ULong.class).eq(DSL.field("b1.num+1",ULong.class)))).where(b1.HASH.isNull()).execute();
		
	}
	
	public void updateMissingWitnessId() {
		
		this.dslContext.update(BLOCK).set(BLOCK.WITNESS_ID,DSL.select(WITNESS.ID).from(WITNESS).where(BLOCK.WITNESS_ADDRESS.eq(WITNESS.ADDRESS))).execute();
		
	}

	
	public void confirmBlock(Long blockNum) {
		
		Block block = this.tronSolidityNodeCli.getBlockByNum(blockNum);
		
		if (block!=null && block.getBlockHeader().getRawData().getNumber()==blockNum) {

			this.dslContext.update(BLOCK)
			.set(BLOCK.CONFIRMED,(byte)1)
			.where(BLOCK.NUM.eq(ULong.valueOf(blockNum)))
			.execute();
			
			HashSet<String> hashes = new HashSet<>();
			
			for(Transaction tx:block.getTransactionsList()) {
				
				hashes.add(Sha256Hash.of(tx.getRawData().toByteArray()).toString());
			
			}
			
			this.dslContext.update(TRANSACTION)
			.set(TRANSACTION.CONFIRMED,(byte)1)
			.where(TRANSACTION.HASH.in(hashes))
			.execute();
			
		}
		

		
	}
}

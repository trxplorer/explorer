package io.trxplorer.syncnode.service;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.tron.common.utils.Sha256Hash;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.Transaction;

import com.google.inject.Inject;

import io.trxplorer.model.tables.records.BlockRecord;
import io.trxplorer.syncnode.utils.Base58;
import io.trxplorer.troncli.TronCli;

public class BlockService {

	private DSLContext dslContext;
	private TransactionService txService;
	private TronCli tronCli;
	
	@Inject
	public BlockService(DSLContext dslContext,TronCli tronCli,TransactionService txService) {
		this.dslContext = dslContext;
		this.txService = txService;
		this.tronCli = tronCli;
	}
	
	/**
	 * Import a new block into db
	 * @param blockNum
	 * @throws ServiceException 
	 */
	public void importBlock(Long blockNum) throws ServiceException {
		
		try {
		Block block = this.tronCli.getBlockByNum(blockNum);
		
		BlockRecord record = new BlockRecord();
		
		String parentHash = Sha256Hash.wrap(block.getBlockHeader().getRawData().getParentHash()).toString();
		
		record.setTxCount(UInteger.valueOf(block.getTransactionsCount()));
		record.setWitnessAddress(Base58.encode58Check(block.getBlockHeader().getRawData().getWitnessAddress().toByteArray()));
		record.setNum(ULong.valueOf(block.getBlockHeader().getRawData().getNumber()));
		record.setHash(Sha256Hash.of(block.getBlockHeader().getRawData().toByteArray()).toString());
		record.setParentHash(parentHash);
		record.setTxtrieroot(block.getBlockHeader().getRawData().getTxTrieRoot().toString());
		record.setTimestamp(new Timestamp(block.getBlockHeader().getRawData().getTimestamp()));
		record.setSize(UInteger.valueOf(block.getSerializedSize()));

		
		//store block
		record.attach(this.dslContext.configuration());
		record.store();
		
		// Create transactions
		for(Transaction transaction:block.getTransactionsList()) {
			this.txService.importTransaction(transaction, record);
		}
		}catch(ServiceException e) {
			
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

	
	
}

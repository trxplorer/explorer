package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectOffsetStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.job.QuickStatsJob;
import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.service.dto.transaction.TransactionCriteria;
import io.trxplorer.service.dto.transaction.TransactionModel;
import io.trxplorer.service.dto.transaction.TransferModel;
import io.trxplorer.service.dto.vote.VoteModel;

@Singleton
public class TransactionService {

	private DSLContext dslContext;
	
	private final int TRON_START_YEAR = 2018;

	private QuickStatsJob quickStats;

	
	@Inject
	public TransactionService(DSLContext dslContext,QuickStatsJob quickStats) {
		this.dslContext  = dslContext;
		this.quickStats = quickStats;
	}
	
	public int getTotalTxLast24h(){
		
		LocalDateTime date = LocalDateTime.now().minusDays(1);

		return this.dslContext.select(DSL.count())
		.from(TRANSACTION)
		.where(TRANSACTION.TIMESTAMP.gt(Timestamp.valueOf(date)))
		.fetchOneInto(Integer.class);
	
	}

	
	public int getTotalVotesLast24h() {
		
		LocalDateTime date = LocalDateTime.now().minusDays(1);
		
		return this.dslContext.select(DSL.count())
		.from(TRANSACTION)
		.join(CONTRACT_VOTE_WITNESS).on(CONTRACT_VOTE_WITNESS.TRANSACTION_ID.eq(TRANSACTION.ID))
		.where(TRANSACTION.TIMESTAMP.gt(Timestamp.valueOf(date)))
		.fetchOneInto(Integer.class);
		 
	}
	
	public List<VoteModel> getLastestVotes(int limit) {

		Table<Record1<ULong>> txTable = DSL.select(TRANSACTION.ID)
		.from(TRANSACTION)
		.join(BLOCK).on(TRANSACTION.BLOCK_ID.eq(BLOCK.ID)).and(DSL.year(TRANSACTION.TIMESTAMP).gt(TRON_START_YEAR-1)).and(DSL.year(TRANSACTION.TIMESTAMP).lt(DSL.year(DSL.currentDate()).plus(1)))
		.where(TRANSACTION.TYPE.eq(4))
		.orderBy(TRANSACTION.TIMESTAMP.desc())
		.limit(limit)
		.asTable("tmp")
		;
		
		return this.dslContext.select(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.as("from"),CONTRACT_VOTE_WITNESS.VOTE_ADDRESS.as("to"),CONTRACT_VOTE_WITNESS.VOTE_COUNT.as("votes"))
		.from(CONTRACT_VOTE_WITNESS)
		.join(txTable).on(txTable.field(0, ULong.class).eq(CONTRACT_VOTE_WITNESS.TRANSACTION_ID))
		.limit(limit)
		.fetchInto(VoteModel.class);
		
 
	}	
	
	public List<TransactionModel> getLatestTransactions(int limit){
		
		return this.dslContext.select(TRANSACTION.HASH,BLOCK.NUM.as("block"))
		.from(TRANSACTION)
		.join(BLOCK).on(TRANSACTION.BLOCK_ID.eq(BLOCK.ID)).and(DSL.year(TRANSACTION.TIMESTAMP).gt(TRON_START_YEAR-1)).and(DSL.year(TRANSACTION.TIMESTAMP).lt(DSL.year(DSL.currentDate()).plus(1)))
		.orderBy(TRANSACTION.TIMESTAMP.desc())
		.limit(limit)
		.fetchInto(TransactionModel.class);
	}
	

	public TransactionModel getTxByHash(String hash) {
		
		TransactionModel result = this.dslContext.select(TRANSACTION.ID,TRANSACTION.HASH,TRANSACTION.TIMESTAMP,BLOCK.NUM.as("block"),TRANSACTION.FROM,TRANSACTION.TYPE,TRANSACTION.CONFIRMED)
				.from(TRANSACTION)
				.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID))
		.where(TRANSACTION.HASH.eq(hash))
		.fetchOneInto(TransactionModel.class);
	
		
		if (result==null) {
			return null;
		}
		
		switch (result.getTypeInt()) {
		case ContractType.VoteWitnessContract_VALUE:
			
			List<VoteModel> votes = this.dslContext.select(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.as("from"),CONTRACT_VOTE_WITNESS.VOTE_ADDRESS.as("to"),CONTRACT_VOTE_WITNESS.VOTE_COUNT.as("votes"))
			.from(CONTRACT_VOTE_WITNESS)
			.where(CONTRACT_VOTE_WITNESS.TRANSACTION_ID.eq(ULong.valueOf(result.getId())))
			.fetchInto(VoteModel.class);
			
			result.setContract(votes);
			
			break;
		case ContractType.TransferContract_VALUE:
			
			TransferModel transferTrx = this.dslContext.select(TRANSFER.FROM,TRANSFER.TO,TRANSFER.AMOUNT)
			.from(TRANSFER)
			.where(TRANSFER.TRANSACTION_ID.eq(ULong.valueOf(result.getId())))
			.fetchOneInto(TransferModel.class);
			
			result.setContract(transferTrx);
			
			break;
		case ContractType.TransferAssetContract_VALUE:
			
			TransferModel transferToken = this.dslContext.select(TRANSFER.FROM,TRANSFER.TO,TRANSFER.AMOUNT,TRANSFER.TOKEN)
			.from(TRANSFER)
			.where(TRANSFER.TRANSACTION_ID.eq(ULong.valueOf(result.getId())))
			.fetchOneInto(TransferModel.class);
			
			result.setContract(transferToken);
			
			break;
		default:
			break;
		}
		
		
		
		return result;
	}


	public ListModel<TransactionModel, TransactionCriteria> listTransactions(TransactionCriteria criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		//remove invalid transactions (wrong dates)
		//FIXME: remove if/when fixed in tron
		conditions.add(DSL.year(TRANSACTION.TIMESTAMP).gt(TRON_START_YEAR-1));
		conditions.add(TRANSACTION.TIMESTAMP.le(DSL.select(DSL.max(BLOCK.TIMESTAMP)).from(BLOCK)));
		conditions.add(TRANSACTION.FROM.isNotNull());

		if (criteria.getBlock()!=null) {
			conditions.add(TRANSACTION.BLOCK_ID.in(DSL.select(BLOCK.ID).from(BLOCK).where(BLOCK.NUM.eq(ULong.valueOf(criteria.getBlock())))));
		}else {
			conditions.add(DSL.year(TRANSACTION.TIMESTAMP).lt(DSL.year(DSL.currentDate()).plus(1)));	
		}

		SelectOnConditionStep<?> listQuery = this.dslContext.select(TRANSACTION.ID,TRANSACTION.HASH,TRANSACTION.TIMESTAMP,BLOCK.NUM.as("block"),TRANSACTION.FROM,TRANSACTION.TYPE,TRANSACTION.CONFIRMED)
		.from(TRANSACTION)
		.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID))
		;
		
		long totalCount = 0;
		if (criteria.getBlock()==null) {
			totalCount = this.quickStats.getTotalTx();	
		}else {
			totalCount = this.dslContext.select(DSL.count()).from(TRANSACTION).where(conditions).execute();
		}
		
		
		List<TransactionModel> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(TransactionModel.class);
		
		
		ListModel<TransactionModel, TransactionCriteria> result = new ListModel<TransactionModel, TransactionCriteria>(criteria, items, totalCount);
		
		return result;
		
	}
	
	
	

}

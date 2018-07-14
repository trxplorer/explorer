package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;

import com.google.inject.Inject;

import io.trxplorer.service.dto.common.ListDTO;
import io.trxplorer.service.dto.transaction.TransactionCriteriaDTO;
import io.trxplorer.service.dto.transaction.TransactionDTO;
import io.trxplorer.service.dto.vote.VoteModel;
import io.trxplorer.service.utils.TransactionHelper;

public class TransactionService {

	private DSLContext dslContext;
	
	private final int TRON_START_YEAR = 2018;
	
	@Inject
	public TransactionService(DSLContext dslContext) {
		this.dslContext  = dslContext;
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

		return this.dslContext.select(TRANSACTION.TIMESTAMP,CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.as("from"),CONTRACT_VOTE_WITNESS.VOTE_ADDRESS,CONTRACT_VOTE_WITNESS.VOTE_COUNT)
		.from(TRANSACTION)
		.join(CONTRACT_VOTE_WITNESS).on(CONTRACT_VOTE_WITNESS.TRANSACTION_ID.eq(TRANSACTION.ID))
		
		.orderBy(TRANSACTION.TIMESTAMP.desc())
		.limit(limit)
		.fetchInto(VoteModel.class);
		
 
	}	
	
	public List<TransactionDTO> getLatestTransactions(int limit){
		
		return this.dslContext.select(TRANSACTION.HASH,BLOCK.NUM.as("blockNum"))
		.from(TRANSACTION).join(BLOCK).on(TRANSACTION.BLOCK_ID.eq(BLOCK.ID)).and(DSL.year(TRANSACTION.TIMESTAMP).gt(TRON_START_YEAR-1)).and(DSL.year(TRANSACTION.TIMESTAMP).lt(DSL.year(DSL.currentDate()).plus(1)))
		.orderBy(TRANSACTION.TIMESTAMP.desc())
		.limit(limit)
		.fetchInto(TransactionDTO.class);
	}
	

	public TransactionDTO getTxByHash(String hash) {
		
		Field<String> fromField = DSL.when(CONTRACT_ACCOUNT_CREATE.OWNER_ADDRESS.isNotNull(), CONTRACT_ACCOUNT_CREATE.OWNER_ADDRESS)
		.when(CONTRACT_ACCOUNT_UPDATE.OWNER_ADDRESS.isNotNull(), CONTRACT_ACCOUNT_UPDATE.OWNER_ADDRESS)
		.when(CONTRACT_ASSET_ISSUE.OWNER_ADDRESS.isNotNull(), CONTRACT_ASSET_ISSUE.OWNER_ADDRESS)
		.when(CONTRACT_DEPLOY.OWNER_ADDRESS.isNotNull(), CONTRACT_DEPLOY.OWNER_ADDRESS)
		.when(CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS.isNotNull(), CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS)
		.when(CONTRACT_TRANSFER.OWNER_ADDRESS.isNotNull(), CONTRACT_TRANSFER.OWNER_ADDRESS)
		.when(CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS.isNotNull(), CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS)
		.when(CONTRACT_VOTE_ASSET.OWNER_ADDRESS.isNotNull(), CONTRACT_VOTE_ASSET.OWNER_ADDRESS)
		.when(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.isNotNull(), CONTRACT_VOTE_WITNESS.OWNER_ADDRESS)
		.when(CONTRACT_WITNESS_CREATE.OWNER_ADDRESS.isNotNull(), CONTRACT_WITNESS_CREATE.OWNER_ADDRESS)
		.when(CONTRACT_WITNESS_UPDATE.OWNER_ADDRESS.isNotNull(), CONTRACT_WITNESS_UPDATE.OWNER_ADDRESS)
		.when(CONTRACT_FREEZE_BALANCE.OWNER_ADDRESS.isNotNull(), CONTRACT_FREEZE_BALANCE.OWNER_ADDRESS)
		.when(CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS.isNotNull(), CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS)
		.when(CONTRACT_WITHDRAW_BALANCE.OWNER_ADDRESS.isNotNull(), CONTRACT_WITHDRAW_BALANCE.OWNER_ADDRESS)
		.as("from");
		
		Field<String> toField = DSL.when(CONTRACT_TRANSFER.TO_ADDRESS.isNotNull(), CONTRACT_TRANSFER.TO_ADDRESS)
		.when(CONTRACT_TRANSFER_ASSET.TO_ADDRESS.isNotNull(), CONTRACT_TRANSFER_ASSET.TO_ADDRESS)
		.as("to");		

		Field<ULong> amountField = DSL.when(CONTRACT_TRANSFER.AMOUNT.isNotNull(), CONTRACT_TRANSFER.AMOUNT)
		.when(CONTRACT_TRANSFER_ASSET.AMOUNT.isNotNull(), CONTRACT_TRANSFER_ASSET.AMOUNT)
		.as("amount");
		
		Field<String> typeField = DSL.when(CONTRACT_ACCOUNT_CREATE.OWNER_ADDRESS.isNotNull(), "ACCOUNT_CREATE")
		.when(CONTRACT_ACCOUNT_UPDATE.OWNER_ADDRESS.isNotNull(), "ACCOUNT_UPDATE")
		.when(CONTRACT_ASSET_ISSUE.OWNER_ADDRESS.isNotNull(), "ASSET_ISSUE")
		.when(CONTRACT_DEPLOY.OWNER_ADDRESS.isNotNull(), "DEPLOY")
		.when(CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS.isNotNull(), "PARTICIPATE_ASSET_ISSUE")
		.when(CONTRACT_TRANSFER.OWNER_ADDRESS.isNotNull(), "TRANSFER")
		.when(CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS.isNotNull(), "TRANSFER_ASSET")
		.when(CONTRACT_VOTE_ASSET.OWNER_ADDRESS.isNotNull(), "VOTE_ASSET")
		.when(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.isNotNull(), "VOTE_WITNESS")
		.when(CONTRACT_WITNESS_CREATE.OWNER_ADDRESS.isNotNull(), "WITNESS_CREATE")
		.when(CONTRACT_WITNESS_UPDATE.OWNER_ADDRESS.isNotNull(), "WITNESS_UPDATE")
		.when(CONTRACT_FREEZE_BALANCE.OWNER_ADDRESS.isNotNull(), "FREEZE_BALANCE")
		.when(CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS.isNotNull(), "UNFREEZE_BALANCE")
		.when(CONTRACT_WITHDRAW_BALANCE.OWNER_ADDRESS.isNotNull(), "WITHDRAW_BALANCE")
		.as("type");		
		
		Field<String> tokenField = DSL.when(CONTRACT_TRANSFER.OWNER_ADDRESS.isNotNull(), DSL.value("TRX"))
				.when(CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS.isNotNull(), CONTRACT_TRANSFER_ASSET.ASSET_NAME).as("token");
		
		TransactionDTO result = this.dslContext.select(fromField,toField,amountField,typeField,tokenField,BLOCK.NUM.as("blockNum"),TRANSACTION.TIMESTAMP,TRANSACTION.HASH,TRANSACTION.CONFIRMED)
		.from(TRANSACTION)
		.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID))
		.leftJoin(CONTRACT_ACCOUNT_CREATE).on(CONTRACT_ACCOUNT_CREATE.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_ACCOUNT_UPDATE).on(CONTRACT_ACCOUNT_UPDATE.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_ASSET_ISSUE).on(CONTRACT_ASSET_ISSUE.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_DEPLOY).on(CONTRACT_DEPLOY.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_PARTICIPATE_ASSET_ISSUE).on(CONTRACT_PARTICIPATE_ASSET_ISSUE.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_TRANSFER).on(CONTRACT_TRANSFER.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_TRANSFER_ASSET).on(CONTRACT_TRANSFER_ASSET.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_VOTE_ASSET).on(CONTRACT_VOTE_ASSET.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_VOTE_WITNESS).on(CONTRACT_VOTE_WITNESS.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_WITNESS_CREATE).on(CONTRACT_WITNESS_CREATE.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_WITNESS_UPDATE).on(CONTRACT_WITNESS_UPDATE.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_FREEZE_BALANCE).on(CONTRACT_FREEZE_BALANCE.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_UNFREEZE_BALANCE).on(CONTRACT_UNFREEZE_BALANCE.TRANSACTION_ID.eq(TRANSACTION.ID))
		.leftJoin(CONTRACT_WITHDRAW_BALANCE).on(CONTRACT_WITHDRAW_BALANCE.TRANSACTION_ID.eq(TRANSACTION.ID))
		.where(TRANSACTION.HASH.eq(hash))
		.fetchOneInto(TransactionDTO.class);
		
		if (result!=null) {
			prepareTransactionDTO(result);			
		}

		
		return result;
	}


	public ListDTO<TransactionDTO, TransactionCriteriaDTO> listTransactions(TransactionCriteriaDTO criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		//remove invalid transactions (wrong dates)
		//FIXME: remove as fixed in tron
		if (criteria.getBlock()==null || !criteria.getBlock().equals("0") ) {
			conditions.add(DSL.year(TRANSACTION.TIMESTAMP).gt(TRON_START_YEAR-1));
			conditions.add(DSL.year(TRANSACTION.TIMESTAMP).lt(DSL.year(DSL.currentDate()).plus(1)));			
		}

		
		Field<String> fromField = DSL.when(CONTRACT_ACCOUNT_CREATE.OWNER_ADDRESS.isNotNull(), CONTRACT_ACCOUNT_CREATE.OWNER_ADDRESS)
		.when(CONTRACT_ACCOUNT_UPDATE.OWNER_ADDRESS.isNotNull(), CONTRACT_ACCOUNT_UPDATE.OWNER_ADDRESS)
		.when(CONTRACT_ASSET_ISSUE.OWNER_ADDRESS.isNotNull(), CONTRACT_ASSET_ISSUE.OWNER_ADDRESS)
		.when(CONTRACT_DEPLOY.OWNER_ADDRESS.isNotNull(), CONTRACT_DEPLOY.OWNER_ADDRESS)
		.when(CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS.isNotNull(), CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS)
		.when(CONTRACT_TRANSFER.OWNER_ADDRESS.isNotNull(), CONTRACT_TRANSFER.OWNER_ADDRESS)
		.when(CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS.isNotNull(), CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS)
		.when(CONTRACT_VOTE_ASSET.OWNER_ADDRESS.isNotNull(), CONTRACT_VOTE_ASSET.OWNER_ADDRESS)
		.when(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.isNotNull(), CONTRACT_VOTE_WITNESS.OWNER_ADDRESS)
		.when(CONTRACT_WITNESS_CREATE.OWNER_ADDRESS.isNotNull(), CONTRACT_WITNESS_CREATE.OWNER_ADDRESS)
		.when(CONTRACT_WITNESS_UPDATE.OWNER_ADDRESS.isNotNull(), CONTRACT_WITNESS_UPDATE.OWNER_ADDRESS)
		.when(CONTRACT_FREEZE_BALANCE.OWNER_ADDRESS.isNotNull(), CONTRACT_FREEZE_BALANCE.OWNER_ADDRESS)
		.when(CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS.isNotNull(), CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS)
		.when(CONTRACT_WITHDRAW_BALANCE.OWNER_ADDRESS.isNotNull(), CONTRACT_WITHDRAW_BALANCE.OWNER_ADDRESS)
		.as("from");
		
		Field<String> toField = DSL.when(CONTRACT_TRANSFER.TO_ADDRESS.isNotNull(), CONTRACT_TRANSFER.TO_ADDRESS)
		.when(CONTRACT_TRANSFER_ASSET.TO_ADDRESS.isNotNull(), CONTRACT_TRANSFER_ASSET.TO_ADDRESS)
		.as("to");		

		Field<ULong> amountField = DSL.when(CONTRACT_TRANSFER.AMOUNT.isNotNull(), CONTRACT_TRANSFER.AMOUNT)
		.when(CONTRACT_TRANSFER_ASSET.AMOUNT.isNotNull(), CONTRACT_TRANSFER_ASSET.AMOUNT)
		.as("amount");
		
		Field<String> typeField = DSL.when(CONTRACT_ACCOUNT_CREATE.OWNER_ADDRESS.isNotNull(), "ACCOUNT_CREATE")
		.when(CONTRACT_ACCOUNT_UPDATE.OWNER_ADDRESS.isNotNull(), "ACCOUNT_UPDATE")
		.when(CONTRACT_ASSET_ISSUE.OWNER_ADDRESS.isNotNull(), "ASSET_ISSUE")
		.when(CONTRACT_DEPLOY.OWNER_ADDRESS.isNotNull(), "DEPLOY")
		.when(CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS.isNotNull(), "PARTICIPATE_ASSET_ISSUE")
		.when(CONTRACT_TRANSFER.OWNER_ADDRESS.isNotNull(), "TRANSFER")
		.when(CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS.isNotNull(), "TRANSFER_ASSET")
		.when(CONTRACT_VOTE_ASSET.OWNER_ADDRESS.isNotNull(), "VOTE_ASSET")
		.when(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.isNotNull(), "VOTE_WITNESS")
		.when(CONTRACT_WITNESS_CREATE.OWNER_ADDRESS.isNotNull(), "WITNESS_CREATE")
		.when(CONTRACT_WITNESS_UPDATE.OWNER_ADDRESS.isNotNull(), "WITNESS_UPDATE")
		.when(CONTRACT_FREEZE_BALANCE.OWNER_ADDRESS.isNotNull(), "FREEZE_BALANCE")
		.when(CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS.isNotNull(), "UNFREEZE_BALANCE")
		.when(CONTRACT_WITHDRAW_BALANCE.OWNER_ADDRESS.isNotNull(), "WITHDRAW_BALANCE")
		.as("type");		

		Field<String> tokenField = DSL.when(CONTRACT_TRANSFER.OWNER_ADDRESS.isNotNull(), DSL.value("TRX"))
				.when(CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS.isNotNull(), CONTRACT_TRANSFER_ASSET.ASSET_NAME).as("token");
		
		
		
		
		
		if (StringUtils.isNotBlank(criteria.getBlock())) {
			conditions.add(TRANSACTION.BLOCK_ID.in(DSL.select(BLOCK.ID).from(BLOCK).where(BLOCK.NUM.eq(ULong.valueOf(criteria.getBlock())))));
		}
		
		if (StringUtils.isNotBlank(criteria.getAddress())) {
			
			conditions.add(TRANSACTION.ID.in(
					DSL.select(CONTRACT_TRANSFER.TRANSACTION_ID)
					.from(CONTRACT_TRANSFER)
					.where(CONTRACT_TRANSFER.OWNER_ADDRESS.eq(criteria.getAddress())
							.or(CONTRACT_TRANSFER.TO_ADDRESS.eq(criteria.getAddress()))))
					.or(TRANSACTION.ID.in(DSL.select(CONTRACT_TRANSFER_ASSET.TRANSACTION_ID)
					.from(CONTRACT_TRANSFER_ASSET)
					.where(CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS.eq(criteria.getAddress())
							.or(CONTRACT_TRANSFER_ASSET.TO_ADDRESS.eq(criteria.getAddress()))))))
					
					;

		}
		
		// We only want asset transfer
		if (StringUtils.isNotBlank(criteria.getAssetName())) {
			
			conditions.add(TRANSACTION.ID.in(DSL.select(CONTRACT_TRANSFER_ASSET.TRANSACTION_ID).from(CONTRACT_TRANSFER_ASSET).where(CONTRACT_TRANSFER_ASSET.ASSET_NAME.eq(criteria.getAssetName()))));
		}
	

		Table<?> tmpTable = DSL.select(TRANSACTION.ID,TRANSACTION.HASH,TRANSACTION.TIMESTAMP,TRANSACTION.BLOCK_ID,TRANSACTION.CONFIRMED).from(TRANSACTION).where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).asTable("tmp");
		


		SelectOnConditionStep<?> listQuery = this.dslContext.select(fromField,toField,amountField,typeField,BLOCK.NUM.as("blockNum"),tokenField,tmpTable.field(TRANSACTION.TIMESTAMP.getName()),tmpTable.field(TRANSACTION.HASH.getName()),tmpTable.field(TRANSACTION.CONFIRMED.getName()))
		.from(tmpTable)
		.join(BLOCK).on(BLOCK.ID.eq(tmpTable.field(TRANSACTION.BLOCK_ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_ACCOUNT_CREATE).on(CONTRACT_ACCOUNT_CREATE.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_ACCOUNT_UPDATE).on(CONTRACT_ACCOUNT_UPDATE.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_ASSET_ISSUE).on(CONTRACT_ASSET_ISSUE.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_DEPLOY).on(CONTRACT_DEPLOY.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_PARTICIPATE_ASSET_ISSUE).on(CONTRACT_PARTICIPATE_ASSET_ISSUE.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_TRANSFER).on(CONTRACT_TRANSFER.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_TRANSFER_ASSET).on(CONTRACT_TRANSFER_ASSET.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_VOTE_ASSET).on(CONTRACT_VOTE_ASSET.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_VOTE_WITNESS).on(CONTRACT_VOTE_WITNESS.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_WITNESS_CREATE).on(CONTRACT_WITNESS_CREATE.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_WITNESS_UPDATE).on(CONTRACT_WITNESS_UPDATE.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_FREEZE_BALANCE).on(CONTRACT_FREEZE_BALANCE.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_UNFREEZE_BALANCE).on(CONTRACT_UNFREEZE_BALANCE.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		.leftJoin(CONTRACT_WITHDRAW_BALANCE).on(CONTRACT_WITHDRAW_BALANCE.TRANSACTION_ID.eq(tmpTable.field(TRANSACTION.ID.getName(),ULong.class)))
		;

		
		
		SelectConditionStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
				.from(TRANSACTION)
				.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID))
				.where(conditions)
				;
		
		
		Integer totalCount = countQuery.fetchOneInto(Integer.class);
		
		List<TransactionDTO> items = listQuery.fetchInto(TransactionDTO.class);
		
		prepareTransactionDTO(items);
		
		ListDTO<TransactionDTO, TransactionCriteriaDTO> result = new ListDTO<TransactionDTO, TransactionCriteriaDTO>(criteria, items, totalCount);
		
		return result;
	}
	
	private void prepareTransactionDTO(List<TransactionDTO> transactions) {
		
		for(TransactionDTO tx:transactions) {
			
			prepareTransactionDTO(tx);
			
		}
		
	}
	
	private void prepareTransactionDTO(TransactionDTO tx) {
		
		if (tx.getToken()!=null && tx.getToken().equals("TRX")) {
			tx.setAmount(TransactionHelper.getTrxAmount(tx.getAmount()));			
		}

		tx.setShortHash(StringUtils.abbreviate(tx.getHash(), 10));
	}
	

}

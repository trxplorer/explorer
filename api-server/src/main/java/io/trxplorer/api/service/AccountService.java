package io.trxplorer.api.service;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.jooq.util.mysql.MySQLDataType;
import org.tron.protos.Protocol.Account;

import com.google.inject.Inject;

import io.trxplorer.api.dto.account.AccountCriteria;
import io.trxplorer.api.dto.account.AccountExistsCriteria;
import io.trxplorer.api.dto.account.AccountInfo;
import io.trxplorer.api.dto.account.AssetBalanceModel;
import io.trxplorer.api.dto.account.FrozenBalanceModel;
import io.trxplorer.api.dto.common.ListResult;
import io.trxplorer.api.dto.witness.AllowanceWidthdrawModel;
import io.trxplorer.service.dto.transaction.TransferModel;
import io.trxplorer.service.dto.vote.VoteModel;
import io.trxplorer.troncli.TronFullNodeCli;

public class AccountService {

	private DSLContext dslContext;
	
	private TronFullNodeCli tronFullNodeCli;

	private WitnessService witnessService;

	
	@Inject
	public AccountService(DSLContext dslContext,TronFullNodeCli tronFullNodeCli,WitnessService witnessService) {
		this.dslContext = dslContext;
		this.tronFullNodeCli = tronFullNodeCli;
		this.witnessService = witnessService;
	}
	
	public boolean accountExists(AccountExistsCriteria criteria) {
		
		List<Condition> conditions = new ArrayList<>();
		
		if (StringUtils.isNotBlank(criteria.getAddress())) {
			conditions.add(ACCOUNT.ADDRESS.eq(criteria.getAddress()));
		}
		
		if (StringUtils.isNotBlank(criteria.getName())) {
			conditions.add(ACCOUNT.ACCOUNT_NAME.eq(criteria.getName()));
		}
		
		if (conditions.size()>0) {
			Integer id = this.dslContext.select(ACCOUNT.ID).from(ACCOUNT).where(conditions).fetchOneInto(Integer.class);
			if (id!=null) {
				return true;
			}
		}

		
		
		return false;
	}
	

	public AccountInfo getAccountByAddress(String address) {
		
		// for some unknow reason this field has to be converted into varchar in order to return correct value 
		Field<?> percentageField = ACCOUNT.BALANCE.divide(DSL.field(DSL.select(DSL.sum(ACCOUNT.BALANCE)).from(ACCOUNT))).cast(MySQLDataType.VARCHAR).as("percentage");
		
		Field<?> rankField = DSL.field("@rank := @rank + 1").cast(Long.class).as("rank");
		
		
		Table<Record> dummyTable = DSL.select().from("select @rank := 0").asTable("dummy");
		;
		
		Field<?> frozenBalanceField = DSL.select(ACCOUNT_FROZEN.BALANCE).from(ACCOUNT_FROZEN).where(ACCOUNT_FROZEN.ACCOUNT_ID.eq(ACCOUNT.ID)).orderBy(ACCOUNT_FROZEN.EXPIRE_TIME.desc()).limit(1).asField("frozenBalance");
		Field<?> frozenExpireField = DSL.select(DSL.field("UNIX_TIMESTAMP({0})",Long.class,ACCOUNT_FROZEN.EXPIRE_TIME)).from(ACCOUNT_FROZEN).where(ACCOUNT_FROZEN.ACCOUNT_ID.eq(ACCOUNT.ID)).orderBy(ACCOUNT_FROZEN.EXPIRE_TIME.desc()).limit(1).asField("frozenExpire");
		
		
		Table<?> mainTable = DSL.select(ACCOUNT.ACCOUNT_NAME.as("name"),ACCOUNT.TYPE,ACCOUNT.IS_WITNESS,ACCOUNT.CREATE_TIME,ACCOUNT.ADDRESS,ACCOUNT.BALANCE,ACCOUNT.ALLOWANCE,ACCOUNT.BANDWIDTH,rankField,percentageField,frozenBalanceField,frozenExpireField)
				.from(ACCOUNT).crossJoin("(SELECT @rank := 0) as t").orderBy(ACCOUNT.BALANCE.desc()).asTable("accountRank");
		
		AccountInfo result = this.dslContext.select(mainTable.fields()).from(mainTable).where(mainTable.field(ACCOUNT.ADDRESS.getName(),String.class).eq(address)).fetchOneInto(AccountInfo.class);
		
		if (result==null) {
			//No null result => account may just have not been created yet
			return new AccountInfo();
		}
		
		try {
			Account tronAccount = this.tronFullNodeCli.getAccountByAddress(address);
			result.setBandwidth(tronAccount.getNetUsage());
		}catch(Throwable e) {
			//e.printStackTrace();
		}
		
		if (result.isWitness()) {
			result.setWitness(this.witnessService.getWitnessByAddress(address));
		}
		

		prepareAccountDTO(result);
		
		return result;
	}

	private void prepareAccountDTO(AccountInfo accountDTO) {
		
		accountDTO.setTotalBalance(accountDTO.getBalance()+accountDTO.getFrozenBalance());
		
	}



	public ListResult<AssetBalanceModel, AccountCriteria> listAssetBalances(AccountCriteria criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(ACCOUNT.ADDRESS.eq(criteria.getAddress()));
		
		SelectOnConditionStep<?> listQuery = this.dslContext.select(ACCOUNT_ASSET.BALANCE,ACCOUNT_ASSET.ASSET_NAME).from(ACCOUNT_ASSET)
				.join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ASSET.ACCOUNT_ID));
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
				.from(ACCOUNT_ASSET).join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ASSET.ACCOUNT_ID));
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		SelectSeekStep1<?, String> q2 = listQuery.where(conditions).orderBy(ACCOUNT_ASSET.ASSET_NAME.asc());
		
		List<AssetBalanceModel> items = new ArrayList<>();
		
		if (criteria.getLimit()>0) {
			items = q2.limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(AssetBalanceModel.class);	
		}else {
			items = q2.fetchInto(AssetBalanceModel.class);
		}
		
		
		
		
		
		ListResult<AssetBalanceModel, AccountCriteria> result = new ListResult<AssetBalanceModel, AccountCriteria>(criteria, items, totalCount);
		
		return result;
		
	}

	
	public ListResult<VoteModel, AccountCriteria> listVotes(AccountCriteria criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(VOTING_ROUND_VOTE.OWNER_ADDRESS.eq(criteria.getAddress()));
		conditions.add(VOTING_ROUND.ID.eq(VOTING_ROUND_VOTE.VOTING_ROUND_ID));
		conditions.add(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(VOTING_ROUND.ID));
		conditions.add(VOTING_ROUND_STATS.ADDRESS.eq(VOTING_ROUND_VOTE.VOTE_ADDRESS));
		
		SelectJoinStep<?> listQuery = this.dslContext.select(VOTING_ROUND_VOTE.OWNER_ADDRESS.as("from"),VOTING_ROUND_VOTE.VOTE_ADDRESS.as("to"),VOTING_ROUND.ROUND,VOTING_ROUND_VOTE.VOTE_COUNT.as("votes"),VOTING_ROUND_STATS.VOTE_COUNT.as("totalRoundVotes"),VOTING_ROUND_VOTE.TIMESTAMP)
				.from(VOTING_ROUND,VOTING_ROUND_VOTE,VOTING_ROUND_STATS);
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(VOTING_ROUND,VOTING_ROUND_VOTE,VOTING_ROUND_STATS);
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VoteModel> items = listQuery.where(conditions).orderBy(VOTING_ROUND.ROUND.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VoteModel.class);
		
		
		
		ListResult<VoteModel, AccountCriteria> result = new ListResult<VoteModel, AccountCriteria>(criteria, items, totalCount);
		
		return result;
	}
	
	public ListResult<VoteModel, AccountCriteria> listLiveVotes(AccountCriteria criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(ACCOUNT.ADDRESS.eq(criteria.getAddress()));
		conditions.add(ACCOUNT_VOTE.ACCOUNT_ID.eq(ACCOUNT.ID));
	
		
		SelectJoinStep<?> listQuery = this.dslContext.select(ACCOUNT.ADDRESS.as("from"),ACCOUNT_VOTE.VOTE_ADDRESS.as("to"),ACCOUNT_VOTE.VOTE_COUNT.as("votes"))
				.from(ACCOUNT_VOTE,ACCOUNT);
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(ACCOUNT_VOTE,ACCOUNT);
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VoteModel> items = listQuery.where(conditions).orderBy(ACCOUNT_VOTE.VOTE_COUNT.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VoteModel.class);
		
		
		
		ListResult<VoteModel, AccountCriteria> result = new ListResult<VoteModel, AccountCriteria>(criteria, items, totalCount);
		
		return result;
	}
	
	public ListResult<AllowanceWidthdrawModel, AccountCriteria> listAllowanceWithdrawals(AccountCriteria criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(CONTRACT_WITHDRAW_BALANCE.OWNER_ADDRESS.eq(criteria.getAddress()));
		
		SelectOnConditionStep<?> listQuery = this.dslContext.select(TRANSACTION.HASH.as("tx"),TRANSACTION.TIMESTAMP).from(CONTRACT_WITHDRAW_BALANCE)
				.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_WITHDRAW_BALANCE.TRANSACTION_ID));
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(CONTRACT_WITHDRAW_BALANCE);
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<AllowanceWidthdrawModel> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(AllowanceWidthdrawModel.class);
		
		
		
		ListResult<AllowanceWidthdrawModel, AccountCriteria> result = new ListResult<AllowanceWidthdrawModel, AccountCriteria>(criteria, items, totalCount);
		
		return result;
		
		
		
	}
	
	
	public ListResult<FrozenBalanceModel, AccountCriteria> listFrozenBalance(AccountCriteria criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		

		
		Table<?> tmpTable = DSL.select(ACCOUNT_FROZEN.BALANCE,ACCOUNT_FROZEN.EXPIRE_TIME)
		.from(ACCOUNT_FROZEN)
		.join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_FROZEN.ACCOUNT_ID).and(ACCOUNT.ADDRESS.eq(criteria.getAddress())))
		.union(DSL.select(DSL.val(0l),TRANSACTION.TIMESTAMP).from(CONTRACT_UNFREEZE_BALANCE)
				.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_UNFREEZE_BALANCE.TRANSACTION_ID))
				.where(CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS.eq(criteria.getAddress()))
				).asTable("tmp");
		
		 SelectJoinStep<?> listQuery = this.dslContext.select(tmpTable.field(ACCOUNT_FROZEN.BALANCE.getName()),tmpTable.field(ACCOUNT_FROZEN.EXPIRE_TIME.getName()))
				.from(tmpTable);
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
				.from(tmpTable);
		
		
		Integer totalCount = countQuery.fetchOneInto(Integer.class);
		
		List<FrozenBalanceModel> items = listQuery.orderBy(tmpTable.field(ACCOUNT_FROZEN.EXPIRE_TIME.getName()).desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(FrozenBalanceModel.class);
		

		
		ListResult<FrozenBalanceModel, AccountCriteria> result = new ListResult<FrozenBalanceModel, AccountCriteria>(criteria, items, totalCount);
		
		return result;
		
	}

	

	public ListResult<TransferModel, AccountCriteria> listTransfers(AccountCriteria criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		
		conditions.add(TRANSFER.FROM.eq(criteria.getAddress()).or(TRANSFER.TO.eq(criteria.getAddress())));
		conditions.add(TRANSFER.TRANSACTION_ID.eq(TRANSACTION.ID));
		
		List<Field<?>> fields = new ArrayList<>(Arrays.asList(TRANSFER.fields()));
		fields.add(TRANSACTION.HASH);
				
		SelectJoinStep<?> listQuery = this.dslContext.select(fields)
					.from(TRANSFER,TRANSACTION);


		
		Integer totalCount = this.dslContext.select(DSL.count())
				.from(TRANSFER,TRANSACTION)
				.where(conditions)
				.fetchOneInto(Integer.class);
		
		List<TransferModel> items = listQuery.where(conditions).orderBy(TRANSFER.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(TransferModel.class);
		
		
		ListResult<TransferModel, AccountCriteria> result = new ListResult<TransferModel, AccountCriteria>(criteria, items, totalCount);
		
		return result;
	}
	
	public ListResult<TransferModel, AccountCriteria> listTokenParticipations(AccountCriteria criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		
		conditions.add(CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS.eq(criteria.getAddress()));
		conditions.add(CONTRACT_PARTICIPATE_ASSET_ISSUE.TRANSACTION_ID.eq(TRANSACTION.ID));

				
		SelectJoinStep<?> listQuery = this.dslContext.select(
				CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS.as("from"),
				CONTRACT_PARTICIPATE_ASSET_ISSUE.TO_ADDRESS.as("to"),
				CONTRACT_PARTICIPATE_ASSET_ISSUE.AMOUNT,
				CONTRACT_PARTICIPATE_ASSET_ISSUE.ASSET_NAME.as("token"),
				TRANSACTION.TIMESTAMP
				)
					.from(CONTRACT_PARTICIPATE_ASSET_ISSUE,TRANSACTION);


		
		Integer totalCount = this.dslContext.select(DSL.count())
				.from(CONTRACT_PARTICIPATE_ASSET_ISSUE,TRANSACTION)
				.where(conditions)
				.fetchOneInto(Integer.class);
		
		List<TransferModel> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(TransferModel.class);
		
		
		ListResult<TransferModel, AccountCriteria> result = new ListResult<TransferModel, AccountCriteria>(criteria, items, totalCount);
		
		return result;
	}


	
}

package io.trxplorer.api.service;

import static io.trxplorer.model.Tables.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.jooq.util.mysql.MySQLDSL;
import org.jooq.util.mysql.MySQLDataType;
import org.tron.protos.Protocol.Account;

import com.google.inject.Inject;

import io.trxplorer.api.dto.account.AccountCriteria;
import io.trxplorer.api.dto.account.AccountExistsCriteria;
import io.trxplorer.api.dto.account.AccountInfo;
import io.trxplorer.api.dto.account.AssetBalanceDTO;
import io.trxplorer.api.dto.account.FrozenBalanceDTO;
import io.trxplorer.api.dto.account.VoteDTO;
import io.trxplorer.api.dto.common.ListResult;
import io.trxplorer.api.dto.transaction.TransactionCriteria;
import io.trxplorer.api.dto.transaction.TransactionDTO;
import io.trxplorer.api.dto.witness.AllowanceWidthdrawDTO;
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



	public ListResult<AssetBalanceDTO, AccountCriteria> listAssetBalances(AccountCriteria criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(ACCOUNT.ADDRESS.eq(criteria.getAddress()));
		
		SelectOnConditionStep<?> listQuery = this.dslContext.select(ACCOUNT_ASSET.BALANCE,ACCOUNT_ASSET.ASSET_NAME).from(ACCOUNT_ASSET)
				.join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ASSET.ACCOUNT_ID));
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
				.from(ACCOUNT_ASSET).join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ASSET.ACCOUNT_ID));
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		SelectSeekStep1<?, String> q2 = listQuery.where(conditions).orderBy(ACCOUNT_ASSET.ASSET_NAME.asc());
		
		List<AssetBalanceDTO> items = new ArrayList<>();
		
		if (criteria.getLimit()>0) {
			items = q2.limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(AssetBalanceDTO.class);	
		}else {
			items = q2.fetchInto(AssetBalanceDTO.class);
		}
		
		
		
		
		
		ListResult<AssetBalanceDTO, AccountCriteria> result = new ListResult<AssetBalanceDTO, AccountCriteria>(criteria, items, totalCount);
		
		return result;
		
	}

	
	public ListResult<VoteDTO, AccountCriteria> listVotes(AccountCriteria criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.eq(criteria.getAddress()).or(CONTRACT_VOTE_WITNESS.VOTE_ADDRESS.eq(criteria.getAddress())));
		
		SelectOnConditionStep<?> listQuery = this.dslContext.select(TRANSACTION.TIMESTAMP,CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.as("from"),CONTRACT_VOTE_WITNESS.VOTE_ADDRESS.as("to"),CONTRACT_VOTE_WITNESS.VOTE_COUNT).from(CONTRACT_VOTE_WITNESS)
				.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_VOTE_WITNESS.TRANSACTION_ID));
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(CONTRACT_VOTE_WITNESS);
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VoteDTO> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VoteDTO.class);
		
		
		
		ListResult<VoteDTO, AccountCriteria> result = new ListResult<VoteDTO, AccountCriteria>(criteria, items, totalCount);
		
		return result;
		
		
		
	}
	
	public ListResult<AllowanceWidthdrawDTO, AccountCriteria> listAllowanceWithdrawals(AccountCriteria criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(CONTRACT_WITHDRAW_BALANCE.OWNER_ADDRESS.eq(criteria.getAddress()));
		
		SelectOnConditionStep<?> listQuery = this.dslContext.select(TRANSACTION.ID.as("txId"),TRANSACTION.TIMESTAMP).from(CONTRACT_WITHDRAW_BALANCE)
				.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_WITHDRAW_BALANCE.TRANSACTION_ID));
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(CONTRACT_WITHDRAW_BALANCE);
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<AllowanceWidthdrawDTO> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(AllowanceWidthdrawDTO.class);
		
		
		
		ListResult<AllowanceWidthdrawDTO, AccountCriteria> result = new ListResult<AllowanceWidthdrawDTO, AccountCriteria>(criteria, items, totalCount);
		
		return result;
		
		
		
	}
	
	
	public ListResult<FrozenBalanceDTO, AccountCriteria> listFrozenBalance(AccountCriteria criteria){
		
		
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
		
		List<FrozenBalanceDTO> items = listQuery.orderBy(tmpTable.field(ACCOUNT_FROZEN.EXPIRE_TIME.getName()).desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(FrozenBalanceDTO.class);
		

		
		ListResult<FrozenBalanceDTO, AccountCriteria> result = new ListResult<FrozenBalanceDTO, AccountCriteria>(criteria, items, totalCount);
		
		return result;
		
	}

	

	public ListResult<TransactionDTO, AccountCriteria> listTransactions(AccountCriteria criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		

		SelectConditionStep<Record5<String, String, ULong, String, String>> CONTRACT_TRANSFER_SELECT = DSL.select(CONTRACT_TRANSFER.OWNER_ADDRESS.as("from"),CONTRACT_TRANSFER.TO_ADDRESS.as("to"),CONTRACT_TRANSFER.AMOUNT.as("amount"),DSL.val("TRX").as("token"),TRANSACTION.HASH).from(CONTRACT_TRANSFER).join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_TRANSFER.TRANSACTION_ID)).where(CONTRACT_TRANSFER.OWNER_ADDRESS.eq(criteria.getAddress()).or(CONTRACT_TRANSFER.TO_ADDRESS.eq(criteria.getAddress())));
		SelectConditionStep<Record5<String, String, ULong, String, String>> CONTRACT_TRANSFER_ASSET_SELECT = DSL.select(CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS.as("from"),CONTRACT_TRANSFER_ASSET.TO_ADDRESS.as("to"),CONTRACT_TRANSFER_ASSET.AMOUNT.as("amount"),CONTRACT_TRANSFER_ASSET.ASSET_NAME.as("token"),TRANSACTION.HASH).from(CONTRACT_TRANSFER_ASSET).join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_TRANSFER_ASSET.TRANSACTION_ID)).where(CONTRACT_TRANSFER_ASSET.OWNER_ADDRESS.eq(criteria.getAddress()).or(CONTRACT_TRANSFER_ASSET.TO_ADDRESS.eq(criteria.getAddress())));

		Table<Record5<String, String, ULong, String, String>> tmpTable = CONTRACT_TRANSFER_SELECT
		.union(CONTRACT_TRANSFER_ASSET_SELECT)
		.asTable();


		
		Integer totalCount = this.dslContext.select(DSL.count()).from(tmpTable).fetchOneInto(Integer.class);
		
		List<TransactionDTO> items = this.dslContext.select().from(tmpTable).fetchInto(TransactionDTO.class);
		
		
		ListResult<TransactionDTO, AccountCriteria> result = new ListResult<TransactionDTO, AccountCriteria>(criteria, items, totalCount);
		
		return result;
	}


	
}

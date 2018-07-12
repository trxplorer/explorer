package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.google.inject.Inject;

import io.trxplorer.service.dto.account.AccountDTO;
import io.trxplorer.service.dto.account.AccountDetailCriteriaDTO;
import io.trxplorer.service.dto.account.AccountListCriteria;
import io.trxplorer.service.dto.account.AssetBalanceDTO;
import io.trxplorer.service.dto.account.FrozenBalanceDTO;
import io.trxplorer.service.dto.account.VoteDTO;
import io.trxplorer.service.dto.asset.AssetIssueListCriteriaDTO;
import io.trxplorer.service.dto.asset.AssetParticipationCriteriaDTO;
import io.trxplorer.service.dto.common.ListDTO;
import io.trxplorer.service.dto.transaction.TransactionCriteriaDTO;
import io.trxplorer.service.dto.transaction.TransactionDTO;
import io.trxplorer.service.dto.witness.AllowanceWidthdrawDTO;
import io.trxplorer.troncli.TronFullNodeCli;


public class AccountService {

	private DSLContext dslContext;
	
	private TronFullNodeCli tronFullNodeCli;

	private TransactionService txService;

	private WitnessService witnessService;

	private AssetService assetService;
	
	@Inject
	public AccountService(DSLContext dslContext,TronFullNodeCli tronFullNodeCli,TransactionService txService,WitnessService witnessService,AssetService assetService) {
		this.dslContext = dslContext;
		this.tronFullNodeCli = tronFullNodeCli;
		this.txService = txService;
		this.witnessService = witnessService;
		this.assetService = assetService;
	}


	public int getTotalAccount() {
		return this.dslContext.select(DSL.count()).from(ACCOUNT).fetchOneInto(Integer.class);
	}
	
	public List<AccountDTO> getLatestAccounts(int limit){
		
		return this.dslContext.select(ACCOUNT.fields()).from(ACCOUNT).orderBy(ACCOUNT.CREATE_TIME.desc()).limit(limit).fetchInto(AccountDTO.class);
		
	}
	
	public AccountDTO getAccountByAddress(AccountDetailCriteriaDTO accountCriteria) {
				
		
		Field<?> frozenBalanceField = DSL.select(ACCOUNT_FROZEN.BALANCE).from(ACCOUNT_FROZEN).where(ACCOUNT_FROZEN.ACCOUNT_ID.eq(ACCOUNT.ID).and(ACCOUNT_FROZEN.EXPIRE_TIME.gt(DSL.currentTimestamp()))).orderBy(ACCOUNT_FROZEN.EXPIRE_TIME.desc()).limit(1).asField("frozenBalance");
		Field<?> frozenExpireField = DSL.select(ACCOUNT_FROZEN.EXPIRE_TIME).from(ACCOUNT_FROZEN).where(ACCOUNT_FROZEN.ACCOUNT_ID.eq(ACCOUNT.ID).and(ACCOUNT_FROZEN.EXPIRE_TIME.gt(DSL.currentTimestamp()))).orderBy(ACCOUNT_FROZEN.EXPIRE_TIME.desc()).limit(1).asField("frozenExpire");
		
		
		
		AccountDTO result = this.dslContext.select(ACCOUNT.ACCOUNT_NAME.as("name"),ACCOUNT.TYPE,ACCOUNT.IS_WITNESS,ACCOUNT.CREATE_TIME,ACCOUNT.ADDRESS,ACCOUNT.BALANCE,ACCOUNT.ALLOWANCE,ACCOUNT.BANDWIDTH,frozenBalanceField,frozenExpireField)
				.from(ACCOUNT).where(ACCOUNT.ADDRESS.eq(accountCriteria.getAddress())).fetchOneInto(AccountDTO.class);
		
		if (result==null) {
			return null;
		}
		
		//check if account is a witness
		if (result.isWitness()) {
			result.setWitness(this.witnessService.getWitnessByAddress(result.getAddress()));			
		}

		
		//TODO: handle fallback on blockchain api
		
		//load transactions if request
		
		if (accountCriteria.getTab().equals("tx")) {
			
			TransactionCriteriaDTO txCriteria = new TransactionCriteriaDTO();
			txCriteria.setAddress(accountCriteria.getAddress());
			txCriteria.setLimit(accountCriteria.getLimit());
			txCriteria.setPage(accountCriteria.getPage());
			
			ListDTO<TransactionDTO, TransactionCriteriaDTO> accountTransactions = this.txService.listTransactions(txCriteria);
			
			
			
			result.setTransactions(accountTransactions.getItems());		
			
		}else if (accountCriteria.getTab().equals("votes")) {
			
			ListDTO<VoteDTO, AccountDetailCriteriaDTO> accountVotes = this.listVotes(accountCriteria);
			

			result.setVotes(accountVotes.getItems());
			
		}else if(accountCriteria.getTab().equals("ti")) {
			
			AssetIssueListCriteriaDTO assetCriteriaDTO = new AssetIssueListCriteriaDTO();
			
			assetCriteriaDTO.setIssuer(accountCriteria.getAddress());
			assetCriteriaDTO.setLimit(accountCriteria.getLimit());
			assetCriteriaDTO.setPage(accountCriteria.getPage());			
			
			result.setAssetIssues(this.assetService.listAssetIssues(assetCriteriaDTO).getItems());
			
		}else if (accountCriteria.getTab().equals("tp")) {
			
			AssetParticipationCriteriaDTO assetParticipationCriteriaDTO = new AssetParticipationCriteriaDTO();
			
			assetParticipationCriteriaDTO.setAddress(accountCriteria.getAddress());
			assetParticipationCriteriaDTO.setLimit(accountCriteria.getLimit());
			assetParticipationCriteriaDTO.setPage(accountCriteria.getPage());
			
			result.setAssetParticipations(this.assetService.listAssetParticipations(assetParticipationCriteriaDTO).getItems());
		}else if (accountCriteria.getTab().equals("fh")) {
			
			ListDTO<FrozenBalanceDTO, AccountDetailCriteriaDTO> frozenBalances = this.listFrozenBalance(accountCriteria);
			
			result.setFrozenBalances(frozenBalances.getItems());
		}else if (accountCriteria.getTab().equals("ab")) {
			
			ListDTO<AssetBalanceDTO, AccountDetailCriteriaDTO> assetBalances = this.listAssetBalances(accountCriteria);
			
			result.setAssetBalances(assetBalances.getItems());
		}else if (accountCriteria.getTab().equals("aw")) {
			
			ListDTO<AllowanceWidthdrawDTO, AccountDetailCriteriaDTO> allowanceWithdrawals = this.listAllowanceWithdrawals(accountCriteria);
			
			result.setAllowanceWithdrawals(allowanceWithdrawals.getItems());
			
		}

		result.setCriteria(accountCriteria);
		//load votes if requested
		
		prepareAccountDTO(result);
		
		return result;
	}

	private void prepareAccountDTO(AccountDTO accountDTO) {
		
		accountDTO.setTotalBalance(accountDTO.getBalance()+accountDTO.getFrozenBalance());
		
	}

	public ListDTO<AccountDTO, AccountListCriteria> listAccounts(AccountListCriteria criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		
		SelectJoinStep<?> listQuery = this.dslContext.select(ACCOUNT.ADDRESS,ACCOUNT.ACCOUNT_NAME.as("name"),ACCOUNT.BALANCE,ACCOUNT.CREATE_TIME).from(ACCOUNT);
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(ACCOUNT);
		
		
		Integer totalCount = countQuery.where(ACCOUNT.BALANCE.gt(0l)).fetchOneInto(Integer.class);
		
		List<AccountDTO> items = listQuery.where(conditions).orderBy(ACCOUNT.CREATE_TIME.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(AccountDTO.class);
		
		
		
		ListDTO<AccountDTO, AccountListCriteria> result = new ListDTO<AccountDTO, AccountListCriteria>(criteria, items, totalCount);
		
		return result;
		

	}
	
	public ListDTO<VoteDTO, AccountDetailCriteriaDTO> listVotes(AccountDetailCriteriaDTO criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.eq(criteria.getAddress()).or(CONTRACT_VOTE_WITNESS.VOTE_ADDRESS.eq(criteria.getAddress())));
		
		SelectOnConditionStep<?> listQuery = this.dslContext.select(TRANSACTION.TIMESTAMP,CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.as("from"),CONTRACT_VOTE_WITNESS.VOTE_ADDRESS.as("to"),CONTRACT_VOTE_WITNESS.VOTE_COUNT).from(CONTRACT_VOTE_WITNESS)
				.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_VOTE_WITNESS.TRANSACTION_ID));
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(CONTRACT_VOTE_WITNESS);
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VoteDTO> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VoteDTO.class);
		
		
		
		ListDTO<VoteDTO, AccountDetailCriteriaDTO> result = new ListDTO<VoteDTO, AccountDetailCriteriaDTO>(criteria, items, totalCount);
		
		return result;
		
		
		
	}
	
	public ListDTO<AllowanceWidthdrawDTO, AccountDetailCriteriaDTO> listAllowanceWithdrawals(AccountDetailCriteriaDTO criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(CONTRACT_WITHDRAW_BALANCE.OWNER_ADDRESS.eq(criteria.getAddress()));
		
		SelectOnConditionStep<?> listQuery = this.dslContext.select(TRANSACTION.ID.as("txId"),TRANSACTION.TIMESTAMP).from(CONTRACT_WITHDRAW_BALANCE)
				.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_WITHDRAW_BALANCE.TRANSACTION_ID));
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(CONTRACT_WITHDRAW_BALANCE);
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<AllowanceWidthdrawDTO> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(AllowanceWidthdrawDTO.class);
		
		
		
		ListDTO<AllowanceWidthdrawDTO, AccountDetailCriteriaDTO> result = new ListDTO<AllowanceWidthdrawDTO, AccountDetailCriteriaDTO>(criteria, items, totalCount);
		
		return result;
		
		
		
	}
	
	
	public ListDTO<FrozenBalanceDTO, AccountDetailCriteriaDTO> listFrozenBalance(AccountDetailCriteriaDTO criteria){
		
		
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
		
		prepareFrozenBalanceDTO(items);
		
		ListDTO<FrozenBalanceDTO, AccountDetailCriteriaDTO> result = new ListDTO<FrozenBalanceDTO, AccountDetailCriteriaDTO>(criteria, items, totalCount);
		
		return result;
		
	}
	
	private void prepareFrozenBalanceDTO(List<FrozenBalanceDTO> items) {
		
		for (int i = 0; i < items.size(); i++) {
			
			FrozenBalanceDTO fb = items.get(i);
			
			if (fb.getBalance()==0) {
				fb.setUnfreezeTime(new Timestamp(fb.getExpireTime().getTime()));
				fb.setExpireTime(null);
			}
			
		}

	}

	
	public ListDTO<AssetBalanceDTO, AccountDetailCriteriaDTO> listAssetBalances(AccountDetailCriteriaDTO criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(ACCOUNT.ADDRESS.eq(criteria.getAddress()));
		
		SelectOnConditionStep<?> listQuery = this.dslContext.select(ACCOUNT_ASSET.BALANCE,ACCOUNT_ASSET.ASSET_NAME).from(ACCOUNT_ASSET)
				.join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ASSET.ACCOUNT_ID));
				
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
				.from(ACCOUNT_ASSET).join(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ASSET.ACCOUNT_ID));
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<AssetBalanceDTO> items = listQuery.where(conditions).orderBy(ACCOUNT_ASSET.ASSET_NAME.asc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(AssetBalanceDTO.class);
		
		
		
		ListDTO<AssetBalanceDTO, AccountDetailCriteriaDTO> result = new ListDTO<AssetBalanceDTO, AccountDetailCriteriaDTO>(criteria, items, totalCount);
		
		return result;
		
	}

	
	


	
}

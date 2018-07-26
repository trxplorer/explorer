package io.trxplorer.api.route;

import java.util.Optional;

import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.api.dto.account.AccountCriteria;
import io.trxplorer.api.dto.account.AccountExistsCriteria;
import io.trxplorer.api.dto.account.AccountInfo;
import io.trxplorer.api.dto.account.AssetBalanceModel;
import io.trxplorer.api.dto.account.FrozenBalanceModel;
import io.trxplorer.api.dto.account.VoteDTO;
import io.trxplorer.api.dto.common.ListResult;
import io.trxplorer.api.dto.transaction.TransactionDTO;
import io.trxplorer.api.dto.witness.AllowanceWidthdrawModel;
import io.trxplorer.api.service.AccountService;
import io.trxplorer.api.service.TransactionService;
import io.trxplorer.service.dto.transaction.TransferModel;
import io.trxplorer.service.dto.vote.VoteModel;

@Singleton
public class AccountRoutes {

	private AccountService accountService;
	private TransactionService transactionService;
	
	@Inject
	public AccountRoutes(AccountService accountService,TransactionService transactionService) {
		this.accountService = accountService;
		this.transactionService = transactionService;
	}
	


	/**
	 * Get basic informations on account
	 * @param address
	 * @return {@link AccountInfo}
	 * @throws Throwable
	 */
	@GET
	@Path(ApiAppRoutePaths.V1.ACCOUNT_INFO)
	public AccountInfo accountInfo(String address) throws Throwable {

		return this.accountService.getAccountByAddress(address);		
	}
	

	
	/**
	 * Checks whether an account exists with at least one of the criteria
	 * @param address account address
	 * @param name account name
	 * @return boolean indicating if the account exists
	 * @throws Throwable
	 */
	@GET
	@Path(ApiAppRoutePaths.V1.ACCOUNT_EXISTS)
	public boolean exists(Optional<String> address,Optional<String> name) throws Throwable {

		AccountExistsCriteria criteria = new AccountExistsCriteria();
		criteria.setAddress(address.orElse(null));
		criteria.setName(name.orElse(null));
		
		return this.accountService.accountExists(criteria);		
	}
	
	/**
	 * Get the all the asset balance associated to an account
	 * @param address
	 * @param limit
	 * @param page
	 * @return
	 */
	@GET
	@Path(ApiAppRoutePaths.V1.ACCOUNT_TOKENS)	
	public ListResult<AssetBalanceModel, AccountCriteria> tokens(String address,Optional<Integer> limit,Optional<Integer> page) {
		
		AccountCriteria criteria = new AccountCriteria();
		
		criteria.setAddress(address);
		criteria.setLimit(limit.orElse(20));
		criteria.setPage(page.orElse(1));
		
		return this.accountService.listAssetBalances(criteria);
	}
	
	/**
	 * Get all the transactions related to a specific account
	 * @param address
	 * @param limit
	 * @param page
	 * @return
	 */
	@GET
	@Path(ApiAppRoutePaths.V1.ACCOUNT_TRANSFERS)	
	public ListResult<TransferModel, AccountCriteria> transactions(String address,Optional<Integer> limit,Optional<Integer> page) {
		
		AccountCriteria criteria = new AccountCriteria();
		
		criteria.setAddress(address);
		criteria.setLimit(limit.orElse(20));
		criteria.setPage(page.orElse(1));
		
		return this.accountService.listTransfers(criteria);
	}
	
	
	@GET
	@Path(ApiAppRoutePaths.V1.ACCOUNT_TOKENPARTICIPATIONS)	
	public ListResult<TransferModel, AccountCriteria> tokenParticipations(String address,Optional<Integer> limit,Optional<Integer> page) {
		
		AccountCriteria criteria = new AccountCriteria();
		
		criteria.setAddress(address);
		criteria.setLimit(limit.orElse(20));
		criteria.setPage(page.orElse(1));
		
		return this.accountService.listTokenParticipations(criteria);
	}
	
	@GET
	@Path(ApiAppRoutePaths.V1.ACCOUNT_VOTES_ALL)	
	public ListResult<VoteModel, AccountCriteria> allVotes(String address,Optional<Integer> limit,Optional<Integer> page) {
		
		AccountCriteria criteria = new AccountCriteria();
		
		criteria.setAddress(address);
		criteria.setLimit(limit.orElse(20));
		criteria.setPage(page.orElse(1));
		
		return this.accountService.listVotes(criteria);
	}

	@GET
	@Path(ApiAppRoutePaths.V1.ACCOUNT_LIVE_VOTES)	
	public ListResult<VoteModel, AccountCriteria> liveVotes(String address,Optional<Integer> limit,Optional<Integer> page) {
		
		AccountCriteria criteria = new AccountCriteria();
		
		criteria.setAddress(address);
		criteria.setLimit(limit.orElse(20));
		criteria.setPage(page.orElse(1));
		
		return this.accountService.listLiveVotes(criteria);
	}
	
	@GET
	@Path(ApiAppRoutePaths.V1.ACCOUNT_WITHDRAWAL)	
	public ListResult<AllowanceWidthdrawModel, AccountCriteria> allWithdrawal(String address,Optional<Integer> limit,Optional<Integer> page) {
		
		AccountCriteria criteria = new AccountCriteria();
		
		criteria.setAddress(address);
		criteria.setLimit(limit.orElse(20));
		criteria.setPage(page.orElse(1));
		
		return this.accountService.listAllowanceWithdrawals(criteria);
	}
	
	/* FIXME
	 * DISABLED
	@GET
	@Path(ApiAppRoutePaths.V1.ACCOUNT_FREEZE_ALL)	
	public ListResult<FrozenBalanceModel, AccountCriteria> freezeAll(String address,Optional<Integer> limit,Optional<Integer> page) {
		
		AccountCriteria criteria = new AccountCriteria();
		
		criteria.setAddress(address);
		criteria.setLimit(limit.orElse(20));
		criteria.setPage(page.orElse(1));
		
		return this.accountService.listFrozenBalance(criteria);
	}
	*/
	
	
	
}

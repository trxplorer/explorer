package io.trxplorer.syncnode.job;

import org.jooby.quartz.Scheduled;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.syncnode.SyncNodeConfig;
import io.trxplorer.syncnode.service.AccountService;
import io.trxplorer.syncnode.service.AccountSyncService;
import io.trxplorer.syncnode.service.ServiceException;

@Singleton
public class AccountSyncJob {

	private AccountService accountService;
	private AccountSyncService accountSyncService;
	private SyncNodeConfig config;

	@Inject
	public AccountSyncJob(AccountSyncService accountSyncService,AccountService accountService,SyncNodeConfig config) {
		this.accountService = accountService;
		this.accountSyncService = accountSyncService;
		this.config = config;
	}
	
	@Scheduled("10ms")
	public void syncAccount() throws ServiceException {
		
		if (!this.config.isAccountJobEnabled()) {
			return;
		}
		
		this.accountSyncService.syncAccounts();
		
	}
	
	@Scheduled("10ms")
	public void syncAccountVote() throws ServiceException {

		if (!this.config.isAccountJobEnabled()) {
			return;
		}
		
		this.accountSyncService.syncAccountVote();
		
	}
	
	public void syncGenesisAccounts() {
		//TODO:
		//Genesis accounts might be used without any transactions appearing on blockchain : for example block rewarding
		// These accounts are updated here
		
	}
	
}

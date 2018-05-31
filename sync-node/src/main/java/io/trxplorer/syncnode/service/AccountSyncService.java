package io.trxplorer.syncnode.service;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class AccountSyncService {
	
	private DSLContext dslContext;
	private AccountService accountService;
	
	private static final Logger logger = LoggerFactory.getLogger(AccountSyncService.class);
	
	@Inject
	public AccountSyncService(DSLContext dslContext,AccountService accountService) {
		this.dslContext = dslContext;
		this.accountService = accountService;
	}
	
	public String getNextAcountToSync() {
		return this.dslContext.select(SYNC_ACCOUNT.ADDRESS).from(SYNC_ACCOUNT).where(SYNC_ACCOUNT.DATE_LOCKED.isNull()).orderBy(SYNC_ACCOUNT.DATE_CREATED.asc()).limit(1).fetchOneInto(String.class);
	}
	
	
	public void syncAccounts() throws ServiceException {
		
		String address = this.getNextAcountToSync();
		
		if (address==null) {
			return;
		}
		logger.debug("=> Syncing account:"+address);
		this.startSync(address);
		
		this.accountService.createOrUpdateAccount(address);
		
		
		this.endSync(address);
	}
	
	public void startSync(String address) {
		this.dslContext.update(SYNC_ACCOUNT).set(SYNC_ACCOUNT.DATE_LOCKED, Timestamp.valueOf(LocalDateTime.now())).where(SYNC_ACCOUNT.ADDRESS.eq(address)).execute();
	}
	
	public void endSync(String address) {
		this.dslContext.delete(SYNC_ACCOUNT).where(SYNC_ACCOUNT.ADDRESS.eq(address).and(SYNC_ACCOUNT.DATE_LOCKED.isNotNull())).execute();
	}
	
	
}

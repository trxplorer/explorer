package io.trxplorer.syncnode.service;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectOffsetStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.trxplorer.model.tables.SyncAccount;
import io.trxplorer.syncnode.SyncNodeConfig;

public class AccountSyncService {
	
	private DSLContext dslContext;
	private AccountService accountService;
	private SyncNodeConfig config;
	
	private static final Logger logger = LoggerFactory.getLogger(AccountSyncService.class);
	
	@Inject
	public AccountSyncService(DSLContext dslContext,AccountService accountService,SyncNodeConfig config) {
		this.dslContext = dslContext;
		this.accountService = accountService;
		this.config = config;
	}
	
	public String getNextAcountToSync() {
		return this.dslContext.select(SYNC_ACCOUNT.ADDRESS).from(SYNC_ACCOUNT).where(SYNC_ACCOUNT.DATE_LOCKED.isNull()).orderBy(SYNC_ACCOUNT.DATE_CREATED.asc()).limit(1).fetchOneInto(String.class);
	}
	
	private void prepareSync() {

		Table<Record1<ULong>> account2Update = DSL.select(SYNC_ACCOUNT.ID).from(SYNC_ACCOUNT).where(SYNC_ACCOUNT.DATE_LOCKED.isNull()).and(SYNC_ACCOUNT.ORIGIN.notIn("contract_vote_witness","contract_unfreeze_balance")).orderBy(SYNC_ACCOUNT.DATE_CREATED.asc()).limit(20).asTable("tmp");
		this.dslContext.update(SYNC_ACCOUNT.join(account2Update).on(account2Update.field(0, ULong.class).eq(SYNC_ACCOUNT.ID)))
		.set(SYNC_ACCOUNT.DATE_LOCKED,Timestamp.valueOf(LocalDateTime.now()))
		.set(SYNC_ACCOUNT.NODE_ID,UInteger.valueOf(this.config.getNodeId()))
		.execute();
		
		
	}

	private void prepareSyncVote() {

		Table<Record1<ULong>> account2Update = DSL.select(SYNC_ACCOUNT.ID).from(SYNC_ACCOUNT).where(SYNC_ACCOUNT.DATE_LOCKED.isNull()).and(SYNC_ACCOUNT.ORIGIN.in("contract_vote_witness","contract_unfreeze_balance")).orderBy(SYNC_ACCOUNT.DATE_CREATED.asc()).limit(20).asTable("tmp");
		this.dslContext.update(SYNC_ACCOUNT.join(account2Update).on(account2Update.field(0, ULong.class).eq(SYNC_ACCOUNT.ID)))
		.set(SYNC_ACCOUNT.DATE_LOCKED,Timestamp.valueOf(LocalDateTime.now()))
		.set(SYNC_ACCOUNT.NODE_ID,UInteger.valueOf(this.config.getNodeId()))
		.execute();
		
		
	}
	
	public void syncAccounts() throws ServiceException {
		
		this.prepareSync();
		
		List<String> addresses = this.dslContext.select(SYNC_ACCOUNT.ADDRESS).from(SYNC_ACCOUNT).where(SYNC_ACCOUNT.NODE_ID.eq(UInteger.valueOf(this.config.getNodeId()))).and(SYNC_ACCOUNT.ORIGIN.notIn("contract_vote_witness","contract_unfreeze_balance")).orderBy(SYNC_ACCOUNT.DATE_CREATED.asc()).limit(20)
		.fetchInto(String.class);

		for(String address:addresses) {

			logger.debug("=> Syncing account:"+address);

			
			this.accountService.createOrUpdateAccount(address);
			
			
			this.endSync(address);
			
		}

	}
	
	
	public void syncAccountVote() throws ServiceException{

		this.prepareSyncVote();
		
		List<String> addresses = this.dslContext.select(SYNC_ACCOUNT.ADDRESS).from(SYNC_ACCOUNT).where(SYNC_ACCOUNT.NODE_ID.eq(UInteger.valueOf(this.config.getNodeId()))).and(SYNC_ACCOUNT.ORIGIN.in("contract_vote_witness","contract_unfreeze_balance")).orderBy(SYNC_ACCOUNT.DATE_CREATED.asc()).limit(20)
		.fetchInto(String.class);

		for(String address:addresses) {

			logger.debug("=> Syncing account:"+address);

			
			this.accountService.createOrUpdateAccount(address);
			
			
			this.endSync(address);
			
		}
		
	}
	
	
	public void startSync(String address) {
		this.dslContext.update(SYNC_ACCOUNT).set(SYNC_ACCOUNT.DATE_LOCKED, Timestamp.valueOf(LocalDateTime.now())).where(SYNC_ACCOUNT.ADDRESS.eq(address)).execute();
	}
	
	public void endSync(String address) {
		this.dslContext.delete(SYNC_ACCOUNT).where(SYNC_ACCOUNT.ADDRESS.eq(address)).execute();
	}
	
	
}

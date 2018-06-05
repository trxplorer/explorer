package io.trxplorer.syncnode.service;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.types.UByte;
import org.jooq.types.ULong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.core.Wallet;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Account.Frozen;
import org.tron.protos.Protocol.Vote;

import com.google.inject.Inject;

import io.trxplorer.model.tables.records.AccountAssetRecord;
import io.trxplorer.model.tables.records.AccountRecord;
import io.trxplorer.model.tables.records.AccountVoteRecord;
import io.trxplorer.troncli.TronCli;

public class AccountService {

	private DSLContext dslContext;
	
	private TronCli tronCli;
	
	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
	
	@Inject
	public AccountService(DSLContext dslContext,TronCli tronCli) {
		this.dslContext = dslContext;
		this.tronCli = tronCli;
	}
	
	
	
	public void createOrUpdateAccount(String address) throws ServiceException {
		
		Account tronAccount = this.tronCli.getAccountByAddress(address);
		
		if (tronAccount==null) {
			logger.error(String.format("Could not find tron account with address: %s", address));
			return;
		}
		
		// Try to fetch existing account
		AccountRecord record = this.dslContext.select(ACCOUNT.ID)
		.from(ACCOUNT).where(ACCOUNT.ADDRESS.eq(address)).fetchOneInto(AccountRecord.class);
		
		// Create it if it doesn't exists yet
		if (record==null) {

			record = this.dslContext.insertInto(ACCOUNT)
			.set(ACCOUNT.ACCOUNT_NAME,tronAccount.getAccountName().toStringUtf8())
			.set(ACCOUNT.TYPE,(byte) tronAccount.getType().getNumber())
			.set(ACCOUNT.IS_WITNESS,(tronAccount.getIsWitness()==true ? UByte.valueOf((byte)1) : UByte.valueOf((byte)0)))
			.set(ACCOUNT.IS_COMMITTEE,(tronAccount.getIsCommittee()==true ? UByte.valueOf((byte)1) : UByte.valueOf((byte)0)))
			.set(ACCOUNT.ADDRESS,address)
			.set(ACCOUNT.ALLOWANCE, ULong.valueOf(tronAccount.getAllowance()))
			.set(ACCOUNT.CREATE_TIME,new Timestamp(tronAccount.getCreateTime()))
			.set(ACCOUNT.BALANCE,tronAccount.getBalance())
			.set(ACCOUNT.BANDWIDTH,tronAccount.getNetUsage())
			.returning(ACCOUNT.ID)
			.fetchOne();
			
		}else {
			
			//Update if exists
			this.dslContext.update(ACCOUNT)
			.set(ACCOUNT.ACCOUNT_NAME,tronAccount.getAccountName().toStringUtf8())
			.set(ACCOUNT.TYPE,(byte) tronAccount.getType().getNumber())
			.set(ACCOUNT.BALANCE,tronAccount.getBalance())
			.set(ACCOUNT.CREATE_TIME,new Timestamp(tronAccount.getCreateTime()))
			.set(ACCOUNT.ALLOWANCE, ULong.valueOf(tronAccount.getAllowance()))
			.set(ACCOUNT.BALANCE,tronAccount.getBalance())
			.set(ACCOUNT.BANDWIDTH,tronAccount.getNetUsage())
			.set(ACCOUNT.IS_WITNESS,(tronAccount.getIsWitness()==true ? UByte.valueOf((byte)1) : UByte.valueOf((byte)0)))
			.set(ACCOUNT.IS_COMMITTEE,(tronAccount.getIsCommittee()==true ? UByte.valueOf((byte)1) : UByte.valueOf((byte)0)))
			.where(ACCOUNT.ID.eq(record.getId()))
			.execute();
			
		}
		
		
		// Update account assets
		if (tronAccount.getAssetMap()!=null && tronAccount.getAssetMap().size()>0) {

			this.dslContext.delete(ACCOUNT_ASSET).where(ACCOUNT_ASSET.ACCOUNT_ID.eq(record.getId())).execute();
			
			List<AccountAssetRecord> accountAssetRecords = new ArrayList<>();
			
			for(String assetName:tronAccount.getAssetMap().keySet()) {
				
				AccountAssetRecord assetRecord = new AccountAssetRecord();
				assetRecord.setAssetName(assetName);
				assetRecord.setBalance(ULong.valueOf(tronAccount.getAssetMap().get(assetName)));
				assetRecord.setAccountId(record.getId());
				
				accountAssetRecords.add(assetRecord);
				
			}
			
			this.dslContext.batchInsert(accountAssetRecords).execute();
			
		}
		
		
		// Update account votes (= current votes)
		if (tronAccount.getVotesList()!=null && tronAccount.getVotesList().size()>0) {
			
			this.dslContext.delete(ACCOUNT_VOTE).where(ACCOUNT_VOTE.ACCOUNT_ID.eq(record.getId())).execute();
			
			List<AccountVoteRecord> accountVoteRecords = new ArrayList<>();
			
			for(Vote accountVote:tronAccount.getVotesList()) {
				
				AccountVoteRecord voteRecord = new AccountVoteRecord();
				voteRecord.setVoteAddress(Wallet.encode58Check(accountVote.getVoteAddress().toByteArray()));
				voteRecord.setVoteCount(ULong.valueOf(accountVote.getVoteCount()));
				voteRecord.setAccountId(record.getId());
				
				accountVoteRecords.add(voteRecord);
			}
			
			this.dslContext.batchInsert(accountVoteRecords).execute();
		}
		
		// Update frozen balance (keep history)
		if (tronAccount.getFrozenList()!=null && tronAccount.getFrozenList().size()>0) {
			
			for(Frozen frozen:tronAccount.getFrozenList()) {
				
				this.dslContext.insertInto(ACCOUNT_FROZEN)
				.set(ACCOUNT_FROZEN.EXPIRE_TIME, new Timestamp(frozen.getExpireTime()))
				.set(ACCOUNT_FROZEN.BALANCE,frozen.getFrozenBalance())
				.set(ACCOUNT_FROZEN.ACCOUNT_ID,record.getId())
				.onDuplicateKeyIgnore()
				.execute();

			}
			

		}
		
		
		
	}
	
	

	
}

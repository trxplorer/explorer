package io.trxplorer.webapp.service;

import static io.trxplorer.model.Tables.*;
import org.jooq.DSLContext;
import org.jooq.types.ULong;

import com.google.inject.Inject;

public class SearchService {

	private DSLContext dslContext;

	@Inject
	public SearchService(DSLContext dslContext) {
		this.dslContext = dslContext;
	}
	
	public String getWitnessAddressById(Long id) {
		return this.dslContext.select(WITNESS.ADDRESS).from(WITNESS).where(WITNESS.ID.eq(ULong.valueOf(id))).fetchOneInto(String.class);
	}
	
	
	public boolean txHashExists(String hash) {
		
		String result = this.dslContext.select(TRANSACTION.HASH)
		.from(TRANSACTION).where(TRANSACTION.HASH.eq(hash))
		.fetchOneInto(String.class)
		;
		
		if (result!=null) {
			return true;
		}
		
		return false;
	}
	
	public boolean blockNumExists(String num) {
		
		String result = this.dslContext.select(BLOCK.NUM)
		.from(BLOCK).where(BLOCK.NUM.eq(ULong.valueOf(num)))
		.fetchOneInto(String.class)
		;
		
		if (result!=null) {
			return true;
		}
		
		return false;
	}	
	
	
	public String getBlockNumByHash(String hash) {
		
		String result = this.dslContext.select(BLOCK.NUM)
		.from(BLOCK).where(BLOCK.HASH.eq(hash))
		.fetchOneInto(String.class)
		;
		

		
		return result;		
	}
	
	public String getWitnessAddressByUrl(String url) {

		String result = this.dslContext.select(WITNESS.ADDRESS)
		.from(WITNESS).where(WITNESS.URL.eq(url)).limit(1)
		.fetchOneInto(String.class)
		;

		
		return result;	
		
	}
	
	
	public boolean tokenByNameExists(String url) {

		String result = this.dslContext.select(CONTRACT_ASSET_ISSUE.NAME)
		.from(CONTRACT_ASSET_ISSUE).where(CONTRACT_ASSET_ISSUE.NAME.eq(url)).limit(1)
		.fetchOneInto(String.class)
		;
		
		if (result!=null) {
			return true;
		}
		
		return false;	
		
	}
	
	public boolean accountByAddressExists(String address) {
		
		String result = this.dslContext.select(ACCOUNT.ADDRESS)
		.from(ACCOUNT).where(ACCOUNT.ADDRESS.eq(address)).limit(1)
		.fetchOneInto(String.class)
		;
		
		if (result!=null) {
			return true;
		}
		
		return false;	
	}
	
	public boolean accountByNameExists(String address) {
		
		String result = this.dslContext.select(ACCOUNT.ADDRESS)
		.from(ACCOUNT).where(ACCOUNT.ACCOUNT_NAME.eq(address)).limit(1)
		.fetchOneInto(String.class)
		;
		
		if (result!=null) {
			return true;
		}
		
		return false;	
	}
	
	public String getAccountAddressByAccountName(String accountName) {
		
		String result = this.dslContext.select(ACCOUNT.ADDRESS)
		.from(ACCOUNT).where(ACCOUNT.ACCOUNT_NAME.eq(accountName)).limit(1)
		.fetchOneInto(String.class)
		;
		
		
		return result;	
	}
	
}

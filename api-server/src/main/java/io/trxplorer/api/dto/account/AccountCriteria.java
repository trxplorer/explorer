package io.trxplorer.api.dto.account;

import io.trxplorer.api.dto.common.CommonCriteria;

public class AccountCriteria extends CommonCriteria{
	
	private String address;

	//trx only
	private boolean trx;
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isTrx() {
		return trx;
	}

	public void setTrx(boolean trx) {
		this.trx = trx;
	}
	
	
	
}

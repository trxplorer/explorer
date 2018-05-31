package io.trxplorer.api.dto.account;

import io.trxplorer.api.dto.common.CommonCriteria;

public class AccountCriteria extends CommonCriteria{
	
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
}

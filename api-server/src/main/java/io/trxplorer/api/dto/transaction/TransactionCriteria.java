package io.trxplorer.api.dto.transaction;

import io.trxplorer.api.dto.common.CommonCriteria;

public class TransactionCriteria extends CommonCriteria{
	
	private String block;
	
	private String address;//all transactions for this address : from or to
	
	private String assetName;
	
	
	public String getBlock() {
		return block;
	}
	
	public void setBlock(String block) {
		this.block = block;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}


	public String getAssetName() {
		return assetName;
	}


	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	
	
	
}

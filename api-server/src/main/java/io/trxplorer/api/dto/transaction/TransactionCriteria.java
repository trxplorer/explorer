package io.trxplorer.api.dto.transaction;

import io.trxplorer.api.dto.common.CommonCriteria;

public class TransactionCriteria extends CommonCriteria{
	
	private Integer block;

	public Integer getBlock() {
		return block;
	}


	public void setBlock(Integer block) {
		this.block = block;
	}
	
	
}

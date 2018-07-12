package io.trxplorer.service.dto.transaction;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.trxplorer.service.dto.common.CommonCriteriaDTO;

public class TransactionCriteriaDTO extends CommonCriteriaDTO{
	
	private String block;
	
	private String address;//all transactions for this address : from or to
	
	private String assetName;
	
	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		if (StringUtils.isNotBlank(block)) {
			map.put("block", block);
		}
		
		return map;
	}

	
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

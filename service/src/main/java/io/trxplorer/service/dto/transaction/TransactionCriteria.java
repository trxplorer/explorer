package io.trxplorer.service.dto.transaction;

import java.util.HashMap;
import java.util.Map;

import io.trxplorer.service.dto.common.CommonCriteriaDTO;

public class TransactionCriteria extends CommonCriteriaDTO{
	
	private Integer block;
	
	
	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		if (block!=null) {
			map.put("block", String.valueOf(block));
		}
		
		return map;
	}


	public Integer getBlock() {
		return block;
	}


	public void setBlock(Integer block) {
		this.block = block;
	}

	
	
	
}

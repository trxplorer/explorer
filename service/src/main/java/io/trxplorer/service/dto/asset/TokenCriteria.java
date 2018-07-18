package io.trxplorer.service.dto.asset;

import java.util.HashMap;
import java.util.Map;

import io.trxplorer.service.dto.common.CommonCriteriaDTO;

public class TokenCriteria extends CommonCriteriaDTO{
	
	private String name;
	
	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		
		return map;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}



	
	
	
}

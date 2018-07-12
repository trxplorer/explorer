package io.trxplorer.service.dto.asset;

import java.util.HashMap;
import java.util.Map;

import io.trxplorer.service.dto.common.CommonCriteriaDTO;

public class AssetIssueDetailCriteriaDTO extends CommonCriteriaDTO{
	
	private String tab;
	
	private String id;
	
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

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}


	public String getTab() {
		return tab;
	}


	public void setTab(String tab) {
		this.tab = tab;
	}

	
	
	
}

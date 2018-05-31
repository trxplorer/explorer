package io.trxplorer.webapp.dto.asset;

import java.util.HashMap;
import java.util.Map;

import io.trxplorer.webapp.dto.common.CommonCriteriaDTO;

public class AssetIssueDetailCriteriaDTO extends CommonCriteriaDTO{
	
	private String tab;
	
	private String id;
	
	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		
		return map;
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

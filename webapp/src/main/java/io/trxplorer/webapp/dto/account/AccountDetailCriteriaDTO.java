package io.trxplorer.webapp.dto.account;

import java.util.HashMap;
import java.util.Map;

import io.trxplorer.webapp.dto.common.CommonCriteriaDTO;

public class AccountDetailCriteriaDTO extends CommonCriteriaDTO{


	private String address;
	
	private String tab;
	
	public AccountDetailCriteriaDTO(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}

	
	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		return map;
	}
	
	public String getTab() {
		return tab;
	}
	
	public void setTab(String tab) {
		this.tab = tab;
	}
	
}

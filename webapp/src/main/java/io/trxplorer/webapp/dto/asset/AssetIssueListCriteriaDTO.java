package io.trxplorer.webapp.dto.asset;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.trxplorer.webapp.dto.common.CommonCriteriaDTO;

public class AssetIssueListCriteriaDTO extends CommonCriteriaDTO{
	
	private String issuer;
	
	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		if (StringUtils.isNotBlank(this.issuer)) {
			map.put("issuer", this.issuer);
		}
		
		return map;
	}
	
	

	public String getIssuer() {
		return issuer;
	}
	
	public void setIssuer(String issuerAddress) {
		this.issuer = issuerAddress;
	}
}

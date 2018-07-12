package io.trxplorer.service.dto.asset;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.trxplorer.service.dto.common.CommonCriteriaDTO;

public class AssetParticipationCriteriaDTO extends CommonCriteriaDTO{
	
	//from or to
	private String address;
	
	private String assetName;
	
	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		
		return map;
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

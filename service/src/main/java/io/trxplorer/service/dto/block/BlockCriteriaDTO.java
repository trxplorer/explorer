package io.trxplorer.service.dto.block;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.trxplorer.service.dto.common.CommonCriteriaDTO;

public class BlockCriteriaDTO extends CommonCriteriaDTO{
	
	private String producedBy;
	
	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		if (StringUtils.isNotBlank(this.producedBy)) {
			map.put("producedBy", this.producedBy);
		}
		
		return map;
	}
	
	public String getProducedBy() {
		return producedBy;
	}
	
	public void setProducedBy(String producedBy) {
		this.producedBy = producedBy;
	}

	
}

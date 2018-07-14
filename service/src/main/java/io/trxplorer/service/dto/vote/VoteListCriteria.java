package io.trxplorer.service.dto.vote;

import java.util.HashMap;
import java.util.Map;

import io.trxplorer.service.dto.common.CommonCriteriaDTO;

public class VoteListCriteria extends CommonCriteriaDTO{
	
	private Integer round;
	
	private String toAddress;
	
	public VoteListCriteria() {
	}
	
	public Integer getRound() {
		return round;
	}
	
	public void setRound(Integer round) {
		this.round = round;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		return map;
	}

	
}

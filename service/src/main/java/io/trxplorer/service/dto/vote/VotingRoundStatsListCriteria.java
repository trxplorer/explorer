package io.trxplorer.service.dto.vote;

import java.util.HashMap;
import java.util.Map;

import io.trxplorer.service.dto.common.CommonCriteriaDTO;

public class VotingRoundStatsListCriteria extends CommonCriteriaDTO{
	
	private Integer round;

	private Integer maxRound;
	
	public VotingRoundStatsListCriteria() {
	}
	
	
	
	public Integer getMaxRound() {
		return maxRound;
	}



	public void setMaxRound(Integer maxRound) {
		this.maxRound = maxRound;
	}



	public Integer getRound() {
		return round;
	}
	
	public void setRound(Integer round) {
		this.round = round;
	}
	
	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		return map;
	}

	
}

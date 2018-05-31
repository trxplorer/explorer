package io.trxplorer.webapp.dto.witness;

import java.util.HashMap;
import java.util.Map;

import io.trxplorer.webapp.dto.common.CommonCriteriaDTO;

public class WitnessListCriteriaDTO extends CommonCriteriaDTO{
	
	private Boolean superRepresentative;
	
	private boolean randomCandidates;
	
	@Override
	public Map<String, String> params() {
		
		HashMap<String, String> map = new HashMap<>();
		
		return map;
	}
	
	public boolean isRandomCandidates() {
		return randomCandidates;
	}
	
	public void setRandomCandidates(boolean randomCandidates) {
		this.randomCandidates = randomCandidates;
	}
	
	public Boolean isSuperRepresentative() {
		return superRepresentative;
	}
	
	public void setSuperRepresentative(Boolean superRepresentative) {
		this.superRepresentative = superRepresentative;
	}
	
}

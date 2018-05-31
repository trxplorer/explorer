package io.trxplorer.api.dto.witness;

import io.trxplorer.api.dto.common.CommonCriteria;

public class WitnessListCriteriaDTO extends CommonCriteria{
	
	private Boolean superRepresentative;
	
	private boolean randomCandidates;

	
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

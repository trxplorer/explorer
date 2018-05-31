package io.trxplorer.api.dto.witness;

import io.trxplorer.api.utils.TransactionHelper;

public class WitnessDTO {

	private long voteCount;

	private long totalProduced;

	private long totalMissed;

	private String url;
	
	private String address;
	
	private boolean isSR;
	
	
	
	public boolean isSR() {
		return isSR;
	}

	public void setSR(boolean isSR) {
		this.isSR = isSR;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}
	
	public long getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(long voteCount) {
		this.voteCount = voteCount;
	}

	public long getTotalProduced() {
		return totalProduced;
	}

	public void setTotalProduced(long totalProduced) {
		this.totalProduced = totalProduced;
	}

	public long getTotalMissed() {
		return totalMissed;
	}

	public void setTotalMissed(long totalMissed) {
		this.totalMissed = totalMissed;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


}

package io.trxplorer.webapp.dto.witness;

import io.trxplorer.webapp.utils.TransactionHelper;

public class WitnessDTO {
	
	private long voteCount;
	
	private long totalProduced;
	
	private long totalMissed;
	
	private String url;
	
	private String shortUrl;
	
	private String address;
	
	
	
	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
	
	public String getVoteCountStr() {
		return TransactionHelper.getTrxAmount(voteCount);
	}
	
}

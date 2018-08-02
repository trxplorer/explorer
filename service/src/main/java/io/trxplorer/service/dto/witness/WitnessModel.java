package io.trxplorer.service.dto.witness;

import java.text.NumberFormat;
import java.util.Locale;

import io.trxplorer.service.utils.TransactionHelper;

public class WitnessModel {
	
	private long voteCount;
	
	private long totalProduced;
	
	private long totalMissed;
	
	private long lastBlock;
	
	private String url;
	
	private String shortUrl;
	
	private String address;
	
	private String name;
	
	private long liveVotes;
	
	
	
	public long getLiveVotes() {
		return liveVotes;
	}

	public void setLiveVotes(long liveVotes) {
		this.liveVotes = liveVotes;
	}

	public long getLastBlock() {
		return lastBlock;
	}

	public void setLastBlock(long lastBlock) {
		this.lastBlock = lastBlock;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
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
		return NumberFormat.getNumberInstance(Locale.US).format(voteCount);
	}
	
	public String getTotalProducedStr() {
		return NumberFormat.getNumberInstance(Locale.US).format(totalProduced);
	}
}

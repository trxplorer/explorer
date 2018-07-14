package io.trxplorer.service.dto.vote;

import java.sql.Timestamp;

public class VoteModel {

	private String from;
	
	private String to;
	
	private Long voteCount;
	
	private Timestamp timestamp;
	

	public Long getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(Long voteCount) {
		this.voteCount = voteCount;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public long getTimestamp() {
		return timestamp.getTime();
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}

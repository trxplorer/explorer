package io.trxplorer.service.dto.vote;

import java.sql.Timestamp;

public class VoteModel {

	private String from;
	
	private String to;
	
	private Long votes;
	
	private Timestamp timestamp;
	

	public Long getVotes() {
		return votes;
	}

	public void setVotes(Long voteCount) {
		this.votes = voteCount;
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
		if (timestamp==null) {
			return 0;
		}
		return timestamp.getTime()/1000;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}

package io.trxplorer.service.dto.vote;

import java.sql.Timestamp;

public class VotingRoundStatsModel {

	private String address;
	
	private long votes;
	
	private int position;
	
	private String name;
	
	private int round;
	
	private String url;
	
	private Timestamp startDate;
	
	private Timestamp endDate;
	
	
	


	public long getStartDate() {
		return startDate.getTime()/1000;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate.getTime()/1000;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getVotes() {
		return votes;
	}

	public void setVotes(long voteCount) {
		this.votes = voteCount;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}


	
	
	
}

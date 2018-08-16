package io.trxplorer.service.dto.vote;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VotingRoundModel {

	private Timestamp startDate;
	
	private Timestamp endDate;
	
	private int round;
	
	private long voteCount;

	@JsonIgnore
	public Timestamp getStartDate() {
		return startDate;
	}

	public long getStart() {
		if (this.startDate!=null) {
			return this.startDate.getTime()/1000;
		}else {
			return 0;
		}
		
	}
	
	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	@JsonIgnore
	public Timestamp getEndDate() {
		return endDate;
	}

	public long getEnd() {
		if (this.endDate!=null) {
			return this.endDate.getTime()/1000;
		}else {
			return 0;
		}
		
	}
	
	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public long getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(long voteCount) {
		this.voteCount = voteCount;
	}
	
	@JsonIgnore
	public String getVoteCountStr() {
		return NumberFormat.getNumberInstance(Locale.US).format(voteCount);
	}
	
	
}

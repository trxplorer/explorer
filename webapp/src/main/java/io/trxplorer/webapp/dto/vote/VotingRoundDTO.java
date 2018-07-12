package io.trxplorer.webapp.dto.vote;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Locale;

public class VotingRoundDTO {

	private Timestamp startDate;
	
	private Timestamp endDate;
	
	private int round;
	
	private long voteCount;

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
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
	
	public String getVoteCountStr() {
		return NumberFormat.getNumberInstance(Locale.US).format(voteCount);
	}
	
	
}

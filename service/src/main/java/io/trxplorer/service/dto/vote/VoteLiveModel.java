package io.trxplorer.service.dto.vote;

public class VoteLiveModel {

	private String address;
	
	private long votes;
	
	private int position;
	
	private String url;
	
	private String name;
	
	private int positionChange;
	
	private Long votesChange;
	
	
	
	
	public int getPositionChange() {
		return positionChange;
	}

	public void setPositionChange(int positionChange) {
		this.positionChange = positionChange;
	}

	public Long getVotesChange() {
		return votesChange;
	}

	public void setVotesChange(Long votesChange) {
		this.votesChange = votesChange;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public void setVotes(long votes) {
		this.votes = votes;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	
	
	
}

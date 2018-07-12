package io.trxplorer.service.dto.block;

public class BlockDTO {

	private Long num;
	
	private String hash;
	
	private String witnessAddress;
	
	private int size;

	private String parentHash;
	
	private int txCount;
	
	private String timestamp;
	
	private String witness;//address or url
	
	private String reward;
	
	private Long maxNum;
	
	private boolean confirmed;
	
	
	
	
	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public Long getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(Long maxNum) {
		this.maxNum = maxNum;
	}

	public String getParentHash() {
		return parentHash;
	}

	public void setParentHash(String parentHash) {
		this.parentHash = parentHash;
	}

	public int getTxCount() {
		return txCount;
	}

	public void setTxCount(int txCount) {
		this.txCount = txCount;
	}

	public void setNum(Long num) {
		this.num = num;
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Long getNum() {
		return num;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getWitnessAddress() {
		return witnessAddress;
	}

	public void setWitnessAddress(String witnessAddress) {
		this.witnessAddress = witnessAddress;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getWitness() {
		return witness;
	}

	public void setWitness(String witness) {
		this.witness = witness;
	}
	
	public void setReward(String reward) {
		this.reward = reward;
	}
	
	public String getReward() {
		return reward;
	}
	
	
}

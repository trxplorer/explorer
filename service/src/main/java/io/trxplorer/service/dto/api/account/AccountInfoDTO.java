package io.trxplorer.service.dto.api.account;

import java.sql.Timestamp;

import io.trxplorer.service.dto.witness.WitnessModel;

public class AccountInfoDTO {
	
	private String name;
	
	private long balance;
	
	private String address;
	
	private long assetCount;
	
	private long bandwidth;
	
	private long allowance;
	
	private long frozenBalance;
	
	private long totalBalance;
	
	private Timestamp frozenExpire;
	
	private String percentage;
	
	private String rank;
	
	private Timestamp createTime;
	
	private WitnessModel witness;
	
	private boolean isWitness;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getAssetCount() {
		return assetCount;
	}

	public void setAssetCount(long assetCount) {
		this.assetCount = assetCount;
	}

	public long getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(long bandwidth) {
		this.bandwidth = bandwidth;
	}

	public long getAllowance() {
		return allowance;
	}

	public void setAllowance(long allowance) {
		this.allowance = allowance;
	}

	public long getFrozenBalance() {
		return frozenBalance;
	}

	public void setFrozenBalance(long frozenBalance) {
		this.frozenBalance = frozenBalance;
	}

	public long getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(long totalBalance) {
		this.totalBalance = totalBalance;
	}

	public Timestamp getFrozenExpire() {
		return frozenExpire;
	}

	public void setFrozenExpire(Timestamp frozenExpire) {
		this.frozenExpire = frozenExpire;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public WitnessModel getWitness() {
		return witness;
	}

	public void setWitness(WitnessModel witness) {
		this.witness = witness;
	}

	public boolean isWitness() {
		return isWitness;
	}

	public void setWitness(boolean isWitness) {
		this.isWitness = isWitness;
	}
	
	
	
}

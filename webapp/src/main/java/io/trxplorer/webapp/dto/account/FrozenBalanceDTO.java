package io.trxplorer.webapp.dto.account;

import java.sql.Timestamp;

import io.trxplorer.webapp.utils.TransactionHelper;

public class FrozenBalanceDTO {

	private Timestamp expireTime;
	
	private Long balance;
	
	private Timestamp unfreezeTime;
	
	
	
	public Timestamp getUnfreezeTime() {
		return unfreezeTime;
	}

	public void setUnfreezeTime(Timestamp unfreezeTime) {
		this.unfreezeTime = unfreezeTime;
	}

	public Timestamp getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Timestamp expireTime) {
		this.expireTime = expireTime;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}
	
	public String getBalanceStr() {
		return TransactionHelper.getTrxAmount(balance);
	}
	
	
}

package io.trxplorer.service.dto.transaction;

import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

import io.trxplorer.service.utils.TransactionHelper;

public class TransferModel {

	private String hash;
	
	private String from;
	
	private String to;
	
	private long amount;
	
	private String token;

	private Timestamp timestamp;
	
	
	
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getTimestamp() {
		if (timestamp==null) {
			return 0;
		}
		return timestamp.getTime()/1000;
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

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
	
	
}

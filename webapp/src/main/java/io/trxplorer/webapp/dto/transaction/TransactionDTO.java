package io.trxplorer.webapp.dto.transaction;

import java.text.NumberFormat;
import java.util.Locale;

import io.trxplorer.webapp.utils.TransactionHelper;

public class TransactionDTO {

	
	private String hash;
	
	private String shortHash;
	
	private String from;
	
	private String to;
	
	private String type;
	
	private String amount;
	
	private long blockNum;
	
	private String timestamp;
	
	private String token;
	
	private boolean confirmed;
	
	
	
	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public String getShortHash() {
		return shortHash;
	}

	public void setShortHash(String shortHash) {
		this.shortHash = shortHash;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
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

	public long getBlockNum() {
		return blockNum;
	}

	public void setBlockNum(long blockNum) {
		this.blockNum = blockNum;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getAmountStr() {
		return TransactionHelper.getTrxAmount(amount);
	}
	
	public String getTokenAmountStr() {
		return NumberFormat.getNumberInstance(Locale.US).format(Long.valueOf(amount));
	}
	
	
}

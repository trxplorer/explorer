package io.trxplorer.webapp.dto.asset;

import java.sql.Timestamp;

import io.trxplorer.webapp.utils.TransactionHelper;

public class AssetParticipationDTO {

	private String from;
	
	private String to;
	
	private long amount;
	
	private String assetName;
	
	private Timestamp timestamp;
	
	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
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
	
	public String getAmountStr() {
		return TransactionHelper.getTrxAmount(amount);
	}
	
	
}

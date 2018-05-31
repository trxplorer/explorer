package io.trxplorer.api.dto.witness;

import java.sql.Timestamp;

public class AllowanceWidthdrawDTO {

	private String txId;
	
	private Timestamp timestamp;

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
	
}

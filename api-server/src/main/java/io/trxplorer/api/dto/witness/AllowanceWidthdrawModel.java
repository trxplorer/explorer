package io.trxplorer.api.dto.witness;

import java.sql.Timestamp;

public class AllowanceWidthdrawModel {

	private String tx;
	
	private Timestamp timestamp;

	

	public String getTx() {
		return tx;
	}

	public void setTx(String tx) {
		this.tx = tx;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
	
}

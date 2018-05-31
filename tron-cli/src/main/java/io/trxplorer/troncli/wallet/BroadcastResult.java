package io.trxplorer.troncli.wallet;

public class BroadcastResult {

	private boolean success;
	
	private String errorMsg;

	private String txId;

	private int code;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public void setCode(int number) {
		this.code = number;
	}
	
	public int getCode() {
		return code;
	}

	
	
	
	
}

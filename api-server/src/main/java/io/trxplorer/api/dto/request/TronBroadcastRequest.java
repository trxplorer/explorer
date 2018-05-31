package io.trxplorer.api.dto.request;

public class TronBroadcastRequest {
	
	private String node;
	
	private String payload;

	public String getNode() {
		return node;
	}
	
	public void setNode(String node) {
		this.node = node;
	}
	
	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	
	
}

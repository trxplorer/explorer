package io.trxplorer.service.dto.status;

public class StatusDTO {

	private StatusType status;
	
	private long totalBlocksMissing;
	
	private long trxplorerBlockRate;
	
	private long tronBlockRate;
	
	private long trxplorerLastBlock;
	
	private long tronLastBlock;
	
	
	
	public long getTrxplorerLastBlock() {
		return trxplorerLastBlock;
	}

	public void setTrxplorerLastBlock(long trxplorerLastBlock) {
		this.trxplorerLastBlock = trxplorerLastBlock;
	}

	public long getTronLastBlock() {
		return tronLastBlock;
	}

	public void setTronLastBlock(long tronLastBlock) {
		this.tronLastBlock = tronLastBlock;
	}

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	public long getTotalBlocksMissing() {
		return totalBlocksMissing;
	}

	public void setTotalBlocksMissing(long totalBlocksMissing) {
		this.totalBlocksMissing = totalBlocksMissing;
	}

	public long getTrxplorerBlockRate() {
		return trxplorerBlockRate;
	}

	public void setTrxplorerBlockRate(long trxplorerBlockRate) {
		this.trxplorerBlockRate = trxplorerBlockRate;
	}

	public long getTronBlockRate() {
		return tronBlockRate;
	}

	public void setTronBlockRate(long tronBlockRate) {
		this.tronBlockRate = tronBlockRate;
	}
	
	
}

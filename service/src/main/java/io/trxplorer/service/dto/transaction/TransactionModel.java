package io.trxplorer.service.dto.transaction;

import org.tron.protos.Protocol.Transaction.Contract.ContractType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author nicholas
 *
 */
public class TransactionModel {

	@JsonIgnore
	private long id;
	
	private String hash;
	
	private String from;
	
	private int type;
	
	private long block;
	
	private String timestamp;
	
	private boolean confirmed;
	
	private Object contract;
	
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Object getContract() {
		return contract;
	}

	public void setContract(Object contract) {
		this.contract = contract;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
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

	
	public long getBlock() {
		return block;
	}

	public void setBlock(long block) {
		this.block = block;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return ContractType.forNumber(this.type).name().toUpperCase();
	}
	
	public int getTypeInt(){
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}

package io.trxplorer.service.dto.account;

public class TokenHolderModel {

	private String address;
	
	private Long balance;

	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}
	
}

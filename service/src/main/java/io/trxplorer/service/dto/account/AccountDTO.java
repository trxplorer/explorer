package io.trxplorer.service.dto.account;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.trxplorer.service.dto.asset.AssetIssueDTO;
import io.trxplorer.service.dto.asset.AssetParticipationDTO;
import io.trxplorer.service.dto.transaction.TransactionModel;
import io.trxplorer.service.dto.vote.VoteModel;
import io.trxplorer.service.dto.witness.AllowanceWidthdrawDTO;
import io.trxplorer.service.dto.witness.WitnessModel;
import io.trxplorer.service.utils.TransactionHelper;

public class AccountDTO {
	
	@JsonIgnore
	private Long id;
	
	private String name;
	
	private long balance;
	
	private String address;
	
	private long bandwidth;
	
	private long allowance;
	
	private long frozenBalance;
	
	private long totalBalance;
	
	private Timestamp frozenExpire;
	
	private Timestamp createTime;
	
	private WitnessModel witness;
	
	private AccountDetailCriteriaDTO criteria;
	
	private boolean isWitness;
	
	private String usdValue;
	
	private Integer transferFromCount;
	
	private Integer transferToCount;
	
	private Integer tokensCount;
	
	private Integer participationsCount;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public Integer getParticipationsCount() {
		return participationsCount;
	}

	public void setParticipationsCount(Integer participationsCount) {
		this.participationsCount = participationsCount;
	}

	public Integer getTransferFromCount() {
		return transferFromCount;
	}

	public void setTransferFromCount(Integer transferFromCount) {
		this.transferFromCount = transferFromCount;
	}

	public Integer getTransferToCount() {
		return transferToCount;
	}

	public void setTransferToCount(Integer transferToCount) {
		this.transferToCount = transferToCount;
	}

	public Integer getTokensCount() {
		return tokensCount;
	}

	public void setTokensCount(Integer tokensCount) {
		this.tokensCount = tokensCount;
	}

	public String getUsdValue() {
		return usdValue;
	}

	public void setUsdValue(String usdValue) {
		this.usdValue = usdValue;
	}



	public long getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(long totalBalance) {
		this.totalBalance = totalBalance;
	}

	public Timestamp getFrozenExpire() {
		return frozenExpire;
	}

	public void setFrozenExpire(Timestamp frozenExpire) {
		this.frozenExpire = frozenExpire;
	}

	public long getFrozenBalance() {
		return frozenBalance;
	}

	public void setFrozenBalance(long frozenBalance) {
		this.frozenBalance = frozenBalance;
	}

	public long getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(long bandwidth) {
		this.bandwidth = bandwidth;
	}

	public long getAllowance() {
		return allowance;
	}

	public void setAllowance(long allowance) {
		this.allowance = allowance;
	}

	public boolean isWitness() {
		return isWitness;
	}

	public void setWitness(boolean isWitness) {
		this.isWitness = isWitness;
	}

	
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createdTime) {
		this.createTime = createdTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
	public void setWitness(WitnessModel witness) {
		this.witness = witness;
	}
	
	public WitnessModel getWitness() {
		return witness;
	}
	
	public void setCriteria(AccountDetailCriteriaDTO criteria) {
		this.criteria = criteria;
	}
	
	public AccountDetailCriteriaDTO getCriteria() {
		return criteria;
	}

	
	public String getBalanceStr() {
		return TransactionHelper.getTrxAmount(this.balance);
	}
	
	public String getFrozenBalanceStr() {
		return TransactionHelper.getTrxAmount(this.frozenBalance);
	}
	
	public String getTotalBalanceStr() {
		return TransactionHelper.getTrxAmount(this.totalBalance);
	}
	
	public String getBandwidthStr() {
		return NumberFormat.getNumberInstance(Locale.US).format(this.bandwidth);
	}
	
	public String getAllowanceStr() {
		return TransactionHelper.getTrxAmount(allowance);
	}
	

}

package io.trxplorer.service.dto.account;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import io.trxplorer.service.dto.asset.AssetIssueDTO;
import io.trxplorer.service.dto.asset.AssetParticipationDTO;
import io.trxplorer.service.dto.transaction.TransactionDTO;
import io.trxplorer.service.dto.witness.AllowanceWidthdrawDTO;
import io.trxplorer.service.dto.witness.WitnessDTO;
import io.trxplorer.service.utils.TransactionHelper;

public class AccountDTO {

	private String name;
	
	private long balance;
	
	private String address;
	
	private List<TransactionDTO> transactions;

	private List<AssetBalanceDTO> assetBalances;
	
	private List<VoteDTO> votes;
	
	private List<FrozenBalanceDTO> frozenBalances;
	
	private List<AssetIssueDTO> assetIssues;
	
	private List<AssetParticipationDTO> assetParticipations;
	
	private List<AllowanceWidthdrawDTO> allowanceWithdrawals;
	
	private long voteSpent;
	
	private long voteReceived;
	
	private long assetCount;
	
	private long bandwidth;
	
	private long allowance;
	
	private long frozenBalance;
	
	private long totalBalance;
	
	private Timestamp frozenExpire;
	
	private String percentage;
	
	private String rank;
	
	private Timestamp createTime;
	
	private WitnessDTO witness;
	
	private AccountDetailCriteriaDTO criteria;
	
	private boolean isWitness;
	
	
	


	public List<AllowanceWidthdrawDTO> getAllowanceWithdrawals() {
		return allowanceWithdrawals;
	}

	public void setAllowanceWithdrawals(List<AllowanceWidthdrawDTO> allowanceWithdrawals) {
		this.allowanceWithdrawals = allowanceWithdrawals;
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

	public List<AssetParticipationDTO> getAssetParticipations() {
		return assetParticipations;
	}

	public void setAssetParticipations(List<AssetParticipationDTO> assetParticipations) {
		this.assetParticipations = assetParticipations;
	}
	
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createdTime) {
		this.createTime = createdTime;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
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

	public List<TransactionDTO> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionDTO> transactions) {
		this.transactions = transactions;
	}

	public List<AssetBalanceDTO> getAssetBalances() {
		return assetBalances;
	}

	public void setAssetBalances(List<AssetBalanceDTO> assetBalances) {
		this.assetBalances = assetBalances;
	}

	public List<FrozenBalanceDTO> getFrozenBalances() {
		return frozenBalances;
	}

	public void setFrozenBalances(List<FrozenBalanceDTO> frozenBalances) {
		this.frozenBalances = frozenBalances;
	}

	public List<VoteDTO> getVotes() {
		return votes;
	}

	public void setVotes(List<VoteDTO> votes) {
		this.votes = votes;
	}

	public long getVoteReceived() {
		return voteReceived;
	}

	public void setVoteReceived(long voteReceived) {
		this.voteReceived = voteReceived;
	}

	public long getAssetCount() {
		return assetCount;
	}

	public void setAssetCount(long assetCount) {
		this.assetCount = assetCount;
	}

	public long getVoteSpent() {
		return voteSpent;
	}

	public void setVoteSpent(long voteSpent) {
		this.voteSpent = voteSpent;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	
	public String getPercentage() {
		return percentage;
	}
	
	public void setWitness(WitnessDTO witness) {
		this.witness = witness;
	}
	
	public WitnessDTO getWitness() {
		return witness;
	}
	
	public void setCriteria(AccountDetailCriteriaDTO criteria) {
		this.criteria = criteria;
	}
	
	public AccountDetailCriteriaDTO getCriteria() {
		return criteria;
	}

	public List<AssetIssueDTO> getAssetIssues() {
		return assetIssues;
	}

	public void setAssetIssues(List<AssetIssueDTO> assetIssues) {
		this.assetIssues = assetIssues;
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

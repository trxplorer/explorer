package io.trxplorer.service.dto.asset;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import io.trxplorer.service.dto.transaction.TransactionModel;
import io.trxplorer.service.utils.TransactionHelper;

public class AssetIssueDTO {
	
	private long id;
	
	private String name;
	
	private String abbr;
	
	private String shortName;
	
	private long totalSupply;
	
	private long trxNum;
	
	private long num;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private int decayRatio;
	
	private long voteScore;
	
	private String description;
	
	private String url;
	
	private String txHash;
	
	private String issuer;
	
	private int totalParticipants;
	
	private List<TransactionModel> transactions;
	
	private List<AssetParticipationDTO> participations;
	
	private TokenCriteria criteria;
	
	private boolean violation;
	

	public boolean isViolation() {
		return violation;
	}

	public void setViolation(boolean hideData) {
		this.violation = hideData;
	}

	public String getAbbr() {
		return abbr;
	}
	
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	
	public TokenCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TokenCriteria criteria) {
		this.criteria = criteria;
	}

	public List<AssetParticipationDTO> getParticipations() {
		return participations;
	}

	public void setParticipations(List<AssetParticipationDTO> participations) {
		this.participations = participations;
	}

	public List<TransactionModel> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionModel> transactions) {
		this.transactions = transactions;
	}

	public String getShortName() {
		return shortName;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public int getTotalParticipants() {
		return totalParticipants;
	}
	
	public void setTotalParticipants(int totalParticipants) {
		this.totalParticipants = totalParticipants;
	}
	
	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTotalSupply() {
		return totalSupply;
	}

	public void setTotalSupply(long totalSupply) {
		this.totalSupply = totalSupply;
	}

	public long getTrxNum() {
		return trxNum;
	}

	public void setTrxNum(long trxNum) {
		this.trxNum = trxNum;
	}

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public int getDecayRatio() {
		return decayRatio;
	}

	public void setDecayRatio(int decayRatio) {
		this.decayRatio = decayRatio;
	}

	public long getVoteScore() {
		return voteScore;
	}

	public void setVoteScore(long voteScore) {
		this.voteScore = voteScore;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTxHash() {
		return txHash;
	}

	public void setTxHash(String txHash) {
		this.txHash = txHash;
	}

	public String getTotalSupplyStr() {
		return NumberFormat.getNumberInstance(Locale.US).format(totalSupply);
	}
	
	public String getTrxNumStr() {
		return TransactionHelper.getTrxAmount(trxNum);
	}
	
	public String getNumStr() {
		return NumberFormat.getNumberInstance(Locale.US).format(num);
	}
	
	
}

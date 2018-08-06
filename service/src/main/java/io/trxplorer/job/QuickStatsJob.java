package io.trxplorer.job;

import static io.trxplorer.model.Tables.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;

import org.jooby.quartz.Scheduled;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.model.tables.pojos.Market;
import io.trxplorer.model.tables.pojos.VotingRound;
import io.trxplorer.service.utils.TransactionHelper;
import io.trxplorer.troncli.TronFullNodeCli;

@Singleton
@DisallowConcurrentExecution
public class QuickStatsJob {

	
	private DSLContext dslContext;
	//blocks
	private Long totalBlocks;
	private Integer blocks24h;
	private Integer blocks6h;
	private Long totalBlocksConfirmed;
	//tx
	private Long totalTx;
	private Long tx24h;
	private Long tx6h;
	private Long totalTxConfirmed;
	//reps
	private Integer totalRepresentative;
	private Integer totalRepresentative24h;
	private HashMap<String,Object> bestRep6h;
	private HashMap<String,Object> bestRepAll;
	//accounts
	private Long totalAccounts;
	private Long totalAccounts24h;
	private Long totalAccountBalance;
	private int totalNodesUp;
	private Integer totalNodes24h;
	private int totalNodesDiscovered;
	
	private HashMap<String,Object> topNodeCountry;
	private Integer totalTokens;
	private Integer totalTokens24h;
	
	private HashMap<String,Object> marketData;
	
	private HashMap<String,Object> votes;
	
	
	//vote round
	private VotingRound currentVotingRound;
	
	private TronFullNodeCli fullNodeCli;
	
	@Inject
	public QuickStatsJob(DSLContext dslContext) {
		this.dslContext = dslContext;
	}
	
	
	@Scheduled("1m")
	public void generateStats() {
		
		LocalDateTime last24hDate = LocalDateTime.now().minusDays(1);
		LocalDateTime last6hDate = LocalDateTime.now().minusHours(6);
		
		//Total blocks
		this.totalBlocks = this.dslContext.select(DSL.count())
				.from(BLOCK)
				.fetchOneInto(Long.class);
		
		//Blocks 24h
		this.blocks24h = this.dslContext.select(DSL.count())
		.from(BLOCK)
		.where(BLOCK.TIMESTAMP.gt(Timestamp.valueOf(last24hDate)))
		.fetchOneInto(Integer.class);

		this.blocks6h = this.dslContext.select(DSL.count())
		.from(BLOCK)
		.where(BLOCK.TIMESTAMP.gt(Timestamp.valueOf(last6hDate)))
		.fetchOneInto(Integer.class);
		
		//Blocks confirmed
		this.totalBlocksConfirmed = this.dslContext.select(DSL.count())
				.from(BLOCK)
				.where(BLOCK.CONFIRMED.eq(Byte.valueOf((byte)1)))
				.fetchOneInto(Long.class);
		
		
		
		//Tx total
		this.totalTx = this.dslContext.select(DSL.count())
				.from(TRANSACTION)
				.fetchOneInto(Long.class);		
	
		//Tx 24h
		this.tx24h = this.dslContext.select(DSL.count())
		.from(TRANSACTION)
		.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID))
		.where(BLOCK.TIMESTAMP.gt(Timestamp.valueOf(last24hDate)))
		.fetchOneInto(Long.class);		

		//Tx 6h
		this.tx6h = this.dslContext.select(DSL.count())
		.from(TRANSACTION)
		.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID))
		.where(BLOCK.TIMESTAMP.gt(Timestamp.valueOf(last6hDate)))
		.fetchOneInto(Long.class);		
		
		//TX confirmed
		this.totalTxConfirmed = this.dslContext.select(DSL.count())
				.from(TRANSACTION)
				.where(TRANSACTION.CONFIRMED.eq(Byte.valueOf((byte)1)))
				.fetchOneInto(Long.class);
		
		//Repesentative total
		this.totalRepresentative = this.dslContext.select(DSL.count())
				.from(WITNESS)
				.fetchOneInto(Integer.class);		
		
		//Representative 24h
		this.totalRepresentative24h = this.dslContext.select(DSL.count())
		.from(WITNESS)
		.join(ACCOUNT).on(WITNESS.ADDRESS.eq(ACCOUNT.ADDRESS))
		.where(ACCOUNT.CREATE_TIME.gt(Timestamp.valueOf(last24hDate)))
		.fetchOneInto(Integer.class);		
		
		//Representative best 6h
		Record2<String, Integer> repBest6h = this.dslContext.select(BLOCK.WITNESS_ADDRESS,DSL.count().divide(blocks6h).multiply(100).as("count"))
		.from(BLOCK)
		.where(BLOCK.TIMESTAMP.gt(Timestamp.valueOf(last6hDate)))
		.groupBy(BLOCK.WITNESS_ADDRESS)
		.orderBy(DSL.field("count").desc())
		.limit(1)
		.fetchOne();
		;

		this.bestRep6h = new HashMap<>();
		if (repBest6h!=null) {
			this.bestRep6h.put("address", repBest6h.get(0));
			this.bestRep6h.put("percentage", repBest6h.get(1));			
		}


		//Representative best all
		Record2<String, Integer> repBestAll = this.dslContext.select(BLOCK.WITNESS_ADDRESS,DSL.count().divide(totalBlocks).multiply(100).as("count"))
		.from(BLOCK)
		.groupBy(BLOCK.WITNESS_ADDRESS)
		.orderBy(DSL.field("count").desc())
		.limit(1)
		.fetchOne();
		;
		
		this.bestRepAll = new HashMap<>();
		this.bestRepAll.put("address", repBestAll.get(0));
		this.bestRepAll.put("percentage", repBestAll.get(1));
		
		//Total Accounts
		this.totalAccounts = this.dslContext.select(DSL.count())
		.from(ACCOUNT)
		.fetchOneInto(Long.class);	
		
		//Total accounts 24h
		this.totalAccounts24h = this.dslContext.select(DSL.count())
		.from(ACCOUNT)
		.where(ACCOUNT.CREATE_TIME.gt(Timestamp.valueOf(last24hDate)))
		.fetchOneInto(Long.class);	
		
		//Total account balance
		this.totalAccountBalance = this.dslContext.select(DSL.sum(ACCOUNT.BALANCE))
		.from(ACCOUNT)
		.where(ACCOUNT.BALANCE.gt(0l))
		.fetchOneInto(Long.class);
		
		//Total up nodes
		this.totalNodesUp = this.dslContext.select(DSL.count())
		.from(NODE)
		.where(NODE.UP.eq((byte)1))
		.fetchOneInto(Integer.class);		
		
		//Total nodes created 24h
		this.totalNodes24h = this.dslContext.select(DSL.count())
		.from(NODE)
		.where(NODE.UP.eq((byte)1))
		.and(NODE.DATE_CREATED.gt(Timestamp.valueOf(last24hDate)))
		.fetchOneInto(Integer.class);	
		
		Record2<String, Integer> topCountry = this.dslContext.select(NODE.COUNTRY,DSL.count().as("count"))
		.from(NODE)
		.where(NODE.UP.eq((byte)1))
		.groupBy(NODE.COUNTRY)
		.orderBy(DSL.field("count").desc())
		.limit(1)
		.fetchOne();
		
		this.topNodeCountry = new HashMap<>();
		
		this.topNodeCountry.put("country",topCountry.get(0));
		this.topNodeCountry.put("nodes", topCountry.get(1));
		
		//total nodes discovered
		this.totalNodesDiscovered = this.dslContext.select(DSL.count()).from(NODE).fetchOneInto(Integer.class);
		
		//Total tokens
		this.totalTokens = this.dslContext.select(DSL.count())
		.from(CONTRACT_ASSET_ISSUE)
		.fetchOneInto(Integer.class);	
		
		//Total tokens 24h
		this.totalTokens24h = this.dslContext.select(DSL.count())
		.from(CONTRACT_ASSET_ISSUE)
		.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_ASSET_ISSUE.TRANSACTION_ID))
		.and(TRANSACTION.TIMESTAMP.gt(Timestamp.valueOf(last24hDate)))
		.fetchOneInto(Integer.class);		
		
		//Market data
		int day = LocalDateTime.now().getDayOfMonth();
		int month = LocalDateTime.now().getDayOfMonth();
		int year = LocalDateTime.now().getYear();
		
		Record2<BigDecimal, UInteger> marketData = this.dslContext.select(MARKET.PRICE,MARKET.VOLUME_24H)
		.from(MARKET)
		.where(MARKET.DAY.eq(day))
		.and(MARKET.MONTH.eq(month))
		.and(MARKET.YEAR.eq(year))
		.and(MARKET.SOURCE.eq("CMC"))
		.orderBy(MARKET.LAST_UPDATE.desc())
		.limit(1)
		.fetchOne();

		this.marketData = new HashMap<>();
		if (marketData!=null) {

			this.marketData.put("price", marketData.get(0));
			this.marketData.put("volume",  NumberFormat.getNumberInstance(Locale.US).format(marketData.get(1)));			
		}

		

		int totalMarkets = this.dslContext.select(DSL.count())
		.from(DSL.select()
				.from(MARKET)
				.where(MARKET.SOURCE.ne("CMC"))
				.groupBy(MARKET.SOURCE).asTable())
		.fetchOneInto(Integer.class);
		

		this.marketData.put("totalMarkets", totalMarkets);			


		
		//Votes

		
		Integer last24hVotesCount = this.dslContext.select(DSL.count())
		.from(TRANSACTION)
		.join(CONTRACT_VOTE_WITNESS).on(CONTRACT_VOTE_WITNESS.TRANSACTION_ID.eq(TRANSACTION.ID))
		.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID))
		.where(BLOCK.TIMESTAMP.gt(Timestamp.valueOf(last24hDate)))
		.fetchOneInto(Integer.class);
		
		this.votes = new HashMap<>();
		
		this.votes.put("last24hCount", last24hVotesCount);
		
		
		//Vote round
		Integer currentRound = this.dslContext.select(DSL.max(VOTING_ROUND.ROUND)).from(VOTING_ROUND).where(VOTING_ROUND.SYNC_END.isNotNull()).fetchOneInto(Integer.class);
		
		this.currentVotingRound = this.dslContext.select(VOTING_ROUND.fields()).from(VOTING_ROUND).orderBy(VOTING_ROUND.ROUND.desc()).limit(1).fetchOneInto(VotingRound.class);
		
	}
	
	public int getTotalNodesDiscovered() {
		return totalNodesDiscovered;
	}
	
	public VotingRound getCurrentVotingRound() {
		return currentVotingRound;
	}
	
	public HashMap<String, Object> getVotes() {
		return votes;
	}
	
	public Integer getTotalTokens() {
		return totalTokens;
	}
	
	public Integer getTotalTokens24h() {
		return totalTokens24h;
	}
	
	public HashMap<String, Object> getMarketData() {
		return marketData;
	}
	
	public HashMap<String, Object> getTopNodeCountry() {
		return topNodeCountry;
	}
	
	public Integer getTotalNodes24h() {
		return totalNodes24h;
	}
	
	public int getTotalNodesUp() {
		return totalNodesUp;
	}
	
	public Long getTotalAccounts24h() {
		return totalAccounts24h;
	}
	
	public Long getTotalAccounts() {
		return totalAccounts;
	}
	
	public String getTotalAccountBalance() {
		return TransactionHelper.getTrxAmount(totalAccountBalance);
	}
	
	public HashMap<String, Object> getBestRep6h() {
		return bestRep6h;
	}
	
	public HashMap<String, Object> getBestRepAll() {
		return bestRepAll;
	}
	
	public Integer getTotalRepresentative() {
		return totalRepresentative;
	}
	
	public Integer getTotalRepresentative24h() {
		return totalRepresentative24h;
	}
	
	public Long getTx6h() {
		return tx6h;
	}
	
	public Long getTx24h() {
		return tx24h;
	}
	
	public Long getTotalTx() {
		return totalTx;
	}
	
	public Long getTotalTxConfirmed() {
		return totalTxConfirmed;
	}
	
	public long getTotalBlocks() {
		return totalBlocks;
	}
	
	public Integer getBlocks24h() {
		return blocks24h;
	}
	
	public Integer getBlocks6h() {
		return blocks6h;
	}
	
	public Long getTotalBlocksConfirmed() {
		return totalBlocksConfirmed;
	}
}

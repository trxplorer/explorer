package io.trxplorer.syncnode.job;
import static io.trxplorer.model.Tables.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jooby.quartz.Scheduled;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSeekStep1;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.model.tables.VoteLive;
import io.trxplorer.model.tables.VotingRound;
import io.trxplorer.model.tables.VotingRoundStats;
import io.trxplorer.model.tables.Witness;
import io.trxplorer.model.tables.records.VotingRoundRecord;
import io.trxplorer.syncnode.SyncNodeConfig;
import io.trxplorer.troncli.TronFullNodeCli;

@Singleton
@DisallowConcurrentExecution
public class VotingRoundJob {

	private DSLContext dslContext;
	private TronFullNodeCli fullNodeCli;
	
	private HashMap<String,Long> genesisVotes;
	private SyncNodeConfig config;
	
	{
		genesisVotes = new HashMap<>();
		genesisVotes.put("THKJYuUmMKKARNf7s2VT51g5uPY6KEqnat", 100000026l);
		genesisVotes.put("TVDmPWGYxgi5DNeW8hXrzrhY8Y6zgxPNg4", 100000025l);
		genesisVotes.put("TWKZN1JJPFydd5rMgMCV5aZTSiwmoksSZv", 100000024l);
		genesisVotes.put("TDarXEG2rAD57oa7JTK785Yb2Et32UzY32", 100000023l);
		genesisVotes.put("TAmFfS4Tmm8yKeoqZN8x51ASwdQBdnVizt", 100000022l);
		genesisVotes.put("TK6V5Pw2UWQWpySnZyCDZaAvu1y48oRgXN", 100000021l);
		genesisVotes.put("TGqFJPFiEqdZx52ZR4QcKHz4Zr3QXA24VL", 100000020l);
		genesisVotes.put("TC1ZCj9Ne3j5v3TLx5ZCDLD55MU9g3XqQW", 100000019l);
		genesisVotes.put("TWm3id3mrQ42guf7c4oVpYExyTYnEGy3JL", 100000018l);
		genesisVotes.put("TCvwc3FV3ssq2rD82rMmjhT4PVXYTsFcKV", 100000017l);
		genesisVotes.put("TFuC2Qge4GxA2U9abKxk1pw3YZvGM5XRir", 100000016l);
		genesisVotes.put("TNGoca1VHC6Y5Jd2B1VFpFEhizVk92Rz85", 100000015l);
		genesisVotes.put("TLCjmH6SqGK8twZ9XrBDWpBbfyvEXihhNS", 100000014l);
		genesisVotes.put("TEEzguTtCihbRPfjf1CvW8Euxz1kKuvtR9", 100000013l);
		genesisVotes.put("TZHvwiw9cehbMxrtTbmAexm9oPo4eFFvLS", 100000012l);
		genesisVotes.put("TGK6iAKgBmHeQyp5hn3imB71EDnFPkXiPR", 100000011l);
		genesisVotes.put("TLaqfGrxZ3dykAFps7M2B4gETTX1yixPgN", 100000010l);
		genesisVotes.put("TX3ZceVew6yLC5hWTXnjrUFtiFfUDGKGty", 100000009l);
		genesisVotes.put("TYednHaV9zXpnPchSywVpnseQxY9Pxw4do", 100000008l);
		genesisVotes.put("TCf5cqLffPccEY7hcsabiFnMfdipfyryvr", 100000007l);
		genesisVotes.put("TAa14iLEKPAetX49mzaxZmH6saRxcX7dT5", 100000006l);
		genesisVotes.put("TBYsHxDmFaRmfCF3jZNmgeJE8sDnTNKHbz", 100000005l);
		genesisVotes.put("TEVAq8dmSQyTYK7uP1ZnZpa6MBVR83GsV6", 100000004l);
		genesisVotes.put("TRKJzrZxN34YyB8aBqqPDt7g4fv6sieemz", 100000003l);
		genesisVotes.put("TRMP6SKeFUt5NtMLzJv8kdpYuHRnEGjGfe", 100000002l);
		genesisVotes.put("TDbNE1VajxjpgM5p7FyGNDASt3UVoFbiD3", 100000001l);
		genesisVotes.put("TLTDZBcPoJ8tZ6TTEeEqEvwYFk2wgotSfD", 100000000l);
		
		
	}
	
	@Inject
	public VotingRoundJob(DSLContext dslContext,TronFullNodeCli fullNodeCli,SyncNodeConfig config) {
		this.dslContext = dslContext;
		this.fullNodeCli = fullNodeCli;
		this.config = config;
	}

	@Scheduled("30s")
	public void buildLiveVotes() {
		
		if (!this.config.isVoteJobEnabled()) {
			return;
		}
		
		this.dslContext.truncate(VOTE_LIVE).execute();
		
		Witness w = WITNESS.as("w");
		
		Field<Long> voteCount = DSL.select(DSL.sum(ACCOUNT_VOTE.VOTE_COUNT)).from(ACCOUNT_VOTE).where(ACCOUNT_VOTE.VOTE_ADDRESS.eq(w.ADDRESS)).asField().cast(Long.class);
		
		SelectJoinStep<Record2<String, Long>> table = DSL.select(w.ADDRESS,voteCount).from(w);
		
		this.dslContext.insertInto(VOTE_LIVE).columns(VOTE_LIVE.ADDRESS,VOTE_LIVE.VOTE_COUNT)
		.select(table).execute();
		
		for(String address:genesisVotes.keySet()) {
			this.dslContext.update(VOTE_LIVE)
			.set(VOTE_LIVE.VOTE_COUNT,VOTE_LIVE.VOTE_COUNT.plus(genesisVotes.get(address)))
			.where(VOTE_LIVE.ADDRESS.eq(address))
			.execute();			
		}
		
		
		SelectSeekStep1<Record2<String, Integer>, Long> table2 = DSL.select(VOTE_LIVE.ADDRESS,DSL.field("@rownum:=@rownum+1",Integer.class).as("position"))
		.from(VOTE_LIVE)
		.crossJoin("(select @rownum:=0) tmp")
		.orderBy(VOTE_LIVE.VOTE_COUNT.desc())
		;
		
		VoteLive vl = VOTE_LIVE.as("vl");
		
		SelectConditionStep<Record1<Integer>> t3 = DSL.select(DSL.field("position",Integer.class)).from(table2).where(table2.field("address", String.class).eq(vl.ADDRESS));
		
		this.dslContext.update(vl)
		.set(vl.POSITION,t3)
		.execute();
		
		
		//update stats from previous round
		io.trxplorer.model.tables.pojos.VotingRound round = this.dslContext.select(VOTING_ROUND.fields()).from(VOTING_ROUND).orderBy(VOTING_ROUND.ROUND.desc()).limit(1).fetchOneInto(io.trxplorer.model.tables.pojos.VotingRound.class);
		
		int previousRound = round.getRound().intValue()-1;
		
		
		if (previousRound>0) {
			
			Table<Record3<Integer, Long, String>> vlPositionTable = DSL.select(VOTING_ROUND_STATS.POSITION.cast(Integer.class).minus(VOTE_LIVE.POSITION).as("position"),
					VOTE_LIVE.VOTE_COUNT.minus(VOTING_ROUND_STATS.VOTE_COUNT.cast(Long.class)).as("votes"),
					VOTE_LIVE.ADDRESS)
			.from(VOTING_ROUND_STATS,VOTE_LIVE)
			.where(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(DSL.select(VOTING_ROUND.ID)
					.from(VOTING_ROUND)
					.where(VOTING_ROUND.ROUND.eq(UInteger.valueOf(previousRound)))
					
					)
					.and(VOTING_ROUND_STATS.ADDRESS.eq(VOTE_LIVE.ADDRESS)
					)).asTable("tmp");
			
			this.dslContext.update(vl)
			.set(vl.POSITION_CHANGE,DSL.select(vlPositionTable.field("position",Integer.class)).from(vlPositionTable).where(vlPositionTable.field("address", String.class).eq(vl.ADDRESS)))
			.set(vl.VOTE_CHANGE,DSL.select(vlPositionTable.field("votes",Long.class)).from(vlPositionTable).where(vlPositionTable.field("address", String.class).eq(vl.ADDRESS)))
			.execute();
			;	
			
		}
	
		
		
	}
	
	@Scheduled("1m")
	public void createMissingVotingRounds() {
		
		Timestamp lastEndDate = this.dslContext.select(DSL.max(VOTING_ROUND.END_DATE)).from(VOTING_ROUND).fetchOneInto(Timestamp.class);
		
		Instant instant = Instant.ofEpochMilli(this.fullNodeCli.getNextMaintenanceTime());
		
		LocalDateTime maintenanceTime = instant.atZone(ZoneOffset.UTC).toLocalDateTime();
		
		List<VotingRoundRecord> records = new ArrayList<>();
		
		Timestamp firstBlockTS = this.dslContext.select(DSL.max(VOTING_ROUND.START_DATE)).from(VOTING_ROUND).fetchOneInto(Timestamp.class);
		
		//We don't have any data yet, create all missing rounds
		if (lastEndDate==null) {
			
			//Use first block timestamp as initial round
			firstBlockTS = this.dslContext.select(BLOCK.TIMESTAMP).from(BLOCK).where(BLOCK.NUM.eq(ULong.valueOf(1))).fetchOneInto(Timestamp.class);
						
		}
		
		//already up to date: skip
		if (lastEndDate!=null && lastEndDate.equals(Timestamp.valueOf(maintenanceTime))) {
			return;
		}
		
		LocalDateTime firstBlockLdt = firstBlockTS.toLocalDateTime();
		
		while(maintenanceTime.isAfter(firstBlockLdt)) {
			
			LocalDateTime startTime = maintenanceTime.minusHours(6);
			

			
			VotingRoundRecord vr = new VotingRoundRecord();
			vr.setMonth(UInteger.valueOf(startTime.getMonthValue()));
			vr.setDay(UInteger.valueOf(startTime.getDayOfMonth()));
			vr.setYear(UInteger.valueOf(startTime.getYear()));
			vr.setEndDate(Timestamp.valueOf(maintenanceTime));
			vr.setStartDate(Timestamp.valueOf(startTime));
			
			maintenanceTime = startTime;

			if (Timestamp.valueOf(startTime).equals(firstBlockTS)) {
				continue;
			}else {
				records.add(vr);	
			}
			
		}
		

		this.dslContext.batchInsert(records).execute();	
			
		VotingRound vr = VOTING_ROUND.as("vr");
		
		Table<Record2<Object, UInteger>> tmp = DSL.select(DSL.field("@rownum := @rownum+1").as("round"),VOTING_ROUND.ID).from(VOTING_ROUND).crossJoin("(select @rownum:=0) tmp").orderBy(VOTING_ROUND.END_DATE.asc()).asTable("tmp");
		
		this.dslContext.update(vr)
		.set(vr.ROUND,DSL.select(DSL.field("round",UInteger.class)).from(tmp).where(vr.ID.eq(tmp.field("id",UInteger.class))))
		.execute();					

		
	}
	
	@Scheduled("1m")
	public void buildVoteRounds() {
		
		if (!this.config.isVoteJobEnabled()) {
			return;
		}
		
		List<VotingRoundRecord> rounds = this.dslContext.select(VOTING_ROUND.fields())
		.from(VOTING_ROUND)
		.where(VOTING_ROUND.SYNC_END.isNull())
		.and(VOTING_ROUND.END_DATE.lt(DSL.select(DSL.max(BLOCK.TIMESTAMP)).from(BLOCK)))
		.orderBy(VOTING_ROUND.START_DATE.asc())
		.fetchInto(VotingRoundRecord.class);
		
		if (rounds.size()>0) {

			//build vote mview
			this.dslContext.truncate(CONTRACT_VOTE_WITNESS_MVIEW).execute();
			this.dslContext.insertInto(CONTRACT_VOTE_WITNESS_MVIEW).select(DSL.select().from(CONTRACT_VOTE_WITNESS)).execute();

			
			for(VotingRoundRecord round:rounds) {
				
				this.computeRoundVotes(round);
				
			}

			this.dslContext.truncate(CONTRACT_VOTE_WITNESS_MVIEW).execute();
			
		}

	}
	
	
	private void computeRoundVotes(VotingRoundRecord round) {
		
		
		this.dslContext.update(VOTING_ROUND)
		.set(VOTING_ROUND.SYNC_START,Timestamp.valueOf(LocalDateTime.now()))
		.where(VOTING_ROUND.ID.eq(round.getId()))
		.execute();


		List<String> addresses = this.dslContext.select(WITNESS.ADDRESS).from(WITNESS,ACCOUNT)
				.where(WITNESS.ACCOUNT_ID.eq(ACCOUNT.ID).and(ACCOUNT.CREATE_TIME.lt(round.getEndDate()))).fetchInto(String.class); 
		
		
		this.dslContext.deleteFrom(VOTING_ROUND_VOTE)
		.where(VOTING_ROUND_VOTE.VOTING_ROUND_ID.eq(round.getId()))
		.execute();
		
		this.dslContext.deleteFrom(VOTING_ROUND_VOTE_LOST)
		.where(VOTING_ROUND_VOTE_LOST.VOTING_ROUND_ID.eq(round.getId()))
		.execute();
		
		this.dslContext.deleteFrom(VOTING_ROUND_STATS)
		.where(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(round.getId()))
		.execute();
		
		for(String witnessAddress : addresses) {
			
		
			
			//set votes for current round
			
			Table<Record2<String, Timestamp>> lastVotesTable = DSL.select(CONTRACT_VOTE_WITNESS_MVIEW.OWNER_ADDRESS,DSL.max(BLOCK.TIMESTAMP).as("timestamp"))
			.from(CONTRACT_VOTE_WITNESS_MVIEW)
			.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_VOTE_WITNESS_MVIEW.TRANSACTION_ID))
			.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID).and(BLOCK.TIMESTAMP.lt(round.getEndDate())))
			.groupBy(CONTRACT_VOTE_WITNESS_MVIEW.OWNER_ADDRESS)
			.asTable();
			
			
			 Table<Record5<UInteger, String, String, ULong, Timestamp>> latestVotesIdsTable = DSL.select(DSL.val(round.getId()).as("roundId"),CONTRACT_VOTE_WITNESS_MVIEW.OWNER_ADDRESS,CONTRACT_VOTE_WITNESS_MVIEW.VOTE_ADDRESS,CONTRACT_VOTE_WITNESS_MVIEW.VOTE_COUNT,BLOCK.TIMESTAMP)
			.from(CONTRACT_VOTE_WITNESS_MVIEW)
			.join(TRANSACTION).on(CONTRACT_VOTE_WITNESS_MVIEW.TRANSACTION_ID.eq(TRANSACTION.ID))
			.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID))
			.join(lastVotesTable).on(CONTRACT_VOTE_WITNESS_MVIEW.OWNER_ADDRESS.eq(lastVotesTable.field(0, String.class))).and(BLOCK.TIMESTAMP.eq(lastVotesTable.field(1, Timestamp.class)))
			.where(CONTRACT_VOTE_WITNESS_MVIEW.VOTE_ADDRESS.eq(witnessAddress))
			.asTable("t1");
			
			this.dslContext.insertInto(VOTING_ROUND_VOTE)
			.columns(VOTING_ROUND_VOTE.VOTING_ROUND_ID,VOTING_ROUND_VOTE.OWNER_ADDRESS,VOTING_ROUND_VOTE.VOTE_ADDRESS,VOTING_ROUND_VOTE.VOTE_COUNT,VOTING_ROUND_VOTE.TIMESTAMP)
			.select(DSL.select(latestVotesIdsTable.field(0, UInteger.class),latestVotesIdsTable.field(1,String.class),latestVotesIdsTable.field(2,String.class),latestVotesIdsTable.field(3,Long.class),latestVotesIdsTable.field(4,Timestamp.class))
					.from(latestVotesIdsTable))
			.execute();
			
			
			//add vote lost for current round
			Table<Record2<String, Timestamp>> lostVotesTable = DSL.select(CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS,BLOCK.TIMESTAMP)
			.from(CONTRACT_UNFREEZE_BALANCE)
			.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_UNFREEZE_BALANCE.TRANSACTION_ID))
			.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID).and(BLOCK.TIMESTAMP.lt(round.getEndDate())))
			.asTable();
			
			
			this.dslContext.insertInto(VOTING_ROUND_VOTE_LOST)
			.columns(VOTING_ROUND_VOTE.VOTING_ROUND_ID,VOTING_ROUND_VOTE.OWNER_ADDRESS,VOTING_ROUND_VOTE.VOTE_ADDRESS,VOTING_ROUND_VOTE.VOTE_COUNT,VOTING_ROUND_VOTE.TIMESTAMP)		
			.select(	
			DSL.select(VOTING_ROUND_VOTE.VOTING_ROUND_ID,VOTING_ROUND_VOTE.OWNER_ADDRESS,VOTING_ROUND_VOTE.VOTE_ADDRESS,VOTING_ROUND_VOTE.VOTE_COUNT,VOTING_ROUND_VOTE.TIMESTAMP)
			.from(VOTING_ROUND_VOTE)
			.join(lostVotesTable).on(VOTING_ROUND_VOTE.OWNER_ADDRESS.eq(lostVotesTable.field(0,String.class)))
			.where(VOTING_ROUND_VOTE.TIMESTAMP.lt(lostVotesTable.field(1,Timestamp.class))
			.and(VOTING_ROUND_VOTE.VOTE_ADDRESS.eq(witnessAddress))))
			.execute()
			;
			
			//delete lost votes from round votes
			Table<Record1<ULong>> vrvd = DSL.select(VOTING_ROUND_VOTE.ID)
			.from(VOTING_ROUND_VOTE)
			.join(lostVotesTable).on(VOTING_ROUND_VOTE.OWNER_ADDRESS.eq(lostVotesTable.field(0,String.class)))
			.where(VOTING_ROUND_VOTE.TIMESTAMP.lt(lostVotesTable.field(1,Timestamp.class))
			.and(VOTING_ROUND_VOTE.VOTE_ADDRESS.eq(witnessAddress))).asTable();
			
			//DSL.select(DSL.field(0,ULong.class));
			
			this.dslContext.deleteFrom(VOTING_ROUND_VOTE)
			.where(VOTING_ROUND_VOTE.ID.in(DSL.select(vrvd.field(0,ULong.class)).from(vrvd)))
			.execute();
					

			//build stats for current round/witness
			this.dslContext.insertInto(VOTING_ROUND_STATS)
			.set(VOTING_ROUND_STATS.ADDRESS,witnessAddress)
			.set(VOTING_ROUND_STATS.VOTING_ROUND_ID,round.getId())
			.set(VOTING_ROUND_STATS.VOTE_COUNT,DSL.select(DSL.sum(VOTING_ROUND_VOTE.VOTE_COUNT).cast(ULong.class))
					.from(VOTING_ROUND_VOTE)
					.where(VOTING_ROUND_VOTE.VOTING_ROUND_ID.eq(round.getId()))
					.and(VOTING_ROUND_VOTE.VOTE_ADDRESS.eq(witnessAddress)))
			.set(VOTING_ROUND_STATS.VOTE_LOST_COUNT,DSL.select(DSL.sum(VOTING_ROUND_VOTE_LOST.VOTE_COUNT).cast(ULong.class))
					.from(VOTING_ROUND_VOTE_LOST)
					.where(VOTING_ROUND_VOTE_LOST.VOTING_ROUND_ID.eq(round.getId()))
					.and(VOTING_ROUND_VOTE_LOST.VOTE_ADDRESS.eq(witnessAddress)))			
			.execute();

		}
		
		
		//update round vote count
		SelectConditionStep<Record1<ULong>> totalRoundVoteCount = DSL.select(DSL.sum(VOTING_ROUND_STATS.VOTE_COUNT).cast(ULong.class)).from(VOTING_ROUND_STATS).where(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(round.getId()));
		this.dslContext.update(VOTING_ROUND)
		.set(VOTING_ROUND.VOTE_COUNT,totalRoundVoteCount)
		.where(VOTING_ROUND.ID.eq(round.getId()))
		.execute();
		
	
		
		
		//update genesis witnesses for current round
		//Done after previous update on total round votes on purpose: we don't want to count those "fake" genesis votes
		for(String address:genesisVotes.keySet()) {
			//create witness in round if no votes associated
			 ULong addressVoteCount = this.dslContext.select(VOTING_ROUND_STATS.VOTE_COUNT)
			.from(VOTING_ROUND_STATS)
			.where(VOTING_ROUND_STATS.ADDRESS.eq(address))
			.and(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(round.getId()))
			.fetchOneInto(ULong.class);
			
			if (addressVoteCount==null) {
				this.dslContext.update(VOTING_ROUND_STATS)
				.set(VOTING_ROUND_STATS.VOTE_COUNT,ULong.valueOf(0))
				.where(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(round.getId()))
				.and(VOTING_ROUND_STATS.ADDRESS.eq(address))
				.execute();
			}
			
			
			this.dslContext.update(VOTING_ROUND_STATS)
			.set(VOTING_ROUND_STATS.VOTE_COUNT,VOTING_ROUND_STATS.VOTE_COUNT.plus(genesisVotes.get(address)))
			.where(VOTING_ROUND_STATS.ADDRESS.eq(address))
			.and(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(round.getId()))
			.execute();			
		}
		
		//update witnesses positions in current round
		VotingRoundStats vrs = VOTING_ROUND_STATS.as("vrs");
		Table<Record2<Object, UInteger>> tmp = DSL.select(DSL.field("@rownum := @rownum+1").as("position"),VOTING_ROUND_STATS.ID)
				.from(VOTING_ROUND_STATS).crossJoin("(select @rownum:=0) tmp")
				.where(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(round.getId()))
				.orderBy(VOTING_ROUND_STATS.VOTE_COUNT.desc()).asTable("tmp");
		
		this.dslContext.update(vrs)
		.set(vrs.POSITION,DSL.select(DSL.field("position",UInteger.class)).from(tmp).where(vrs.ID.eq(tmp.field("id",UInteger.class))))
		.where(vrs.VOTING_ROUND_ID.eq(round.getId()))
		.execute();			
		
		
		//update stats from previous round
		int previousRound = round.getRound().intValue()-1;
		if (previousRound>0) {
			
			VotingRoundStats vrs1 = VOTING_ROUND_STATS.as("vrs1");
			VotingRoundStats vrs2 = VOTING_ROUND_STATS.as("vrs2");
			Table<Record3<Integer, Long, String>> vrsChangeTable = DSL.select(vrs1.POSITION.cast(Integer.class).minus(vrs2.POSITION.cast(Integer.class)).as("position"),
					vrs2.VOTE_COUNT.cast(Long.class).minus(vrs1.VOTE_COUNT.cast(Long.class)).as("votes"),
					vrs1.ADDRESS)
			.from(vrs1,vrs2)
			.where(vrs1.VOTING_ROUND_ID.eq(DSL.select(VOTING_ROUND.ID)
					.from(VOTING_ROUND)
					.where(VOTING_ROUND.ROUND.eq(UInteger.valueOf(previousRound))))
					.and(vrs1.ADDRESS.eq(vrs2.ADDRESS))
					.and(vrs2.VOTING_ROUND_ID.eq(round.getId()))
					).asTable("tmp");
			
			this.dslContext.update(VOTING_ROUND_STATS)
			.set(VOTING_ROUND_STATS.POSITION_CHANGE,DSL.select(vrsChangeTable.field("position",Integer.class)).from(vrsChangeTable).where(vrsChangeTable.field("address", String.class).eq(VOTING_ROUND_STATS.ADDRESS)))
			.set(VOTING_ROUND_STATS.VOTES_CHANGE,DSL.select(vrsChangeTable.field("votes",Long.class)).from(vrsChangeTable).where(vrsChangeTable.field("address", String.class).eq(VOTING_ROUND_STATS.ADDRESS)))
			.where(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(round.getId()))
			.execute();
			;	
			
			

		}
		
		//mark round sync as completed			
		this.dslContext.update(VOTING_ROUND)
		.set(VOTING_ROUND.SYNC_END,Timestamp.valueOf(LocalDateTime.now()))
		.where(VOTING_ROUND.ID.eq(round.getId()))
		.execute();
			
		
		
		
	}
	
	
}

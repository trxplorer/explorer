package io.trxplorer.syncnode.job;
import static io.trxplorer.model.Tables.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.jooby.quartz.Scheduled;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.model.tables.ContractVoteWitness;
import io.trxplorer.model.tables.VotingRoundVote;
import io.trxplorer.model.tables.records.ContractVoteWitnessRecord;
import io.trxplorer.model.tables.records.VotingRoundRecord;
import io.trxplorer.troncli.TronFullNodeCli;

@Singleton
@DisallowConcurrentExecution
public class VotingRoundJob {

	private DSLContext dslContext;
	private TronFullNodeCli fullNodeCli;

	@Inject
	public VotingRoundJob(DSLContext dslContext,TronFullNodeCli fullNodeCli) {
		this.dslContext = dslContext;
		this.fullNodeCli = fullNodeCli;
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

		
	}
	
	//@Scheduled("10s")
	public void createVoteRound() {
		
		long nextMaintenanceTime = this.fullNodeCli.getNextMaintenanceTime();
		
		
		VotingRoundRecord round = this.dslContext.select(VOTING_ROUND.fields())
		.from(VOTING_ROUND)
		.where(VOTING_ROUND.SYNC_END.isNull())
		.orderBy(VOTING_ROUND.START_DATE.asc())
		.limit(1)
		.fetchOneInto(VotingRoundRecord.class);

		this.dslContext.update(VOTING_ROUND)
		.set(VOTING_ROUND.SYNC_START,Timestamp.valueOf(LocalDateTime.now()))
		.where(VOTING_ROUND.ID.eq(round.getId()))
		.execute();

		
		//set votes for current round

		ContractVoteWitness cvw = CONTRACT_VOTE_WITNESS.as("cvw");
		 Table<Record5<UInteger, String, String, ULong, Timestamp>> latestVotesIdsTable = DSL.select(DSL.val(round.getId()).as("roundId"),cvw.OWNER_ADDRESS,cvw.VOTE_ADDRESS,cvw.VOTE_COUNT,TRANSACTION.TIMESTAMP)
		.from(cvw)
		.join(TRANSACTION).on(cvw.field(CONTRACT_VOTE_WITNESS.TRANSACTION_ID.getName(),ULong.class).eq(TRANSACTION.ID))
		.and(TRANSACTION.TIMESTAMP.lt(round.getEndDate()))
		.and(TRANSACTION.TIMESTAMP.eq(DSL.select(DSL.max(TRANSACTION.TIMESTAMP))
				.from(CONTRACT_VOTE_WITNESS)
				.join(TRANSACTION).on(CONTRACT_VOTE_WITNESS.TRANSACTION_ID.eq(TRANSACTION.ID))
				.and(TRANSACTION.TIMESTAMP.lt(round.getEndDate()))
				.where(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.eq(cvw.field(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.getName(),String.class)))
				)
				
			)
		.asTable("t1");
		
		
		this.dslContext.deleteFrom(VOTING_ROUND_VOTE)
		.where(VOTING_ROUND_VOTE.VOTING_ROUND_ID.eq(round.getId()))
		.execute();
		
		this.dslContext.insertInto(VOTING_ROUND_VOTE)
		.columns(VOTING_ROUND_VOTE.VOTING_ROUND_ID,VOTING_ROUND_VOTE.OWNER_ADDRESS,VOTING_ROUND_VOTE.VOTE_ADDRESS,VOTING_ROUND_VOTE.VOTE_COUNT,VOTING_ROUND_VOTE.TIMESTAMP)
		.select(DSL.select(latestVotesIdsTable.field(0, UInteger.class),latestVotesIdsTable.field(1,String.class),latestVotesIdsTable.field(2,String.class),latestVotesIdsTable.field(3,ULong.class),latestVotesIdsTable.field(4,Timestamp.class))
				.from(latestVotesIdsTable))
		.execute();
		
		
		//set votes lost for current round 
		this.dslContext.deleteFrom(VOTING_ROUND_VOTE_LOST)
		.where(VOTING_ROUND_VOTE_LOST.VOTING_ROUND_ID.eq(round.getId()))
		.execute();
		
		VotingRoundVote vrv = VOTING_ROUND_VOTE.as("vrv");
		
		this.dslContext.insertInto(VOTING_ROUND_VOTE_LOST)
		.columns(VOTING_ROUND_VOTE.VOTING_ROUND_ID,VOTING_ROUND_VOTE.OWNER_ADDRESS,VOTING_ROUND_VOTE.VOTE_ADDRESS,VOTING_ROUND_VOTE.VOTE_COUNT,VOTING_ROUND_VOTE.TIMESTAMP)		
		.select(	
		DSL.select(vrv.VOTING_ROUND_ID,vrv.OWNER_ADDRESS,vrv.VOTE_ADDRESS,vrv.VOTE_COUNT,vrv.TIMESTAMP)
		.from(vrv)
		.where(vrv.OWNER_ADDRESS.in(DSL.select(CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS)
				.from(CONTRACT_UNFREEZE_BALANCE).join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_UNFREEZE_BALANCE.TRANSACTION_ID))
				.where(CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS.eq(vrv.OWNER_ADDRESS))
				.and(TRANSACTION.TIMESTAMP.gt(vrv.TIMESTAMP)
				.and(TRANSACTION.TIMESTAMP.lt(round.getEndDate()))
			))
			.and(vrv.VOTING_ROUND_ID.eq(round.getId()))))
		.execute()
		;
		
		//set stats for current round
//		Table<Record2<String, BigDecimal>> totalVotesPerAddress = DSL.select(CONTRACT_VOTE_WITNESS.VOTE_ADDRESS,DSL.sum(CONTRACT_VOTE_WITNESS.VOTE_COUNT).as("total_votes"))
//		.from(VOTING_ROUND_VOTE)
//		.join(CONTRACT_VOTE_WITNESS).on(CONTRACT_VOTE_WITNESS.ID.eq(VOTING_ROUND_VOTE.CONTRACT_VOTE_ID))
//		.where(VOTING_ROUND_VOTE.VOTING_ROUND_ID.eq(round.getId()))
//		.groupBy(CONTRACT_VOTE_WITNESS.VOTE_ADDRESS)
//		.orderBy(DSL.field("total_votes").desc())
//		.asTable("t1");
//		
//		
//		Table<?> totalVotesPerAddressWithPosition = DSL.select(totalVotesPerAddress.field(CONTRACT_VOTE_WITNESS.VOTE_ADDRESS.getName()),DSL.field("total_votes"),DSL.field("@rownum:= @rownum+1").as("position"))
//		.from(totalVotesPerAddress).crossJoin("(SELECT @rownum := 0) as t").asTable("t2");
//		
//		
//		
//		this.dslContext.deleteFrom(VOTING_ROUND_STATS)
//		.where(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(round.getId()))
//		.execute();		
		
//		this.dslContext.insertInto(VOTING_ROUND_STATS)
//		.columns(VOTING_ROUND_STATS.VOTING_ROUND_ID,VOTING_ROUND_STATS.ADDRESS,VOTING_ROUND_STATS.TOTAL_VOTES,VOTING_ROUND_STATS.POSITION)
//		.select(DSL.select(DSL.val(round.getId()),
//				totalVotesPerAddressWithPosition.field(CONTRACT_VOTE_WITNESS.VOTE_ADDRESS.getName(),String.class),
//				totalVotesPerAddressWithPosition.field("total_votes",ULong.class),
//				totalVotesPerAddressWithPosition.field("position",UInteger.class))
//				.from(totalVotesPerAddressWithPosition)
//		).execute();

		
		//mark round as completed if not the last one
		Instant instant = Instant.ofEpochMilli(nextMaintenanceTime);
		
		LocalDateTime maintenanceTime = instant.atZone(ZoneOffset.UTC).toLocalDateTime();
		Timestamp nextMaintenanceTS = Timestamp.valueOf(maintenanceTime);
		
		if (round.getEndDate().before(nextMaintenanceTS)) {
			
			this.dslContext.update(VOTING_ROUND)
			.set(VOTING_ROUND.SYNC_END,Timestamp.valueOf(LocalDateTime.now()))
			.where(VOTING_ROUND.ID.eq(round.getId()))
			.execute();
			
		}
		
				
	}
	
}

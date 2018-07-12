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
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.model.tables.ContractVoteWitness;
import io.trxplorer.model.tables.VotingRound;
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
			
		VotingRound vr = VOTING_ROUND.as("vr");
		
		Table<Record2<Object, UInteger>> tmp = DSL.select(DSL.field("@rownum := @rownum+1").as("round"),VOTING_ROUND.ID).from(VOTING_ROUND).crossJoin("(select @rownum:=0) tmp").orderBy(VOTING_ROUND.END_DATE.asc()).asTable("tmp");
		
		this.dslContext.update(vr)
		.set(vr.ROUND,DSL.select(DSL.field("round",UInteger.class)).from(tmp).where(vr.ID.eq(tmp.field("id",UInteger.class))))
		.execute();					

		
	}
	
	@Scheduled("5m")
	public void createVoteRound() {
		
		
		List<VotingRoundRecord> rounds = this.dslContext.select(VOTING_ROUND.fields())
		.from(VOTING_ROUND)
		.where(VOTING_ROUND.SYNC_END.isNull())
		.and(VOTING_ROUND.END_DATE.lt(DSL.select(DSL.max(BLOCK.TIMESTAMP)).from(BLOCK)))
		.orderBy(VOTING_ROUND.START_DATE.desc())
		.fetchInto(VotingRoundRecord.class);

		
		for(VotingRoundRecord round:rounds) {
			
			this.computeRoundVotes(round);
			
		}
				
	}
	
	
	private void computeRoundVotes(VotingRoundRecord round) {
		
		
		this.dslContext.update(VOTING_ROUND)
		.set(VOTING_ROUND.SYNC_START,Timestamp.valueOf(LocalDateTime.now()))
		.where(VOTING_ROUND.ID.eq(round.getId()))
		.execute();


		List<String> addresses = this.dslContext.select(WITNESS.ADDRESS).from(WITNESS).fetchInto(String.class); 
		 
		for(String witnessAddress : addresses) {
			
			this.dslContext.deleteFrom(VOTING_ROUND_VOTE)
			.where(VOTING_ROUND_VOTE.VOTING_ROUND_ID.eq(round.getId()))
			.and(VOTING_ROUND_VOTE.VOTE_ADDRESS.eq(witnessAddress))
			.execute();
			
			this.dslContext.deleteFrom(VOTING_ROUND_VOTE_LOST)
			.where(VOTING_ROUND_VOTE_LOST.VOTING_ROUND_ID.eq(round.getId()))
			.and(VOTING_ROUND_VOTE_LOST.VOTE_ADDRESS.eq(witnessAddress))
			.execute();
			
			this.dslContext.deleteFrom(VOTING_ROUND_STATS)
			.where(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(round.getId()))
			.and(VOTING_ROUND_STATS.ADDRESS.eq(witnessAddress))
			.execute();
			
			//set votes for current round

			ContractVoteWitness cvw = CONTRACT_VOTE_WITNESS.as("cvw");
			 Table<Record5<UInteger, String, String, ULong, Timestamp>> latestVotesIdsTable = DSL.select(DSL.val(round.getId()).as("roundId"),cvw.OWNER_ADDRESS,cvw.VOTE_ADDRESS,cvw.VOTE_COUNT,BLOCK.TIMESTAMP)
			.from(cvw)
			.join(TRANSACTION).on(cvw.field(CONTRACT_VOTE_WITNESS.TRANSACTION_ID.getName(),ULong.class).eq(TRANSACTION.ID))
			.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID)).and(BLOCK.TIMESTAMP.lt(round.getEndDate()))
			.where(cvw.VOTE_ADDRESS.eq(witnessAddress))
			.and(BLOCK.TIMESTAMP.eq(DSL.select(DSL.max(BLOCK.TIMESTAMP))
					.from(CONTRACT_VOTE_WITNESS)
					.join(TRANSACTION).on(CONTRACT_VOTE_WITNESS.TRANSACTION_ID.eq(TRANSACTION.ID))
					.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID))
					.and(BLOCK.TIMESTAMP.lt(round.getEndDate()))
					.where(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.eq(cvw.field(CONTRACT_VOTE_WITNESS.OWNER_ADDRESS.getName(),String.class)))
					)
					
				)
			.asTable("t1");
			
			this.dslContext.insertInto(VOTING_ROUND_VOTE)
			.columns(VOTING_ROUND_VOTE.VOTING_ROUND_ID,VOTING_ROUND_VOTE.OWNER_ADDRESS,VOTING_ROUND_VOTE.VOTE_ADDRESS,VOTING_ROUND_VOTE.VOTE_COUNT,VOTING_ROUND_VOTE.TIMESTAMP)
			.select(DSL.select(latestVotesIdsTable.field(0, UInteger.class),latestVotesIdsTable.field(1,String.class),latestVotesIdsTable.field(2,String.class),latestVotesIdsTable.field(3,Long.class),latestVotesIdsTable.field(4,Timestamp.class))
					.from(latestVotesIdsTable))
			.execute();
			
			
			//add vote lost for current round
			VotingRoundVote vrv = VOTING_ROUND_VOTE.as("vrv");
			
			this.dslContext.insertInto(VOTING_ROUND_VOTE_LOST)
			.columns(VOTING_ROUND_VOTE.VOTING_ROUND_ID,VOTING_ROUND_VOTE.OWNER_ADDRESS,VOTING_ROUND_VOTE.VOTE_ADDRESS,VOTING_ROUND_VOTE.VOTE_COUNT,VOTING_ROUND_VOTE.TIMESTAMP)		
			.select(	
			DSL.select(vrv.VOTING_ROUND_ID,vrv.OWNER_ADDRESS,vrv.VOTE_ADDRESS,vrv.VOTE_COUNT,vrv.TIMESTAMP)
			.from(vrv)
			.where(vrv.TIMESTAMP.lt(DSL.select(DSL.max(BLOCK.TIMESTAMP))
					.from(CONTRACT_UNFREEZE_BALANCE)
					.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_UNFREEZE_BALANCE.TRANSACTION_ID))
					.join(BLOCK).on(BLOCK.ID.eq(TRANSACTION.BLOCK_ID))
					.where(CONTRACT_UNFREEZE_BALANCE.OWNER_ADDRESS.eq(vrv.OWNER_ADDRESS))
					.and(BLOCK.TIMESTAMP.lt(round.getEndDate()))
				))
			.and(vrv.VOTE_ADDRESS.eq(witnessAddress))
			.and(vrv.VOTING_ROUND_ID.eq(round.getId()))
			)
			.execute()
			;
			
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
		
		
		SelectConditionStep<Record1<ULong>> totalRoundVoteCount = DSL.select(DSL.sum(VOTING_ROUND_STATS.VOTE_COUNT).minus(DSL.sum(VOTING_ROUND_STATS.VOTE_LOST_COUNT)).cast(ULong.class)).from(VOTING_ROUND_STATS).where(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(round.getId()));
		
		//update round vote count
		this.dslContext.update(VOTING_ROUND)
		.set(VOTING_ROUND.VOTE_COUNT,totalRoundVoteCount)
		.where(VOTING_ROUND.ID.eq(round.getId()))
		.execute();
		
		
		//mark round sync as completed			
		this.dslContext.update(VOTING_ROUND)
		.set(VOTING_ROUND.SYNC_END,Timestamp.valueOf(LocalDateTime.now()))
		.where(VOTING_ROUND.ID.eq(round.getId()))
		.execute();
			
		
		
		
	}
	
	
}

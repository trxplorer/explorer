package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record1;
import org.jooq.ResultQuery;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;

import com.google.inject.Inject;

import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.service.dto.vote.VoteListCriteria;
import io.trxplorer.service.dto.vote.VoteLiveListCriteria;
import io.trxplorer.service.dto.vote.VoteLiveModel;
import io.trxplorer.service.dto.vote.VoteModel;
import io.trxplorer.service.dto.vote.VotingRoundDTO;
import io.trxplorer.service.dto.vote.VotingRoundListCriteria;
import io.trxplorer.service.dto.vote.VotingRoundStatsListCriteria;
import io.trxplorer.service.dto.vote.VotingRoundStatsModel;

public class VoteService {

	private DSLContext dslContext;

	
	@Inject
	public VoteService(DSLContext dslContext) {
		this.dslContext = dslContext;
		
	}
	
	
	public ListModel<VotingRoundDTO, VotingRoundListCriteria> listRounds(VotingRoundListCriteria criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(VOTING_ROUND.SYNC_END.isNotNull());
		
		 SelectJoinStep<?> listQuery = this.dslContext.select(VOTING_ROUND.START_DATE,VOTING_ROUND.END_DATE,VOTING_ROUND.ROUND,VOTING_ROUND.VOTE_COUNT)
		.from(VOTING_ROUND);
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(VOTING_ROUND);
		

		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VotingRoundDTO> items = listQuery.where(conditions).orderBy(VOTING_ROUND.START_DATE.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VotingRoundDTO.class);
		
		
		
		ListModel<VotingRoundDTO, VotingRoundListCriteria> result = new ListModel<VotingRoundDTO, VotingRoundListCriteria>(criteria, items, totalCount);
		
		return result;
	}
	
	
	public VotingRoundDTO getVotingRoundByNum(Integer num) {
		
		return this.dslContext.select(VOTING_ROUND.fields()).from(VOTING_ROUND).where(VOTING_ROUND.ROUND.eq(UInteger.valueOf(num))).fetchOneInto(VotingRoundDTO.class);
		
	}
	
	public VotingRoundStatsModel getVotingRoundStats(Integer round,String address) {
		
		Integer maxVotingRound = this.dslContext.select(DSL.max(VOTING_ROUND.ROUND))
				.from(VOTING_ROUND).fetchOneInto(Integer.class);
		
		if (maxVotingRound==round) {
			
			return this.dslContext.select(VOTE_LIVE.ADDRESS,VOTE_LIVE.POSITION,VOTE_LIVE.VOTE_COUNT.as("votes"),ACCOUNT.ACCOUNT_NAME.as("name"),VOTING_ROUND.START_DATE,VOTING_ROUND.END_DATE,VOTING_ROUND.ROUND)
					.from(VOTE_LIVE,VOTING_ROUND,ACCOUNT)
					.where(VOTING_ROUND.ROUND.eq(UInteger.valueOf(round))
					.and(ACCOUNT.ADDRESS.eq(VOTE_LIVE.ADDRESS))
					.and(VOTE_LIVE.ADDRESS.eq(address))	
				).fetchOneInto(VotingRoundStatsModel.class);
			
		}else {

			return this.dslContext.select(VOTING_ROUND_STATS.ADDRESS,VOTING_ROUND_STATS.POSITION,VOTING_ROUND_STATS.VOTE_COUNT.as("votes"),VOTING_ROUND_STATS.VOTE_LOST_COUNT,ACCOUNT.ACCOUNT_NAME.as("name"),VOTING_ROUND.START_DATE,VOTING_ROUND.END_DATE,VOTING_ROUND.ROUND)
					.from(VOTING_ROUND_STATS,VOTING_ROUND,ACCOUNT)
					.where(VOTING_ROUND.ID.eq(VOTING_ROUND_STATS.VOTING_ROUND_ID))
					.and(ACCOUNT.ADDRESS.eq(VOTING_ROUND_STATS.ADDRESS))
					.and(VOTING_ROUND.ROUND.eq(UInteger.valueOf(round))
					.and(VOTING_ROUND_STATS.ADDRESS.eq(address))	
				).fetchOneInto(VotingRoundStatsModel.class);
			
		}
		

	}
	
	public ListModel<VotingRoundStatsModel, VotingRoundStatsListCriteria> listRoundStats(VotingRoundStatsListCriteria criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(VOTING_ROUND.ID));
		conditions.add(ACCOUNT.ADDRESS.eq(WITNESS.ADDRESS));
		conditions.add(ACCOUNT.ADDRESS.eq(VOTING_ROUND_STATS.ADDRESS));
		
		if (StringUtils.isNotBlank(criteria.getAddress())) {
			conditions.add(ACCOUNT.ADDRESS.eq(criteria.getAddress()));	
		}
		
		if (criteria.getRound()!=null) {
			conditions.add(VOTING_ROUND.ROUND.eq(UInteger.valueOf(criteria.getRound())));			
		}

		if (criteria.getMaxRound()!=null) {
			
			Integer maxRound = this.dslContext.select(DSL.max(VOTING_ROUND.ROUND)).from(VOTING_ROUND).where(VOTING_ROUND.SYNC_END.isNotNull()).fetchOneInto(Integer.class);
			Integer minRound = (maxRound-10<0) ? 1 : maxRound-10;
			
			conditions.add(VOTING_ROUND.ROUND.ge(UInteger.valueOf(minRound)));
			conditions.add(VOTING_ROUND.ROUND.le(UInteger.valueOf(maxRound)));
			
		}
		
		SelectJoinStep<?> listQuery = this.dslContext.select(ACCOUNT.ACCOUNT_NAME.as("name"),WITNESS.URL,VOTING_ROUND_STATS.ADDRESS,VOTING_ROUND_STATS.VOTE_COUNT.as("votes"),VOTING_ROUND.ROUND,VOTING_ROUND.START_DATE,VOTING_ROUND.END_DATE,VOTING_ROUND_STATS.POSITION,VOTING_ROUND_STATS.POSITION_CHANGE,VOTING_ROUND_STATS.VOTES_CHANGE)
					.from(ACCOUNT,WITNESS,VOTING_ROUND_STATS,VOTING_ROUND);
		
		
		SelectJoinStep<?> countQuery = dslContext.select(DSL.count())
				.from(ACCOUNT,WITNESS,VOTING_ROUND_STATS,VOTING_ROUND);
		

		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		Query query = listQuery.where(conditions).orderBy(VOTING_ROUND_STATS.POSITION.asc());
		
		if (criteria.getMaxRound()==null) {
			query = ((SelectLimitStep<?>) query).limit(criteria.getLimit()).offset(criteria.getOffSet());
		}
		
		List<VotingRoundStatsModel> items = ((ResultQuery<?>) query).fetchInto(VotingRoundStatsModel.class);

		ListModel<VotingRoundStatsModel, VotingRoundStatsListCriteria> result = new ListModel<VotingRoundStatsModel, VotingRoundStatsListCriteria>(criteria, items, totalCount);
		
		
		return result;
	}
	
	
	public ListModel<VoteModel, VoteListCriteria> listLiveVotes(VoteListCriteria criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(ACCOUNT.ID.eq(ACCOUNT_VOTE.ACCOUNT_ID));
		conditions.add(ACCOUNT_VOTE.VOTE_ADDRESS.eq(criteria.getToAddress()));
		
		SelectJoinStep<?> listQuery = this.dslContext.select(ACCOUNT.ADDRESS.as("from"),ACCOUNT_VOTE.VOTE_ADDRESS.as("to"),ACCOUNT_VOTE.VOTE_COUNT.as("votes"))
					.from(ACCOUNT_VOTE,ACCOUNT);
		
		
		SelectJoinStep<?> countQuery = dslContext.select(DSL.count())
				.from(ACCOUNT_VOTE,ACCOUNT);

		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VoteModel> items = listQuery.where(conditions).orderBy(ACCOUNT_VOTE.VOTE_COUNT.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VoteModel.class);

		ListModel<VoteModel, VoteListCriteria> result = new ListModel<VoteModel, VoteListCriteria>(criteria, items, totalCount);
		
		
		return result;
		

	}
	
	public ListModel<VoteModel, VoteListCriteria> listVotes(VoteListCriteria criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(VOTING_ROUND_VOTE.VOTING_ROUND_ID.eq(VOTING_ROUND.ID));
		if (criteria.getRound()!=null) {
			conditions.add(VOTING_ROUND.ROUND.eq(UInteger.valueOf(criteria.getRound())));			
		}
		if (StringUtils.isNotBlank(criteria.getToAddress())) {
			conditions.add(VOTING_ROUND_VOTE.VOTE_ADDRESS.eq(criteria.getToAddress()));			
		}

		
		SelectJoinStep<?> listQuery = this.dslContext.select(VOTING_ROUND_VOTE.OWNER_ADDRESS.as("from"),VOTING_ROUND_VOTE.VOTE_ADDRESS.as("to"),VOTING_ROUND_VOTE.VOTE_COUNT.as("votes"),VOTING_ROUND_VOTE.TIMESTAMP)
					.from(VOTING_ROUND,VOTING_ROUND_VOTE);
		
		
		SelectJoinStep<?> countQuery = dslContext.select(DSL.count())
				.from(VOTING_ROUND,VOTING_ROUND_VOTE);

		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VoteModel> items = listQuery.where(conditions).orderBy(VOTING_ROUND_VOTE.VOTE_COUNT.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VoteModel.class);

		ListModel<VoteModel, VoteListCriteria> result = new ListModel<VoteModel, VoteListCriteria>(criteria, items, totalCount);
		
		
		return result;
	}
	
	public ListModel<VoteLiveModel, VoteLiveListCriteria> listLiveVotesStats(VoteLiveListCriteria criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(VOTE_LIVE.ADDRESS.eq(ACCOUNT.ADDRESS));
		conditions.add(VOTE_LIVE.ADDRESS.eq(WITNESS.ADDRESS));
		
		SelectJoinStep<?> listQuery = this.dslContext.select(ACCOUNT.ACCOUNT_NAME.as("name"),WITNESS.URL,VOTE_LIVE.ADDRESS,VOTE_LIVE.POSITION,VOTE_LIVE.VOTE_COUNT.as("votes"),VOTE_LIVE.POSITION_CHANGE,VOTE_LIVE.VOTE_CHANGE.as("votesChange"))
					.from(VOTE_LIVE,ACCOUNT,WITNESS);
		
		
		SelectJoinStep<?> countQuery = dslContext.select(DSL.count())
				.from(VOTE_LIVE,ACCOUNT,WITNESS);

		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VoteLiveModel> items = listQuery.where(conditions).orderBy(VOTE_LIVE.VOTE_COUNT.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VoteLiveModel.class);

		ListModel<VoteLiveModel, VoteLiveListCriteria> result = new ListModel<VoteLiveModel, VoteLiveListCriteria>(criteria, items, totalCount);
		
		return result;
	}
	
	public long getLiveTotalVotes() {
		return this.dslContext.select(DSL.sum(ACCOUNT_VOTE.VOTE_COUNT)).from(ACCOUNT_VOTE).fetchOneInto(Long.class);
	}

	
}

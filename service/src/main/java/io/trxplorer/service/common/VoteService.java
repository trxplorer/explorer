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

import io.trxplorer.service.dto.common.ListDTO;
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
	
	
	public ListDTO<VotingRoundDTO, VotingRoundListCriteria> listRounds(VotingRoundListCriteria criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(VOTING_ROUND.SYNC_END.isNotNull());
		
		 SelectJoinStep<?> listQuery = this.dslContext.select(VOTING_ROUND.START_DATE,VOTING_ROUND.END_DATE,VOTING_ROUND.ROUND,VOTING_ROUND.VOTE_COUNT)
		.from(VOTING_ROUND);
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(VOTING_ROUND);
		

		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VotingRoundDTO> items = listQuery.where(conditions).orderBy(VOTING_ROUND.START_DATE.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VotingRoundDTO.class);
		
		
		
		ListDTO<VotingRoundDTO, VotingRoundListCriteria> result = new ListDTO<VotingRoundDTO, VotingRoundListCriteria>(criteria, items, totalCount);
		
		return result;
	}
	
	
	public VotingRoundDTO getVotingRoundByNum(Integer num) {
		
		return this.dslContext.select(VOTING_ROUND.fields()).from(VOTING_ROUND).where(VOTING_ROUND.ROUND.eq(UInteger.valueOf(num))).fetchOneInto(VotingRoundDTO.class);
		
	}
	
	public VotingRoundStatsModel getVotingRoundStats(Integer round,String address) {
		return this.dslContext.select(VOTING_ROUND_STATS.ADDRESS,VOTING_ROUND_STATS.POSITION,VOTING_ROUND_STATS.VOTE_COUNT,VOTING_ROUND_STATS.VOTE_LOST_COUNT,ACCOUNT.ACCOUNT_NAME.as("name"),VOTING_ROUND.START_DATE,VOTING_ROUND.END_DATE,VOTING_ROUND.ROUND)
				.from(VOTING_ROUND_STATS,VOTING_ROUND,ACCOUNT)
				.where(VOTING_ROUND.ID.eq(VOTING_ROUND_STATS.VOTING_ROUND_ID))
				.and(ACCOUNT.ADDRESS.eq(VOTING_ROUND_STATS.ADDRESS))
				.and(VOTING_ROUND.ROUND.eq(UInteger.valueOf(round))
				.and(VOTING_ROUND_STATS.ADDRESS.eq(address))	
			).fetchOneInto(VotingRoundStatsModel.class);
	}
	
	public ListDTO<VotingRoundStatsModel, VotingRoundStatsListCriteria> listRoundStats(VotingRoundStatsListCriteria criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(VOTING_ROUND_STATS.VOTING_ROUND_ID.eq(VOTING_ROUND.ID));
		
		if (criteria.getRound()!=null) {
			conditions.add(VOTING_ROUND.ROUND.eq(UInteger.valueOf(criteria.getRound())));			
		}

		if (criteria.getMaxRound()!=null) {
			
			Integer maxRound = this.dslContext.select(DSL.max(VOTING_ROUND.ROUND)).from(VOTING_ROUND).where(VOTING_ROUND.SYNC_END.isNotNull()).fetchOneInto(Integer.class);
			Integer minRound = (maxRound-10<0) ? 1 : maxRound-10;
			
			conditions.add(VOTING_ROUND.ROUND.ge(UInteger.valueOf(minRound)));
			conditions.add(VOTING_ROUND.ROUND.le(UInteger.valueOf(maxRound)));
			
		}
		
		SelectJoinStep<?> listQuery = this.dslContext.select(VOTING_ROUND_STATS.ADDRESS,VOTING_ROUND_STATS.VOTE_COUNT,VOTING_ROUND.ROUND)
					.from(VOTING_ROUND_STATS,VOTING_ROUND);
		
		
		SelectJoinStep<?> countQuery = dslContext.select(DSL.count())
				.from(VOTING_ROUND_STATS,VOTING_ROUND);
		

		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		Query query = listQuery.where(conditions).orderBy(VOTING_ROUND_STATS.VOTE_COUNT.desc());
		
		if (criteria.getMaxRound()==null) {
			query = ((SelectLimitStep<?>) query).limit(criteria.getLimit()).offset(criteria.getOffSet());
		}
		
		List<VotingRoundStatsModel> items = ((ResultQuery<?>) query).fetchInto(VotingRoundStatsModel.class);

		ListDTO<VotingRoundStatsModel, VotingRoundStatsListCriteria> result = new ListDTO<VotingRoundStatsModel, VotingRoundStatsListCriteria>(criteria, items, totalCount);
		
		
		return result;
	}
	
	
	public ListDTO<VoteModel, VoteListCriteria> listVotes(VoteListCriteria criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(VOTING_ROUND_VOTE.VOTING_ROUND_ID.eq(VOTING_ROUND.ID));
		if (criteria.getRound()!=null) {
			conditions.add(VOTING_ROUND.ROUND.eq(UInteger.valueOf(criteria.getRound())));			
		}
		if (StringUtils.isNotBlank(criteria.getToAddress())) {
			conditions.add(VOTING_ROUND_VOTE.VOTE_ADDRESS.eq(criteria.getToAddress()));			
		}

		
		SelectJoinStep<?> listQuery = this.dslContext.select(VOTING_ROUND_VOTE.OWNER_ADDRESS.as("from"),VOTING_ROUND_VOTE.VOTE_ADDRESS.as("to"),VOTING_ROUND_VOTE.VOTE_COUNT,VOTING_ROUND_VOTE.TIMESTAMP)
					.from(VOTING_ROUND,VOTING_ROUND_VOTE);
		
		
		SelectJoinStep<?> countQuery = dslContext.select(DSL.count())
				.from(VOTING_ROUND,VOTING_ROUND_VOTE);

		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VoteModel> items = listQuery.where(conditions).orderBy(VOTING_ROUND_VOTE.VOTE_COUNT.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VoteModel.class);

		ListDTO<VoteModel, VoteListCriteria> result = new ListDTO<VoteModel, VoteListCriteria>(criteria, items, totalCount);
		
		
		return result;
	}
	
	public ListDTO<VoteLiveModel, VoteLiveListCriteria> listLiveVotes(VoteLiveListCriteria criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(VOTE_LIVE.ADDRESS.eq(ACCOUNT.ADDRESS));
		conditions.add(VOTE_LIVE.ADDRESS.eq(WITNESS.ADDRESS));
		
		SelectJoinStep<?> listQuery = this.dslContext.select(ACCOUNT.ACCOUNT_NAME.as("name"),WITNESS.URL,VOTE_LIVE.ADDRESS,VOTE_LIVE.POSITION,VOTE_LIVE.VOTE_COUNT.as("votes"))
					.from(VOTE_LIVE,ACCOUNT,WITNESS);
		
		
		SelectJoinStep<?> countQuery = dslContext.select(DSL.count())
				.from(VOTE_LIVE,ACCOUNT,WITNESS);

		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VoteLiveModel> items = listQuery.where(conditions).orderBy(VOTE_LIVE.VOTE_COUNT.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VoteLiveModel.class);

		ListDTO<VoteLiveModel, VoteLiveListCriteria> result = new ListDTO<VoteLiveModel, VoteLiveListCriteria>(criteria, items, totalCount);
		
		return result;
	}

	
}

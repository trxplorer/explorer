package io.trxplorer.webapp.service;

import static io.trxplorer.model.Tables.*;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import com.google.inject.Inject;

import io.trxplorer.webapp.dto.common.ListDTO;
import io.trxplorer.webapp.dto.vote.VotingRoundDTO;
import io.trxplorer.webapp.dto.vote.VotingRoundListCriteriaDTO;

public class VoteService {

	private DSLContext dslContext;

	
	@Inject
	public VoteService(DSLContext dslContext) {
		this.dslContext = dslContext;
		
	}
	
	
	public ListDTO<VotingRoundDTO, VotingRoundListCriteriaDTO> listRounds(VotingRoundListCriteriaDTO criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		conditions.add(VOTING_ROUND.SYNC_END.isNotNull());
		
		 SelectJoinStep<?> listQuery = this.dslContext.select(VOTING_ROUND.START_DATE,VOTING_ROUND.END_DATE,VOTING_ROUND.ROUND,VOTING_ROUND.VOTE_COUNT)
		.from(VOTING_ROUND);
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(VOTING_ROUND);
		

		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<VotingRoundDTO> items = listQuery.where(conditions).orderBy(VOTING_ROUND.START_DATE.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(VotingRoundDTO.class);
		
		
		
		ListDTO<VotingRoundDTO, VotingRoundListCriteriaDTO> result = new ListDTO<VotingRoundDTO, VotingRoundListCriteriaDTO>(criteria, items, totalCount);
		
		return result;
	}
	
	
	public VotingRoundDTO getVotingRoundByNum(Integer num) {
		
		return this.dslContext.select(VOTING_ROUND.fields()).from(VOTING_ROUND).where(VOTING_ROUND.ROUND.eq(UInteger.valueOf(num))).fetchOneInto(VotingRoundDTO.class);
		
	}
	

	
}

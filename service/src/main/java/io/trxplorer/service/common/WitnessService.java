package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SortField;
import org.jooq.impl.DSL;

import com.google.inject.Inject;

import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.service.dto.witness.WitnessListCriteriaDTO;
import io.trxplorer.service.dto.witness.WitnessModel;
import io.trxplorer.service.utils.Constants;
import io.trxplorer.troncli.TronFullNodeCli;

public class WitnessService {

	private DSLContext dslContext;
	private TronFullNodeCli tronFullNodeCli;
	
	
	@Inject
	public WitnessService(DSLContext dslContext,TronFullNodeCli tronService) {
		this.dslContext = dslContext;
		this.tronFullNodeCli = tronService;
	}
	
	
	
	public WitnessModel getWitnessByAddress(String address) {
		
		WitnessModel result = this.dslContext.select(WITNESS.URL,WITNESS.VOTE_COUNT,WITNESS.TOTAL_MISSED,WITNESS.TOTAL_PRODUCED,DSL.sum(ACCOUNT_VOTE.VOTE_COUNT).as("liveVotes"))
		.from(WITNESS,ACCOUNT_VOTE).where(WITNESS.ADDRESS.eq(address))
		.and(WITNESS.ADDRESS.eq(ACCOUNT_VOTE.VOTE_ADDRESS))
		.fetchOneInto(WitnessModel.class);
		
		Long genesisVotes = Constants.genesisVotes.get(address); 
		if (genesisVotes!=null) {
			result.setLiveVotes(result.getLiveVotes()+genesisVotes);
		}
		
		return result; 
	}
	
	
	public ListModel<WitnessModel, WitnessListCriteriaDTO> listWitnesses(WitnessListCriteriaDTO criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		
		
		SelectOnConditionStep<?> listQuery = this.dslContext.select(WITNESS.URL,WITNESS.VOTE_COUNT,WITNESS.URL,WITNESS.LATEST_BLOCK_NUM.as("lastBlock"),WITNESS.TOTAL_PRODUCED,WITNESS.TOTAL_MISSED,WITNESS.ADDRESS,ACCOUNT.ACCOUNT_NAME.as("name")).from(WITNESS)
				.leftJoin(ACCOUNT).on(ACCOUNT.ADDRESS.eq(WITNESS.ADDRESS));


		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
				.from(WITNESS).leftJoin(ACCOUNT).on(ACCOUNT.ADDRESS.eq(WITNESS.ADDRESS));
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<SortField<?>> sortFields = new ArrayList<>();
		

		sortFields.add(WITNESS.VOTE_COUNT.desc());

		
		List<WitnessModel> items = listQuery.where(conditions).orderBy(sortFields).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(WitnessModel.class);
		
		prepareWitnessDTO(items);
		
		ListModel<WitnessModel, WitnessListCriteriaDTO> result = new ListModel<WitnessModel, WitnessListCriteriaDTO>(criteria, items, totalCount);
		
		return result;
		
	}
	
	

	
	private void prepareWitnessDTO(List<WitnessModel> witnesses) {
		
		for(WitnessModel witness:witnesses) {

			witness.setShortUrl(StringUtils.abbreviate(witness.getUrl().trim(), 50));
			
		}
		
	}
	
	
	
}

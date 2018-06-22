package io.trxplorer.webapp.service;

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

import io.trxplorer.troncli.TronFullNodeCli;
import io.trxplorer.webapp.dto.common.ListDTO;
import io.trxplorer.webapp.dto.witness.WitnessDTO;
import io.trxplorer.webapp.dto.witness.WitnessListCriteriaDTO;

public class WitnessService {

	private DSLContext dslContext;
	private TronFullNodeCli tronFullNodeCli;
	
	
	@Inject
	public WitnessService(DSLContext dslContext,TronFullNodeCli tronService) {
		this.dslContext = dslContext;
		this.tronFullNodeCli = tronService;
	}
	
	
	
	public WitnessDTO getWitnessByAddress(String address) {
		return this.dslContext.select(WITNESS.URL,WITNESS.VOTE_COUNT,WITNESS.TOTAL_MISSED,WITNESS.TOTAL_PRODUCED)
		.from(WITNESS).where(WITNESS.ADDRESS.eq(address)).fetchOneInto(WitnessDTO.class);
	}
	
	
	public ListDTO<WitnessDTO, WitnessListCriteriaDTO> listWitnesses(WitnessListCriteriaDTO criteria){
		
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		
		
		SelectOnConditionStep<?> listQuery = this.dslContext.select(WITNESS.URL,WITNESS.VOTE_COUNT,WITNESS.URL,WITNESS.LATEST_BLOCK_NUM.as("lastBlock"),WITNESS.TOTAL_PRODUCED,WITNESS.TOTAL_MISSED,WITNESS.ADDRESS,ACCOUNT.ACCOUNT_NAME.as("name")).from(WITNESS)
				.leftJoin(ACCOUNT).on(ACCOUNT.ADDRESS.eq(WITNESS.ADDRESS));


		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
				.from(WITNESS).leftJoin(ACCOUNT).on(ACCOUNT.ADDRESS.eq(WITNESS.ADDRESS));
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<SortField<?>> sortFields = new ArrayList<>();
		

		sortFields.add(WITNESS.VOTE_COUNT.desc());

		
		List<WitnessDTO> items = listQuery.where(conditions).orderBy(sortFields).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(WitnessDTO.class);
		
		prepareWitnessDTO(items);
		
		ListDTO<WitnessDTO, WitnessListCriteriaDTO> result = new ListDTO<WitnessDTO, WitnessListCriteriaDTO>(criteria, items, totalCount);
		
		return result;
		
	}
	
	private void prepareWitnessDTO(List<WitnessDTO> witnesses) {
		
		for(WitnessDTO witness:witnesses) {

			witness.setShortUrl(StringUtils.abbreviate(witness.getUrl().trim(), 50));
			
		}
		
	}
	
}

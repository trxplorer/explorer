package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SortField;
import org.jooq.impl.DSL;

import com.google.inject.Inject;

import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.service.dto.witness.WitnessDTO;
import io.trxplorer.service.dto.witness.WitnessListCriteriaDTO;
import io.trxplorer.troncli.TronFullNodeCli;

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
	
	
	public ListModel<WitnessDTO, WitnessListCriteriaDTO> listWitnesses(WitnessListCriteriaDTO criteria){
		
		
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
		
		ListModel<WitnessDTO, WitnessListCriteriaDTO> result = new ListModel<WitnessDTO, WitnessListCriteriaDTO>(criteria, items, totalCount);
		
		return result;
		
	}
	
	
	public List<Map<String, Object>> listGenesisWitnesses(){
		
		HashMap<String,Long> genesisVotes = new HashMap<>();

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
			
		Field<Object> voteCoundField = DSL.select(DSL.sum(ACCOUNT_VOTE.VOTE_COUNT)).from(ACCOUNT_VOTE,ACCOUNT).where(ACCOUNT.ID.eq(ACCOUNT_VOTE.ID).and(ACCOUNT.ADDRESS.eq(WITNESS.ADDRESS))).asField("votes");
		
		List<Map<String, Object>> result = this.dslContext.select(WITNESS.URL,voteCoundField).from(WITNESS).where(WITNESS.ADDRESS.in(genesisVotes.keySet())).fetchMaps();
		
		for(Map<String, Object> item:result) {
			
			item.put("genesisVotes", genesisVotes.get(item.get("address")));
			
		}
		
		
		return result;
	}
	
	private void prepareWitnessDTO(List<WitnessDTO> witnesses) {
		
		for(WitnessDTO witness:witnesses) {

			witness.setShortUrl(StringUtils.abbreviate(witness.getUrl().trim(), 50));
			
		}
		
	}
	
	
	
}

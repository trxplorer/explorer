package io.trxplorer.webapp.service;

import static io.trxplorer.model.Tables.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.webapp.dto.asset.AssetIssueListCriteriaDTO;
import io.trxplorer.webapp.dto.asset.AssetIssueDTO;
import io.trxplorer.webapp.dto.asset.AssetIssueDetailCriteriaDTO;
import io.trxplorer.webapp.dto.asset.AssetParticipationCriteriaDTO;
import io.trxplorer.webapp.dto.asset.AssetParticipationDTO;
import io.trxplorer.webapp.dto.common.ListDTO;
import io.trxplorer.webapp.dto.transaction.TransactionCriteriaDTO;
import io.trxplorer.webapp.dto.transaction.TransactionDTO;

@Singleton
public class AssetService {
	
	private DSLContext dslContext;
	private TransactionService txService;
	
	@Inject
	public AssetService(DSLContext dslContext,TransactionService txService) {
		this.dslContext = dslContext;
		this.txService = txService;
	}
	
	public ListDTO<AssetIssueDTO, AssetIssueListCriteriaDTO> listAssetIssues(AssetIssueListCriteriaDTO criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		Field<?> participantCountField = DSL.select(DSL.count()).from(CONTRACT_PARTICIPATE_ASSET_ISSUE).where(CONTRACT_PARTICIPATE_ASSET_ISSUE.ASSET_NAME.eq(CONTRACT_ASSET_ISSUE.NAME)).asField("totalParticipants");
		
		SelectJoinStep<?> listQuery = this.dslContext.select(CONTRACT_ASSET_ISSUE.ID,CONTRACT_ASSET_ISSUE.NAME,CONTRACT_ASSET_ISSUE.TOTAL_SUPPLY,CONTRACT_ASSET_ISSUE.START_TIME,CONTRACT_ASSET_ISSUE.END_TIME,CONTRACT_ASSET_ISSUE.OWNER_ADDRESS.as("issuer"),participantCountField).from(CONTRACT_ASSET_ISSUE)
				.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_ASSET_ISSUE.TRANSACTION_ID));
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(CONTRACT_ASSET_ISSUE);
		
		
		if (StringUtils.isNotBlank(criteria.getIssuer())) {
			conditions.add(CONTRACT_ASSET_ISSUE.OWNER_ADDRESS.eq(criteria.getIssuer()));
		}
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<AssetIssueDTO> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(AssetIssueDTO.class);
		
		prepareAssetIssueDTO(items);
		
		ListDTO<AssetIssueDTO, AssetIssueListCriteriaDTO> result = new ListDTO<AssetIssueDTO, AssetIssueListCriteriaDTO>(criteria, items, totalCount);
		
		return result;
		

	}

	
	public ListDTO<AssetParticipationDTO, AssetParticipationCriteriaDTO> listAssetParticipations(AssetParticipationCriteriaDTO criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		

		
		SelectJoinStep<?> listQuery = this.dslContext.select(CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS.as("from"),CONTRACT_PARTICIPATE_ASSET_ISSUE.TO_ADDRESS.as("to"),CONTRACT_PARTICIPATE_ASSET_ISSUE.AMOUNT,CONTRACT_PARTICIPATE_ASSET_ISSUE.ASSET_NAME,TRANSACTION.TIMESTAMP).from(CONTRACT_PARTICIPATE_ASSET_ISSUE)
				.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_PARTICIPATE_ASSET_ISSUE.TRANSACTION_ID));
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(CONTRACT_PARTICIPATE_ASSET_ISSUE);
		
		
		if (StringUtils.isNotBlank(criteria.getAddress())) {
			conditions.add(CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS.eq(criteria.getAddress()).or(CONTRACT_PARTICIPATE_ASSET_ISSUE.TO_ADDRESS.eq(criteria.getAddress())));
		}

		if (StringUtils.isNotBlank(criteria.getAssetName())) {
			conditions.add(CONTRACT_PARTICIPATE_ASSET_ISSUE.ASSET_NAME.eq(criteria.getAssetName()));
		}
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<AssetParticipationDTO> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(AssetParticipationDTO.class);
		
		
		
		ListDTO<AssetParticipationDTO, AssetParticipationCriteriaDTO> result = new ListDTO<AssetParticipationDTO, AssetParticipationCriteriaDTO>(criteria, items, totalCount);
		
		return result;
		

	}
	

	public AssetIssueDTO getAssetDetails(AssetIssueDetailCriteriaDTO assetIssueCriteria) {
		
		AssetIssueDTO result = this.dslContext.select(CONTRACT_ASSET_ISSUE.ID,CONTRACT_ASSET_ISSUE.OWNER_ADDRESS.as("issuer"),CONTRACT_ASSET_ISSUE.NAME,CONTRACT_ASSET_ISSUE.TOTAL_SUPPLY,CONTRACT_ASSET_ISSUE.TRX_NUM,CONTRACT_ASSET_ISSUE.NUM,CONTRACT_ASSET_ISSUE.START_TIME,CONTRACT_ASSET_ISSUE.END_TIME,CONTRACT_ASSET_ISSUE.DECAY_RATIO,CONTRACT_ASSET_ISSUE.VOTE_SCORE,CONTRACT_ASSET_ISSUE.DESCRIPTION,CONTRACT_ASSET_ISSUE.URL)
		.from(CONTRACT_ASSET_ISSUE).where(CONTRACT_ASSET_ISSUE.NAME.eq(assetIssueCriteria.getName()))
		.fetchOneInto(AssetIssueDTO.class);
		
		if (result!=null) {
		
		if (assetIssueCriteria.getTab().equals("tx")) {
			
			TransactionCriteriaDTO txCriteria = new TransactionCriteriaDTO();
			
			txCriteria.setLimit(assetIssueCriteria.getLimit());
			txCriteria.setPage(assetIssueCriteria.getPage());
			txCriteria.setAssetName(result.getName());
			
			ListDTO<TransactionDTO, TransactionCriteriaDTO> accountTransactions = this.txService.listTransactions(txCriteria);
			
			
			
			result.setTransactions(accountTransactions.getItems());		

			
		}else if (assetIssueCriteria.getTab().equals("tp")) {
			
			AssetParticipationCriteriaDTO participationCriteria = new AssetParticipationCriteriaDTO();
			participationCriteria.setLimit(assetIssueCriteria.getLimit());
			participationCriteria.setPage(assetIssueCriteria.getPage());
			participationCriteria.setAssetName(result.getName());
			
			ListDTO<AssetParticipationDTO, AssetParticipationCriteriaDTO> assetParticipations = listAssetParticipations(participationCriteria);
			
			result.setParticipations(assetParticipations.getItems());
			
		}
		
		

			result.setCriteria(assetIssueCriteria);			
		}

		
		return result;
	}
	
	
	private void prepareAssetIssueDTO(List<AssetIssueDTO> assets) {
		
		for(AssetIssueDTO asset:assets) {
			
			asset.setShortName(StringUtils.abbreviate(asset.getName().trim(), 25));
			
		}
		
	}
	
	
}

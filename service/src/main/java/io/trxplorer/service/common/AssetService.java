package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.job.QuickStatsJob;
import io.trxplorer.service.dto.account.TokenHolderModel;
import io.trxplorer.service.dto.asset.AssetIssueDTO;
import io.trxplorer.service.dto.asset.TokenCriteria;
import io.trxplorer.service.dto.asset.AssetIssueListCriteriaDTO;
import io.trxplorer.service.dto.asset.AssetParticipationCriteria;
import io.trxplorer.service.dto.asset.AssetParticipationDTO;
import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.service.dto.transaction.TransferModel;

@Singleton
public class AssetService {
	
	private DSLContext dslContext;
	private TransactionService txService;
	private QuickStatsJob quickStats;
	
	@Inject
	public AssetService(DSLContext dslContext,TransactionService txService,QuickStatsJob quickStats) {
		this.dslContext = dslContext;
		this.txService = txService;
		this.quickStats = quickStats;
	}
	
	public ListModel<AssetIssueDTO, AssetIssueListCriteriaDTO> listAssetIssues(AssetIssueListCriteriaDTO criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		Field<?> participantCountField = DSL.select(DSL.count()).from(CONTRACT_PARTICIPATE_ASSET_ISSUE).where(CONTRACT_PARTICIPATE_ASSET_ISSUE.ASSET_NAME.eq(CONTRACT_ASSET_ISSUE.NAME)).asField("totalParticipants");
		
		SelectJoinStep<?> listQuery = this.dslContext.select(CONTRACT_ASSET_ISSUE.ID,CONTRACT_ASSET_ISSUE.NAME,CONTRACT_ASSET_ISSUE.ABBR,CONTRACT_ASSET_ISSUE.TOTAL_SUPPLY,CONTRACT_ASSET_ISSUE.START_TIME,CONTRACT_ASSET_ISSUE.END_TIME,CONTRACT_ASSET_ISSUE.OWNER_ADDRESS.as("issuer"),participantCountField).from(CONTRACT_ASSET_ISSUE)
				.join(TRANSACTION).on(TRANSACTION.ID.eq(CONTRACT_ASSET_ISSUE.TRANSACTION_ID));
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(CONTRACT_ASSET_ISSUE);
		
		
		if (StringUtils.isNotBlank(criteria.getIssuer())) {
			conditions.add(CONTRACT_ASSET_ISSUE.OWNER_ADDRESS.eq(criteria.getIssuer()));
		}
		
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<AssetIssueDTO> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(AssetIssueDTO.class);
		
		prepareAssetIssueDTO(items);
		
		ListModel<AssetIssueDTO, AssetIssueListCriteriaDTO> result = new ListModel<AssetIssueDTO, AssetIssueListCriteriaDTO>(criteria, items, totalCount);
		
		return result;
		

	}

	
	public ListModel<AssetParticipationDTO, AssetParticipationCriteria> listAssetParticipations(AssetParticipationCriteria criteria) {
		
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
		
		
		
		ListModel<AssetParticipationDTO, AssetParticipationCriteria> result = new ListModel<AssetParticipationDTO, AssetParticipationCriteria>(criteria, items, totalCount);
		
		return result;
		

	}
	
	
	public ListModel<TransferModel, AssetParticipationCriteria> listTokenParticipations(AssetParticipationCriteria criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		
		conditions.add(CONTRACT_PARTICIPATE_ASSET_ISSUE.ASSET_NAME.eq(criteria.getAssetName()));
		conditions.add(CONTRACT_PARTICIPATE_ASSET_ISSUE.TRANSACTION_ID.eq(TRANSACTION.ID));

				
		SelectJoinStep<?> listQuery = this.dslContext.select(
				CONTRACT_PARTICIPATE_ASSET_ISSUE.OWNER_ADDRESS.as("from"),
				CONTRACT_PARTICIPATE_ASSET_ISSUE.TO_ADDRESS.as("to"),
				CONTRACT_PARTICIPATE_ASSET_ISSUE.AMOUNT,
				CONTRACT_PARTICIPATE_ASSET_ISSUE.ASSET_NAME.as("token"),
				TRANSACTION.TIMESTAMP
				)
					.from(CONTRACT_PARTICIPATE_ASSET_ISSUE,TRANSACTION);


		
		Integer totalCount = this.dslContext.select(DSL.count())
				.from(CONTRACT_PARTICIPATE_ASSET_ISSUE,TRANSACTION)
				.where(conditions)
				.fetchOneInto(Integer.class);
		
		List<TransferModel> items = listQuery.where(conditions).orderBy(TRANSACTION.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(TransferModel.class);
		
		
		ListModel<TransferModel, AssetParticipationCriteria> result = new ListModel<TransferModel,AssetParticipationCriteria>(criteria, items, totalCount);
		
		return result;
	}
	


	
	public ListModel<TransferModel, TokenCriteria> listTransfers(TokenCriteria criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		
		conditions.add(TRANSFER.TOKEN.eq(criteria.getName()));
		conditions.add(TRANSFER.TRANSACTION_ID.eq(TRANSACTION.ID));
		
		List<Field<?>> fields = new ArrayList<>(Arrays.asList(TRANSFER.fields()));
		fields.add(TRANSACTION.HASH);
				
		SelectJoinStep<?> listQuery = this.dslContext.select(fields)
					.from(TRANSFER,TRANSACTION);


		
		Integer totalCount = this.dslContext.select(DSL.count())
				.from(TRANSFER,TRANSACTION)
				.where(conditions)
				.fetchOneInto(Integer.class);
		
		List<TransferModel> items = listQuery.where(conditions).orderBy(TRANSFER.TIMESTAMP.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(TransferModel.class);
		
		
		ListModel<TransferModel, TokenCriteria> result = new ListModel<TransferModel, TokenCriteria>(criteria, items, totalCount);
		
		return result;
	}
	

	public ListModel<TokenHolderModel, TokenCriteria> listHolders(TokenCriteria criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		conditions.add(ACCOUNT_ASSET.ASSET_NAME.eq(criteria.getName()));
		conditions.add(ACCOUNT_ASSET.ACCOUNT_ID.eq(ACCOUNT.ID));
		
				
		SelectJoinStep<?> listQuery = this.dslContext.select(ACCOUNT_ASSET.BALANCE,ACCOUNT.ADDRESS)
					.from(ACCOUNT,ACCOUNT_ASSET);


		
		Integer totalCount = this.dslContext.select(DSL.count())
				.from(ACCOUNT,ACCOUNT_ASSET)
				.where(conditions)
				.fetchOneInto(Integer.class);
		
		List<TokenHolderModel> items = listQuery.where(conditions).orderBy(ACCOUNT_ASSET.BALANCE.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(TokenHolderModel.class);
		
		ListModel<TokenHolderModel, TokenCriteria> result = new ListModel<TokenHolderModel, TokenCriteria>(criteria, items, totalCount);
		
		return result;
	}
	

	public AssetIssueDTO getAssetDetails(TokenCriteria assetIssueCriteria) {
		
		AssetIssueDTO result = this.dslContext.select(CONTRACT_ASSET_ISSUE.ID,CONTRACT_ASSET_ISSUE.OWNER_ADDRESS.as("issuer"),CONTRACT_ASSET_ISSUE.NAME,CONTRACT_ASSET_ISSUE.TOTAL_SUPPLY,CONTRACT_ASSET_ISSUE.TRX_NUM,CONTRACT_ASSET_ISSUE.NUM,CONTRACT_ASSET_ISSUE.START_TIME,CONTRACT_ASSET_ISSUE.END_TIME,CONTRACT_ASSET_ISSUE.DECAY_RATIO,CONTRACT_ASSET_ISSUE.VOTE_SCORE,CONTRACT_ASSET_ISSUE.DESCRIPTION,CONTRACT_ASSET_ISSUE.URL,CONTRACT_ASSET_ISSUE.ABBR)
		.from(CONTRACT_ASSET_ISSUE).where(CONTRACT_ASSET_ISSUE.NAME.eq(assetIssueCriteria.getName()))
		.fetchOneInto(AssetIssueDTO.class);
		
	
		
		return result;
	}
	
	
	private void prepareAssetIssueDTO(List<AssetIssueDTO> assets) {
		
		for(AssetIssueDTO asset:assets) {
			
			asset.setShortName(StringUtils.abbreviate(asset.getName().trim(), 25));
			
		}
		
	}
	
	
}

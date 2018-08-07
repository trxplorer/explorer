package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;

import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.service.dto.node.NodeCriteriaDTO;
import io.trxplorer.service.dto.node.NodeDTO;

public class NodeService {

	private DSLContext dslContext;
	private Cache<String, Object> cache;
	
	@Inject
	public NodeService(DSLContext dslContext){
		this.dslContext = dslContext;
		this.cache = CacheBuilder.newBuilder()
			    .expireAfterAccess(5, TimeUnit.MINUTES)
			    .build();
	}
	
	public List<Map<String, Object>> getCountries(){
		
		List<Map<String, Object>> result = (List<Map<String, Object>>) this.cache.getIfPresent("countries");
		
		if (result==null) {
			result = this.dslContext.selectDistinct(NODE.COUNTRY,NODE.COUNTRY_CODE.as("code")).from(NODE).where(NODE.COUNTRY.isNotNull().and(NODE.COUNTRY_CODE.isNotNull())).orderBy(NODE.COUNTRY.asc()).fetchMaps();
			this.cache.put("countries", result);
		}
		
		return result;
	}
	
	public ListModel<NodeDTO, NodeCriteriaDTO> listNodes(NodeCriteriaDTO criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		if (StringUtils.isNotBlank(criteria.getCountry()) && !criteria.getCountry().equals("0")) {
			conditions.add(NODE.COUNTRY_CODE.eq(criteria.getCountry()));
		}
		
		if (StringUtils.isNotBlank(criteria.getIp())) {
			conditions.add(NODE.HOST.eq(criteria.getIp()));
		}
		
		SelectJoinStep<Record> listQuery = this.dslContext.select(NODE.fields()).from(NODE);
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(NODE);
	
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<NodeDTO> items = listQuery.where(conditions).orderBy(NODE.UP.desc(),NODE.LAST_UPDATED.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(NodeDTO.class);
		
		
		ListModel<NodeDTO, NodeCriteriaDTO> result = new ListModel<NodeDTO, NodeCriteriaDTO>(criteria, items, totalCount);
		
		
		
		return result;
	}

	
}

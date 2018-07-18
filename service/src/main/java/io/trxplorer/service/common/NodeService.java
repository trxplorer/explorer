package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSelectStep;
import org.jooq.impl.DSL;

import com.google.inject.Inject;

import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.service.dto.node.NodeCriteriaDTO;
import io.trxplorer.service.dto.node.NodeDTO;

public class NodeService {

	private DSLContext dslContext;

	@Inject
	public NodeService(DSLContext dslContext){
		this.dslContext = dslContext;
	}
	
	public ListModel<NodeDTO, NodeCriteriaDTO> listNodes(NodeCriteriaDTO criteria) {
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		
		SelectJoinStep<Record> listQuery = this.dslContext.select(NODE.fields()).from(NODE);
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(NODE);
	
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<NodeDTO> items = listQuery.where(conditions).orderBy(NODE.UP.desc(),NODE.LAST_UPDATED.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(NodeDTO.class);
		
		
		ListModel<NodeDTO, NodeCriteriaDTO> result = new ListModel<NodeDTO, NodeCriteriaDTO>(criteria, items, totalCount);
		
		
		
		return result;
	}

	
}

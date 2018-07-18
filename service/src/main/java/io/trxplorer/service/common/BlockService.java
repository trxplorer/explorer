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
import org.jooq.types.ULong;

import com.google.inject.Inject;

import io.trxplorer.service.dto.block.BlockCriteriaDTO;
import io.trxplorer.service.dto.block.BlockDTO;
import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.troncli.TronFullNodeCli;

public class BlockService {

	private DSLContext dslContext;
	private TransactionService txService;
	private TronFullNodeCli tronService;
	
	@Inject
	public BlockService(DSLContext dslContext,TronFullNodeCli tronFullNodeCli,TransactionService txService) {
		this.dslContext = dslContext;
		this.txService = txService;
	}
	
	public List<BlockDTO> getLastBlocks(){
		
		 List<BlockDTO> result = this.dslContext.select(BLOCK.NUM,BLOCK.HASH,BLOCK.WITNESS_ADDRESS)
		.from(BLOCK).orderBy(BLOCK.TIMESTAMP.desc()).limit(10).fetchInto(BlockDTO.class);
		
		
		return result;
	}
	
	public BlockDTO getBlockByNum(long num) {
		
		List<Field<?>> fields = new ArrayList<>(Arrays.asList(BLOCK.fields()));
		fields.add(DSL.select(DSL.max(BLOCK.NUM)).from(BLOCK).asField("maxNum"));
		
		BlockDTO block=this.dslContext.select(fields).from(BLOCK).where(BLOCK.NUM.eq(ULong.valueOf(num))).fetchOneInto(BlockDTO.class);
		
		if (block!=null) {
			setReward(Arrays.asList(block));			
		}
		
		return block;
	}
	
	public BlockDTO getBlockByHash(String hash) {

		List<Field<?>> fields = new ArrayList<>(Arrays.asList(BLOCK.fields()));
		fields.add(DSL.select(DSL.max(BLOCK.NUM)).from(BLOCK).asField("maxNum"));
		
		BlockDTO block=this.dslContext.select(fields).from(BLOCK).where(BLOCK.HASH.eq(hash)).fetchOneInto(BlockDTO.class);
		
		if (block!=null) {
			setReward(Arrays.asList(block));			
		}
		
		return block;
	}

	public BlockDTO getBlockByParentHash(String hash) {

		List<Field<?>> fields = new ArrayList<>(Arrays.asList(BLOCK.fields()));
		fields.add(DSL.select(DSL.max(BLOCK.NUM)).from(BLOCK).asField("maxNum"));
		
		BlockDTO block=this.dslContext.select(fields).from(BLOCK).where(BLOCK.PARENT_HASH.eq(hash)).fetchOneInto(BlockDTO.class);
		
		if (block!=null) {
			setReward(Arrays.asList(block));			
		}

		
		return block;
	}
	
	public ListModel<BlockDTO, BlockCriteriaDTO> listBlocks(BlockCriteriaDTO criteria){
		
		ArrayList<Condition> conditions = new ArrayList<>();
		
		 SelectJoinStep<?> listQuery = this.dslContext.select(BLOCK.NUM,BLOCK.CONFIRMED,BLOCK.TIMESTAMP,BLOCK.WITNESS_ADDRESS,BLOCK.TX_COUNT,BLOCK.WITNESS_ADDRESS.as("witness"))
		.from(BLOCK);
		
		
		SelectJoinStep<Record1<Integer>> countQuery = dslContext.select(DSL.count())
		.from(BLOCK);
		
		if (StringUtils.isNotBlank(criteria.getProducedBy())) {
			conditions.add(BLOCK.WITNESS_ADDRESS.eq(criteria.getProducedBy()));
		}
		
		Integer totalCount = countQuery.where(conditions).fetchOneInto(Integer.class);
		
		List<BlockDTO> items = listQuery.where(conditions).orderBy(BLOCK.NUM.desc()).limit(criteria.getLimit()).offset(criteria.getOffSet()).fetchInto(BlockDTO.class);
		
		setReward(items);
		
		ListModel<BlockDTO, BlockCriteriaDTO> result = new ListModel<BlockDTO, BlockCriteriaDTO>(criteria, items, totalCount);
		
		return result;
	}
	
	
	private void setReward(List<BlockDTO> blocks) {
		
		// Rewards are fixed for block production, right now always 32trx, may (or may not change with time)
		// If it changes handle it here based on block timestamp
		
		for(BlockDTO block:blocks) {
			block.setReward("32 TRX");
		}
		
	}


	
}

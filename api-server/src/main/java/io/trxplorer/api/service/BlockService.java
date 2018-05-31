package io.trxplorer.api.service;

import static io.trxplorer.model.Tables.*;

import org.jooq.DSLContext;

import com.google.inject.Inject;

import io.trxplorer.api.dto.block.BlockDTO;

public class BlockService {

	private DSLContext dslContext;
	

	
	@Inject
	public BlockService(DSLContext dslContext) {
		this.dslContext = dslContext;
	}


	public BlockDTO getLastBlock() {
	
		return this.dslContext.select(BLOCK.NUM,BLOCK.HASH,BLOCK.TIMESTAMP,BLOCK.TX_COUNT,BLOCK.SIZE,BLOCK.PARENT_HASH,BLOCK.WITNESS_ADDRESS)
				.from(BLOCK).orderBy(BLOCK.NUM.desc()).limit(1).fetchOneInto(BlockDTO.class);
	
	}


	
	
	
}

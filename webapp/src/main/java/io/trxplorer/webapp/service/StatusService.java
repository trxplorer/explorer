package io.trxplorer.webapp.service;

import static io.trxplorer.model.Tables.*;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;

import com.google.inject.Inject;

import io.trxplorer.troncli.TronCli;
import io.trxplorer.webapp.WebAppConfig;
import io.trxplorer.webapp.dto.status.StatusDTO;
import io.trxplorer.webapp.dto.status.StatusType;


public class StatusService {

	private DSLContext dslContext;
	private TronCli tronCli;
	private WebAppConfig config;
	
	@Inject
	public StatusService(WebAppConfig config,DSLContext dslContext,TronCli tronCli) {
		this.config = config;
		this.dslContext = dslContext;
		this.tronCli = tronCli;
	}
	
	
	public StatusDTO getStatus() {
		
		StatusDTO result = new StatusDTO();
		
		
		long tronLastBlock = this.tronCli.getLastBlock().getBlockHeader().getRawData().getNumber();
		long trxplorerLastBlock = this.dslContext.select(DSL.when(DSL.max(BLOCK.NUM).isNotNull(), DSL.max(BLOCK.NUM)).otherwise(DSL.val(ULong.valueOf(-1)))).from(BLOCK).fetchOneInto(Long.class);
		long blocksMissing = this.dslContext.select(DSL.count()).from(SYNC_BLOCK).where(SYNC_BLOCK.BLOCK_NUM.notIn(DSL.select(BLOCK.NUM).from(BLOCK))).fetchOneInto(Long.class);
		StatusType status = StatusType.WARNING;
		
		if (blocksMissing>=0 && blocksMissing<=this.config.getStatus().getOkBlocksLate()) {
			status = StatusType.OK;
		}else if (blocksMissing>0 && blocksMissing>=this.config.getStatus().getKoBlocksLate()) {
			status = StatusType.KO;
		}
		

		Long syncRate = this.dslContext.select(DSL.count().divide(24)).from(BLOCK).where("timestamp > DATE_SUB((select max(timestamp) from block), INTERVAL 24 HOUR)").fetchOneInto(Long.class);
		
		result.setStatus(status);
		result.setTronLastBlock(tronLastBlock);
		result.setTrxplorerLastBlock(trxplorerLastBlock);
		result.setTotalBlocksMissing(blocksMissing);
		result.setTrxplorerBlockRate(syncRate);
		
		return result;
	}
	
}

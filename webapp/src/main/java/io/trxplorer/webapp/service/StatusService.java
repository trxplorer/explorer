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
		long trxplorerLastBlock = this.dslContext.select(DSL.max(BLOCK.NUM)).from(BLOCK).fetchOneInto(Long.class);
		long blocksMissing = tronLastBlock-trxplorerLastBlock;
		StatusType status = StatusType.WARNING;
		
		if (blocksMissing>=0 && blocksMissing<=this.config.getStatus().getOkBlocksLate()) {
			status = StatusType.OK;
		}else if (blocksMissing>0 && blocksMissing>=this.config.getStatus().getKoBlocksLate()) {
			status = StatusType.KO;
		}
		

		
		result.setStatus(status);
		result.setTronLastBlock(tronLastBlock);
		result.setTrxplorerLastBlock(trxplorerLastBlock);
		result.setTotalBlocksMissing(blocksMissing);

		
		return result;
	}
	
}

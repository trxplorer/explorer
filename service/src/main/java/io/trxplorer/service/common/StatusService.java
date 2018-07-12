package io.trxplorer.service.common;

import static io.trxplorer.model.Tables.*;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.google.inject.Inject;

import io.trxplorer.config.WebAppConfig;
import io.trxplorer.service.dto.status.StatusDTO;
import io.trxplorer.service.dto.status.StatusType;
import io.trxplorer.troncli.TronFullNodeCli;



public class StatusService {

	private DSLContext dslContext;
	private TronFullNodeCli tronFullNodeCli;
	private WebAppConfig config;
	
	@Inject
	public StatusService(WebAppConfig config,DSLContext dslContext,TronFullNodeCli tronFullNodeCli) {
		this.config = config;
		this.dslContext = dslContext;
		this.tronFullNodeCli = tronFullNodeCli;
	}
	
	
	public StatusDTO getStatus() {
		
		StatusDTO result = new StatusDTO();
		
		
		long tronLastBlock = this.tronFullNodeCli.getLastBlock().getBlockHeader().getRawData().getNumber();
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

package io.trxplorer.api.route;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;

import io.trxplorer.api.dto.block.BlockDTO;
import io.trxplorer.api.service.BlockService;

public class BlockRoutes {

	private BlockService blockService;

	@Inject
	public BlockRoutes(BlockService blockService) {
		this.blockService = blockService;
	}
	
	/**
	 * Get the latest available block
	 * @param req
	 * @param res
	 * @return
	 * @throws Throwable
	 */
	@GET
	@Path(ApiAppRoutePaths.V1.BLOCK_LATEST)
	public BlockDTO broadcastTransaction(Request req,Response res) throws Throwable {
		req.param("toto");
	    
		return this.blockService.getLastBlock();
		
	}
	
	
}

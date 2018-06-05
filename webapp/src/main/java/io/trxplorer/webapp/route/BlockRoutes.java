package io.trxplorer.webapp.route;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.View;
import org.jooby.Route.Chain;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.webapp.dto.block.BlockCriteriaDTO;
import io.trxplorer.webapp.dto.block.BlockDTO;
import io.trxplorer.webapp.service.BlockService;

@Singleton
public class BlockRoutes {

	private BlockService blockService;

	@Inject
	public BlockRoutes(BlockService blockService) {
		this.blockService = blockService;
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.BLOCK_DETAIL)
	public void blockDetail(Request req,Response res,Chain chain) throws Throwable {
		
		String num = req.param("num").value(null);
		
		if (StringUtils.isBlank(num)) {
			chain.next(req, res);
			return;
		}
		
		View view = Results.html("block/block.detail");
		
		if (NumberUtils.isDigits(num)) {
			BlockDTO block = this.blockService.getBlockByNum(Long.valueOf(num));
			
			if (block==null) {
				chain.next(req, res);
			}else {
				view.put("block",block);			
				res.send(view);			
			}			
		}else {
			chain.next(req, res);
		}


	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.BLOCK_LIST)
	public void blockList(Request req,Response res) throws Throwable {
		
		Integer limit = req.param("limit").intValue(20);
		Integer page = req.param("page").intValue(1);
		
		String producedBy = req.param("producedBy").value(null);
		
		BlockCriteriaDTO criteria = new BlockCriteriaDTO();
		
		criteria.setProducedBy(producedBy);
		criteria.setLimit(limit);
		criteria.setPage(page);

		View view = Results.html("block/block.list");
		
		view.put("list",this.blockService.listBlocks(criteria));
		
		res.send(view);
	}
	
	
	
}

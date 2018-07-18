package io.trxplorer.webapp.route;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.Route.Chain;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.service.common.BlockService;
import io.trxplorer.service.dto.block.BlockCriteriaDTO;
import io.trxplorer.service.dto.block.BlockDTO;
import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.webapp.job.QuickStatsJob;

@Singleton
public class BlockRoutes {

	private BlockService blockService;
	private QuickStatsJob quickStats;
	
	@Inject
	public BlockRoutes(BlockService blockService,QuickStatsJob quickStats) {
		this.blockService = blockService;
		this.quickStats = quickStats;
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
		
		ListModel<BlockDTO, BlockCriteriaDTO> list = this.blockService.listBlocks(criteria);
		
		HashMap<String, Object> stats = new HashMap<>();
		stats.put("totalBlocks", list.getItems().get(0).getNum());
		stats.put("totalBlocks6h", quickStats.getBlocks6h());
		stats.put("totalBlocks24h", quickStats.getBlocks24h());
		stats.put("totalBlocksConfirmed", quickStats.getTotalBlocksConfirmed());
		
		view.put("list",list);
		view.put("stats",stats);
		
		res.send(view);
	}
	
	
	
}

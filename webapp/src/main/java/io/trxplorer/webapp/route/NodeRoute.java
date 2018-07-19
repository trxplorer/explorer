package io.trxplorer.webapp.route;

import java.util.HashMap;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.job.QuickStatsJob;
import io.trxplorer.service.common.NodeService;
import io.trxplorer.service.dto.node.NodeCriteriaDTO;

@Singleton
public class NodeRoute {

	private NodeService nodeService;
	private QuickStatsJob quickStats;
	

	@Inject
	public NodeRoute(NodeService nodeService,QuickStatsJob quickStats) {
		this.nodeService = nodeService;
		this.quickStats = quickStats;
	}
	
	
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.NODE_LIST)
	public void nodeList(Request req,Response res) throws Throwable {
		
		Integer limit = req.param("limit").intValue(500);
		Integer page = req.param("page").intValue(1);
		
	
		NodeCriteriaDTO criteria = new NodeCriteriaDTO();
		

		criteria.setLimit(limit);
		criteria.setPage(page);

		View view = Results.html("node/node.list");
		
		HashMap<String, Object> stats = new HashMap<>();
		stats.put("totalNodesUp", quickStats.getTotalNodesUp());
		stats.put("totalNodes24h", quickStats.getTotalNodes24h());
		stats.put("topNodeCountry", quickStats.getTopNodeCountry());

		
		view.put("list",this.nodeService.listNodes(criteria));
		view.put("stats",stats);
		
		
		res.send(view);
	}
	
	
	
}

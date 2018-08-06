package io.trxplorer.webapp.route;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.job.QuickStatsJob;
import io.trxplorer.service.common.NodeService;
import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.service.dto.node.NodeCriteriaDTO;
import io.trxplorer.service.dto.node.NodeDTO;

@Singleton
public class NodeRoute {

	private NodeService nodeService;
	private QuickStatsJob quickStats;
	private Cache<String, Object> cache;

	@Inject
	public NodeRoute(NodeService nodeService,QuickStatsJob quickStats) {
		this.nodeService = nodeService;
		this.quickStats = quickStats;
		this.cache = CacheBuilder.newBuilder()
			    .expireAfterAccess(1, TimeUnit.MINUTES)
			    .build();
	}
	
	
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.NODE_LIST)
	public void nodeList(Request req,Response res) throws Throwable {
		
		Integer limit = req.param("limit").intValue(500);
		Integer page = req.param("page").intValue(1);
		String ip = req.param("ip").value(null);
		String country = req.param("country").value(null);
		
		NodeCriteriaDTO criteria = new NodeCriteriaDTO();
		

		criteria.setLimit(300);
		criteria.setPage(page);
		criteria.setCountry(country);
		criteria.setIp(ip);
		
		View view = Results.html("node/node.list");
		
		HashMap<String, Object> stats = new HashMap<>();
		stats.put("totalNodesUp", quickStats.getTotalNodesUp());
		stats.put("totalNodes24h", quickStats.getTotalNodes24h());
		stats.put("topNodeCountry", quickStats.getTopNodeCountry());
		stats.put("nodesDiscovered", quickStats.getTotalNodesDiscovered());
		
		Object result = this.cache.getIfPresent(criteria.params().toString());
		
		if (result==null) {
			result = this.nodeService.listNodes(criteria);
			this.cache.put("nodes", result);
		}
		
		view.put("countries", this.nodeService.getCountries());
		view.put("list",result);
		view.put("stats",stats);
		
		
		res.send(view);
	}
	
	
	
}

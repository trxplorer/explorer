package io.trxplorer.webapp.route;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.webapp.dto.node.NodeCriteriaDTO;
import io.trxplorer.webapp.service.NodeService;

@Singleton
public class NodeRoute {

	private NodeService nodeService;

	@Inject
	public NodeRoute(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.NODE_LIST)
	public void blockList(Request req,Response res) throws Throwable {
		
		Integer limit = req.param("limit").intValue(500);
		Integer page = req.param("page").intValue(1);
		
	
		NodeCriteriaDTO criteria = new NodeCriteriaDTO();
		

		criteria.setLimit(limit);
		criteria.setPage(page);

		View view = Results.html("node/node.list");
		
		view.put("list",this.nodeService.listNodes(criteria));
		
		res.send(view);
	}
	
	
	
}

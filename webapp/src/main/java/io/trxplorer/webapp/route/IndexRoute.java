package io.trxplorer.webapp.route;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.webapp.dto.transaction.TransactionCriteriaDTO;
import io.trxplorer.webapp.service.BlockService;
import io.trxplorer.webapp.service.TransactionService;

@Singleton
public class IndexRoute {

	
	private BlockService blockService;
	private TransactionService txService;
	
	@Inject
	public IndexRoute(BlockService blockService,TransactionService txService) {
		this.blockService = blockService;
		this.txService = txService;
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.HOME)
	public void home(Request req,Response res) throws Throwable {
		
		View view = Results.html("index");
		
		TransactionCriteriaDTO txCriteria = new TransactionCriteriaDTO();
		txCriteria.setLimit(10);
		txCriteria.setPage(1);
		
		view.put("lastBlocks",this.blockService.getLastBlocks());
		view.put("lastTxs",this.txService.listTransactions(txCriteria).getItems());
		
		res.send(view);
	}
	
}

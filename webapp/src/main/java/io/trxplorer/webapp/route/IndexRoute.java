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
import io.trxplorer.webapp.service.AccountService;
import io.trxplorer.webapp.service.BlockService;
import io.trxplorer.webapp.service.MarketService;
import io.trxplorer.webapp.service.TransactionService;

@Singleton
public class IndexRoute {

	
	private BlockService blockService;
	private TransactionService txService;
	private MarketService marketService;
	private AccountService accountService;
	
	@Inject
	public IndexRoute(BlockService blockService,TransactionService txService,MarketService marketService,AccountService accountService) {
		this.blockService = blockService;
		this.txService = txService;
		this.marketService = marketService;
		this.accountService = accountService;
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.HOME)
	public void home(Request req,Response res) throws Throwable {
		
		View view = Results.html("index");
		
		TransactionCriteriaDTO txCriteria = new TransactionCriteriaDTO();
		txCriteria.setLimit(10);
		txCriteria.setPage(1);
		
		view.put("lastTxs",this.txService.listTransactions(txCriteria).getItems());
		view.put("price",this.marketService.getCurrentAvgPrice());
		view.put("totalAccounts",this.accountService.getTotalAccount());
		view.put("lastestAccounts",this.accountService.getLatestAccounts(10));
		view.put("latestVotes",this.txService.getLastestVotes(10));
		view.put("lastTxCount",this.txService.getTotalTxLast24h());
		view.put("lastVotesCount",this.txService.getTotalVotesLast24h());
		

		res.send(view);
	}
	
}

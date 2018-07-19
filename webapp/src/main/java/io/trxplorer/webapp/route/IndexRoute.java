package io.trxplorer.webapp.route;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.job.QuickStatsJob;
import io.trxplorer.service.common.AccountService;
import io.trxplorer.service.common.TransactionService;

@Singleton
public class IndexRoute {

	

	private TransactionService txService;
	private AccountService accountService;
	private QuickStatsJob quickStats;
	
	@Inject
	public IndexRoute(TransactionService txService,AccountService accountService,QuickStatsJob quickStats) {
		this.txService = txService;
		this.accountService = accountService;
		this.quickStats = quickStats;
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.HOME)
	public void home(Request req,Response res) throws Throwable {
		
		View view = Results.html("index");
		

		
		view.put("lastTxs",this.txService.getLatestTransactions(10));
		view.put("price",this.quickStats.getMarketData().get("price"));
		view.put("totalAccounts",this.quickStats.getTotalAccounts());
		view.put("lastestAccounts",this.accountService.getLatestAccounts(10));
		view.put("latestVotes",this.txService.getLastestVotes(10));
		view.put("lastTxCount",this.quickStats.getTx24h());
		view.put("lastVotesCount",this.quickStats.getVotes().get("last24hCount"));
		

		res.send(view);
	}
	
}

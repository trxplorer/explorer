package io.trxplorer.webapp.route;

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
import io.trxplorer.service.common.AccountService;
import io.trxplorer.service.common.TransactionService;

@Singleton
public class IndexRoute {

	

	private TransactionService txService;
	private AccountService accountService;
	private QuickStatsJob quickStats;
	
	private Cache<String, Object> cache;
	
	@Inject
	public IndexRoute(TransactionService txService,AccountService accountService,QuickStatsJob quickStats) {
		this.txService = txService;
		this.accountService = accountService;
		this.quickStats = quickStats;
		this.cache = CacheBuilder.newBuilder()
			    .expireAfterAccess(5, TimeUnit.SECONDS)
			    .build();
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.HOME)
	public void home(Request req,Response res) throws Throwable {
		
		View view = Results.html("index");
		
		Object latestTransactions = this.cache.getIfPresent("lastTxs");
		Object latestAccounts = this.cache.getIfPresent("lastestAccounts");
		Object latestVotes = this.cache.getIfPresent("latestVotes");
		
		if (latestTransactions==null) {
			latestTransactions = this.txService.getLatestTransactions(10);
			this.cache.put("lastTxs", latestTransactions);
		}
		
		if (latestAccounts==null) {
			latestAccounts = this.accountService.getLatestAccounts(10);
			this.cache.put("lastestAccounts", latestAccounts);
		}
		
		if (latestVotes==null) {
			latestVotes = this.txService.getLastestVotes(10);
			this.cache.put("latestVotes", latestVotes);
		}
		
		
		
		view.put("lastTxs",latestTransactions);
		view.put("price",this.quickStats.getMarketData().get("price"));
		view.put("totalAccounts",this.quickStats.getTotalAccounts());
		view.put("lastestAccounts",latestAccounts);
		view.put("latestVotes",latestVotes);
		view.put("lastTxCount",this.quickStats.getTx24h());
		view.put("lastVotesCount",this.quickStats.getVotes().get("last24hCount"));
		

		res.send(view);
	}
	
}

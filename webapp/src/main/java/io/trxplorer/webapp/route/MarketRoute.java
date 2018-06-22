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
import io.trxplorer.webapp.job.QuickStatsJob;
import io.trxplorer.webapp.service.MarketService;
import io.trxplorer.webapp.service.NodeService;

@Singleton
public class MarketRoute {

	private MarketService marketService;
	private QuickStatsJob quickStats;

	@Inject
	public MarketRoute(MarketService marketService,QuickStatsJob quickStats) {
		this.marketService = marketService;
		this.quickStats = quickStats;
	}
	
	
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.MARKET_LIST)
	public void marketList(Request req,Response res) throws Throwable {
		
		View view = Results.html("market/market.list");
		
		view.put("markets",this.marketService.getMarkets());
		
		view.put("stats",this.quickStats.getMarketData());
		
		res.send(view);
	}
	
	
	
}

package io.trxplorer.webapp.route;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.Route.Chain;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.job.QuickStatsJob;
import io.trxplorer.service.common.AssetService;
import io.trxplorer.service.dto.asset.AssetIssueDTO;
import io.trxplorer.service.dto.asset.AssetIssueListCriteriaDTO;
import io.trxplorer.service.dto.asset.TokenCriteria;

@Singleton
public class AssetRoutes {

	private AssetService assetService;
	private QuickStatsJob quickStats;
	
	@Inject
	public AssetRoutes(AssetService blockService,QuickStatsJob quickStats) {
		this.assetService = blockService;
		this.quickStats = quickStats;

	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.ASSET_DETAIL)
	public void assetDetail(Request req,Response res,Chain chain) throws Throwable {

		Integer limit = req.param("limit").intValue(20);
		Integer page = req.param("page").intValue(1);
		
		String assetName = req.param("assetName").value(null); 
		
		View view = Results.html("asset/asset.detail");
		
		TokenCriteria criteria = new TokenCriteria();
		criteria.setLimit(limit);
		criteria.setPage(page);
		criteria.setName(assetName);


		AssetIssueDTO assetIssue = this.assetService.getAssetDetails(criteria);
		
		if (assetIssue==null) {
			chain.next(req, res);
		}else {
			view.put("asset",assetIssue);
			res.send(view);			
		}

	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.ASSET_LIST)
	public void assetList(Request req,Response res) throws Throwable {

		Integer page = req.param("page").intValue(1);
		

		
		AssetIssueListCriteriaDTO criteria = new AssetIssueListCriteriaDTO();
		

		criteria.setLimit(100);
		criteria.setPage(page);

		View view = Results.html("asset/asset.list");
		
		
		view.put("list",this.assetService.listAssetIssues(criteria));
		
		HashMap<String, Object> stats = new HashMap<>();
		stats.put("totalTokens", quickStats.getTotalTokens());
		stats.put("totalTokens24h", quickStats.getTotalTokens24h());
		
		view.put("stats",stats);
		
		
		res.send(view);
	}
	
	
	
}

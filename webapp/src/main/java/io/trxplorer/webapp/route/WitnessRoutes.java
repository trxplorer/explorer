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

import io.trxplorer.service.common.WitnessService;
import io.trxplorer.service.dto.witness.WitnessListCriteriaDTO;
import io.trxplorer.webapp.job.QuickStatsJob;

@Singleton
public class WitnessRoutes {

	private WitnessService witnessService;
	private QuickStatsJob quickStats;

	@Inject
	public WitnessRoutes(WitnessService transactionService,QuickStatsJob quickStats) {
		this.witnessService = transactionService;
		this.quickStats = quickStats;
	}
	
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.REPRESENTATIVE_LIST)
	public void superRepresentativeList(Request req,Response res) throws Throwable {
		

		Integer page = req.param("page").intValue(1);
		
		
		WitnessListCriteriaDTO srCriteria = new WitnessListCriteriaDTO();
		
		srCriteria.setLimit(200);
		srCriteria.setPage(page);
		
		
		View view = Results.html("witness/witness.list");
		
		view.put("list",this.witnessService.listWitnesses(srCriteria));

		HashMap<String, Object> stats = new HashMap<>();
		stats.put("totalRepresentatives", quickStats.getTotalRepresentative());
		stats.put("totalRepresentatives24h", quickStats.getTotalRepresentative24h());
		stats.put("bestRep6h", quickStats.getBestRep6h());
		stats.put("bestRepAll", quickStats.getBestRepAll());
		
		view.put("stats",stats);
		
		res.send(view);
	}
	
	
	
}

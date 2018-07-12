package io.trxplorer.webapp.route;

import java.util.HashMap;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.Route.Chain;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.service.common.BlockService;
import io.trxplorer.service.common.VoteService;
import io.trxplorer.service.dto.block.BlockCriteriaDTO;
import io.trxplorer.service.dto.vote.VotingRoundListCriteriaDTO;
import io.trxplorer.webapp.job.QuickStatsJob;

@Singleton
public class VoteRoundRoutes {

	private VoteService voteService;
	private QuickStatsJob quickStats;
	
	@Inject
	public VoteRoundRoutes(VoteService voteService,QuickStatsJob quickStats) {
		this.voteService = voteService;
		this.quickStats = quickStats;
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.VOTE_LIST_LIVE)
	public void votelist(Request req,Response res,Chain chain) throws Throwable {
		

		Integer limit = req.param("limit").intValue(20);
		Integer page = req.param("page").intValue(1);
		
		VotingRoundListCriteriaDTO criteria = new VotingRoundListCriteriaDTO();
		criteria.setLimit(limit);
		criteria.setPage(page);		
		
		View view = Results.html("vote/vote.list");
		
		view.put("list",this.voteService.listRounds(criteria));
		
		res.send(view);

	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.VOTE_ROUND_DETAIL)
	public void voteRoundDetail(Request req,Response res,Chain chain) throws Throwable {
		

		Integer round = req.param("round").intValue(0);

		
		View view = Results.html("vote/vote.detail");
		
		view.put("vr",this.voteService.getVotingRoundByNum(round));
		
		res.send(view);

	}

	
	
	
}

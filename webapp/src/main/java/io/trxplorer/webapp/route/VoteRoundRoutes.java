package io.trxplorer.webapp.route;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.Route.Chain;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.job.QuickStatsJob;
import io.trxplorer.service.common.AccountService;
import io.trxplorer.service.common.VoteService;
import io.trxplorer.service.dto.vote.VotingRoundListCriteria;

@Singleton
public class VoteRoundRoutes {

	private VoteService voteService;
	private QuickStatsJob quickStats;
	private AccountService accountService;
	
	@Inject
	public VoteRoundRoutes(VoteService voteService,AccountService accountService,QuickStatsJob quickStats) {
		this.voteService = voteService;
		this.quickStats = quickStats;
		this.accountService = accountService;
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.VOTE_LIST_LIVE)
	public void votelist(Request req,Response res,Chain chain) throws Throwable {
		

		Integer limit = req.param("limit").intValue(20);
		Integer page = req.param("page").intValue(1);
		
		VotingRoundListCriteria criteria = new VotingRoundListCriteria();
		criteria.setLimit(limit);
		criteria.setPage(page);		
		
		View view = Results.html("vote/vote.list");
		
		view.put("list",this.voteService.listRounds(criteria));
		view.put("currentRound",this.quickStats.getCurrentVotingRound());
		
		
		res.send(view);

	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.VOTE_ROUND_DETAIL)
	public void voteRoundDetail(Request req,Response res,Chain chain) throws Throwable {
		

		Integer round = req.param("round").intValue(0);

		
		View view = Results.html("vote/vote.detail");
		
		view.put("vr",this.voteService.getVotingRoundByNum(round));
		view.put("currentRound",this.quickStats.getCurrentVotingRound());
		
		res.send(view);

	}

	
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.VOTE_ROUND_ADDRESS_DETAIL)
	public void voteRoundAddressDetail(Request req,Response res,Chain chain) throws Throwable {
		
		Integer round = req.param("round").intValue(0);
		String address = req.param("address").value();

		
		View view = Results.html("vote/vote.address.detail");
		
		
		view.put("vrs",this.voteService.getVotingRoundStats(round, address));
		view.put("currentRound",this.quickStats.getCurrentVotingRound());
		
		res.send(view);

	}
	
	
	
}

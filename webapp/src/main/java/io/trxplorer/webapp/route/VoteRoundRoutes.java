package io.trxplorer.webapp.route;

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
import io.trxplorer.service.common.VoteService;
import io.trxplorer.service.dto.vote.VotingRoundListCriteria;

@Singleton
public class VoteRoundRoutes {

	private VoteService voteService;
	private QuickStatsJob quickStats;
	private Cache<String, Object> genesisVotesCache;
	
	@Inject
	public VoteRoundRoutes(VoteService voteService,QuickStatsJob quickStats) {
		this.voteService = voteService;
		this.quickStats = quickStats;
		this.genesisVotesCache = CacheBuilder.newBuilder()
			    .expireAfterAccess(1, TimeUnit.MINUTES)
			    .build();
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
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.GENESIS_VOTES)	
	public void listGenesisVotes(Request req,Response res,Chain chain) throws Throwable {
		
		View view = Results.html("vote/genesis.votes");
		
		Object votes = this.genesisVotesCache.getIfPresent("votes");
		
		if (votes==null) {
			votes = this.voteService.listGenesisWitnessesVotes();
		}
		
		view.put("data", votes);	
		
		res.send(view);
	}
	
	
	
}

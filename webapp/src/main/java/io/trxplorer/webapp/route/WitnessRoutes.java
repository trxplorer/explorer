package io.trxplorer.webapp.route;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.webapp.dto.witness.WitnessListCriteriaDTO;
import io.trxplorer.webapp.service.WitnessService;

@Singleton
public class WitnessRoutes {

	private WitnessService witnessService;

	@Inject
	public WitnessRoutes(WitnessService transactionService) {
		this.witnessService = transactionService;
	}
	
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.REPRESENTATIVE_LIST)
	public void representativeList(Request req,Response res) throws Throwable {
		
		Integer limit = req.param("limit").intValue(20);
		Integer page = req.param("page").intValue(1);
		
		
		WitnessListCriteriaDTO criteria = new WitnessListCriteriaDTO();
		
		criteria.setLimit(limit);
		criteria.setPage(page);
		criteria.setSuperRepresentative(false);
		criteria.setRandomCandidates(true);
		
		View view = Results.html("witness/witness.list");
		
		view.put("list",this.witnessService.listWitnesses(criteria));
		
		res.send(view);
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.SUPER_REPRESENTATIVE_LIST)
	public void superRepresentativeList(Request req,Response res) throws Throwable {
		
		Integer limit = req.param("limit").intValue(20);
		Integer page = req.param("page").intValue(1);
		
		
		WitnessListCriteriaDTO criteria = new WitnessListCriteriaDTO();
		
		criteria.setLimit(limit);
		criteria.setPage(page);
		criteria.setSuperRepresentative(true);
		
		View view = Results.html("witness/sr.list");
		
		WitnessListCriteriaDTO candidatesCriteria = new WitnessListCriteriaDTO();
		candidatesCriteria.setLimit(15);
		candidatesCriteria.setPage(1);
		candidatesCriteria.setRandomCandidates(true);
		candidatesCriteria.setSuperRepresentative(false);
		
		view.put("list",this.witnessService.listWitnesses(criteria));
		view.put("candidates", this.witnessService.listWitnesses(candidatesCriteria).getItems());
		
		res.send(view);
	}
	
	
	
}

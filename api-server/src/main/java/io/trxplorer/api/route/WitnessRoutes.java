package io.trxplorer.api.route;

import java.util.Optional;

import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.api.dto.account.AccountInfo;
import io.trxplorer.api.dto.common.ListResult;
import io.trxplorer.api.dto.witness.WitnessDTO;
import io.trxplorer.api.dto.witness.WitnessListCriteriaDTO;
import io.trxplorer.api.service.WitnessService;

@Singleton
public class WitnessRoutes {

	private WitnessService witnessService;

	@Inject
	public WitnessRoutes(WitnessService witnessService) {
		this.witnessService = witnessService;
	}
	


	/**
	 * Get basic informations on account
	 * @param address
	 * @return {@link AccountInfo}
	 * @throws Throwable
	 */
	@GET
	@Path(ApiAppRoutePaths.V1.WITNESS_ALL)
	public ListResult<WitnessDTO, WitnessListCriteriaDTO> all(Optional<Boolean> isSR,Optional<Integer> limit,Optional<Integer> page) throws Throwable {
		

		
		WitnessListCriteriaDTO criteria = new WitnessListCriteriaDTO();
		
		criteria.setLimit(limit.orElse(20));
		criteria.setPage(page.orElse(1));
		criteria.setSuperRepresentative(isSR.orElse(null));
		
		
		return this.witnessService.listWitnesses(criteria);		
	}
	




	
}

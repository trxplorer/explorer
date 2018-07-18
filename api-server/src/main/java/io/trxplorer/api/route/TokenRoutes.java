package io.trxplorer.api.route;

import java.util.Optional;

import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;

import io.trxplorer.service.common.AssetService;
import io.trxplorer.service.dto.account.TokenHolderModel;
import io.trxplorer.service.dto.asset.AssetParticipationCriteria;
import io.trxplorer.service.dto.asset.TokenCriteria;
import io.trxplorer.service.dto.common.ListModel;
import io.trxplorer.service.dto.transaction.TransferModel;

public class TokenRoutes {

	private AssetService tokenService;

	@Inject
	public TokenRoutes(AssetService blockService) {
		this.tokenService = blockService;
	}
	
	
	@GET
	@Path(ApiAppRoutePaths.V1.TOKEN_TRANSFERS)
	public ListModel<TransferModel, TokenCriteria> listTransfers(String token,Optional<Integer> page) throws Throwable {
		
		
		
		TokenCriteria criteria = new TokenCriteria();
		
		criteria.setLimit(20);
		criteria.setPage(page.orElse(1));
		
		criteria.setName(token);
	    
		
		
		return this.tokenService.listTransfers(criteria);
		
	}
	
	
	
	@GET
	@Path(ApiAppRoutePaths.V1.TOKEN_PARTICIPANTS)
	public ListModel<TransferModel, AssetParticipationCriteria> listParticipations(String token,Optional<Integer> page) throws Throwable {
		
		
		
		AssetParticipationCriteria criteria = new AssetParticipationCriteria();
		
		criteria.setLimit(20);
		criteria.setPage(page.orElse(1));
		
		criteria.setAssetName(token);
	    
		
		
		return this.tokenService.listTokenParticipations(criteria);
		
	}	
	
	@GET
	@Path(ApiAppRoutePaths.V1.TOKEN_HOLDERS)
	public ListModel<TokenHolderModel, TokenCriteria> listHolders(String token,Optional<Integer> page) throws Throwable {
		
		
		
		TokenCriteria criteria = new TokenCriteria();
		
		criteria.setLimit(20);
		criteria.setPage(page.orElse(1));
		
		criteria.setName(token);
	    
		
		
		return this.tokenService.listHolders(criteria);
		
	}	
	
}

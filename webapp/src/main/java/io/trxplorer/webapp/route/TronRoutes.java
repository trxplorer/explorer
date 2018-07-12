package io.trxplorer.webapp.route;

import org.jooby.mvc.Body;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;
import org.tron.common.utils.ByteArray;

import com.google.inject.Inject;

import io.trxplorer.service.dto.tron.TronBroadcastRequestDTO;
import io.trxplorer.troncli.TronFullNodeCli;
import io.trxplorer.troncli.wallet.BroadcastResult;

public class TronRoutes {

	private TronFullNodeCli tronFullNodeCli;
	
	@Inject
	public TronRoutes(TronFullNodeCli tronService) {
		this.tronFullNodeCli = tronService;
	}
	
	@POST
	@Path(TRXPlorerRoutePaths.Front.TRON_API_BROADCAST_TX)
	public BroadcastResult broadcastTransaction(@Body TronBroadcastRequestDTO request) throws Throwable {

	    final byte[] bytes = ByteArray.fromHexString(request.getPayload());
	    
		return this.tronFullNodeCli.broadcastTransaction(bytes);
	}
	
	
}

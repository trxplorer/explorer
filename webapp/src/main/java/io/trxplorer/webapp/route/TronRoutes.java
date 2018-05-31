package io.trxplorer.webapp.route;

import org.jooby.mvc.Body;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;
import org.tron.common.utils.ByteArray;

import com.google.inject.Inject;

import io.trxplorer.troncli.TronCli;
import io.trxplorer.troncli.wallet.BroadcastResult;
import io.trxplorer.webapp.dto.tron.TronBroadcastRequestDTO;

public class TronRoutes {

	private TronCli tronCli;
	
	@Inject
	public TronRoutes(TronCli tronService) {
		this.tronCli = tronService;
	}
	
	@POST
	@Path(TRXPlorerRoutePaths.Front.TRON_API_BROADCAST_TX)
	public BroadcastResult broadcastTransaction(@Body TronBroadcastRequestDTO request) throws Throwable {

	    final byte[] bytes = ByteArray.fromHexString(request.getPayload());
	    
		return this.tronCli.broadcastTransaction(bytes);
	}
	
	
}

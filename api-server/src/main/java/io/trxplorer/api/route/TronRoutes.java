package io.trxplorer.api.route;

import org.apache.commons.lang3.StringUtils;
import org.jooby.mvc.Body;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;
import org.tron.common.utils.ByteArray;

import com.google.inject.Inject;

import io.trxplorer.api.ApiAppConfig;
import io.trxplorer.api.dto.request.TronBroadcastRequest;
import io.trxplorer.troncli.TronCli;
import io.trxplorer.troncli.wallet.BroadcastResult;

public class TronRoutes {

	private TronCli tronCli;
	private ApiAppConfig config;
	
	@Inject
	public TronRoutes(TronCli tronService,ApiAppConfig config) {
		this.tronCli = tronService;
		this.config = config;
	}
	
	@POST
	@Path(ApiAppRoutePaths.V1.TRON_BROADCAST)
	public BroadcastResult broadcastTransaction(@Body TronBroadcastRequest request) throws Throwable {

	    final byte[] bytes = ByteArray.fromHexString(request.getPayload());
	    
	    if (StringUtils.isBlank(request.getNode())) {
			return this.tronCli.broadcastTransaction(bytes);	    	
	    }else {
	    	
	    	TronCli cli = new TronCli(request.getNode(),this.config.isTronMainNet());
	    	
	    	return cli.broadcastTransaction(bytes);
	    	
	    }
	    

	}
	
	
}

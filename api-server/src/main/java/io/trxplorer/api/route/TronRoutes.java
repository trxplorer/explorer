package io.trxplorer.api.route;

import org.apache.commons.lang3.StringUtils;
import org.jooby.mvc.Body;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;
import org.tron.common.utils.ByteArray;

import com.google.inject.Inject;

import io.trxplorer.api.ApiAppConfig;
import io.trxplorer.api.dto.request.TronBroadcastRequest;
import io.trxplorer.troncli.TronFullNodeCli;
import io.trxplorer.troncli.wallet.BroadcastResult;

public class TronRoutes {

	private TronFullNodeCli tronFullNodeCli;
	private ApiAppConfig config;
	
	@Inject
	public TronRoutes(TronFullNodeCli tronService,ApiAppConfig config) {
		this.tronFullNodeCli = tronService;
		this.config = config;
	}
	
	@POST
	@Path(ApiAppRoutePaths.V1.TRON_BROADCAST)
	public BroadcastResult broadcastTransaction(@Body TronBroadcastRequest request) throws Throwable {

	    final byte[] bytes = ByteArray.fromHexString(request.getPayload());
	    
	    if (StringUtils.isBlank(request.getNode())) {
			return this.tronFullNodeCli.broadcastTransaction(bytes);	    	
	    }else {
	    	
	    	TronFullNodeCli cli = new TronFullNodeCli(request.getNode(),this.config.isTronMainNet());
	    	
	    	return cli.broadcastTransaction(bytes);
	    	
	    }
	    

	}
	
	
}

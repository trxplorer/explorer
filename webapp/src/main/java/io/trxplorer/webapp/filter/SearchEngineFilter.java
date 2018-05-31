package io.trxplorer.webapp.filter;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Route.Chain;
import org.jooby.Route.Filter;

import io.trxplorer.webapp.WebAppConfig;

public class SearchEngineFilter implements Filter{



	@Override
	public void handle(Request req, Response rsp, Chain chain) throws Throwable {
		
		WebAppConfig config = req.require(WebAppConfig.class);
		
		req.set("seconfig", config.getSearchEngine());
		
		
		chain.next(req, rsp);
		
	}
	
}

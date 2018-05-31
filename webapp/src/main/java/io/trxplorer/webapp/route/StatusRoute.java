package io.trxplorer.webapp.route;

import org.jooby.Request;
import org.jooby.Response;
import org.jooby.Results;
import org.jooby.View;
import org.jooby.mvc.GET;
import org.jooby.mvc.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.webapp.service.StatusService;

@Singleton
public class StatusRoute {

	private StatusService statusService;

	@Inject
	public StatusRoute(StatusService statusService) {
		this.statusService = statusService;
	}
	
	@GET
	@Path(TRXPlorerRoutePaths.Front.STATUS)
	public void home(Request req,Response res) throws Throwable {
		
		View view = Results.html("status");
		
		view.put("status",this.statusService.getStatus());
		
		res.send(view);
	}
	
}

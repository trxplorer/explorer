package io.trxplorer.api;

import org.jooby.Jooby;
import org.jooby.apitool.ApiTool;
import org.jooby.flyway.Flywaydb;
import org.jooby.jdbc.Jdbc;
import org.jooby.jooq.jOOQ;
import org.jooby.json.Jackson;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;

import io.trxplorer.api.route.AccountRoutes;
import io.trxplorer.api.route.BlockRoutes;
import io.trxplorer.api.route.TokenRoutes;
import io.trxplorer.api.route.TransactionRoutes;
import io.trxplorer.api.route.TronRoutes;
import io.trxplorer.api.route.VotingRoundRoutes;
import io.trxplorer.api.route.WitnessRoutes;

public class ApiApp extends Jooby {

	{

		use(new Jdbc());
		use(new jOOQ());

		use(new Jackson());
		use(new Flywaydb());

		
		
		use("*", (req,res,chain)->{

			//CORS
			
			res.header("Access-Control-Allow-Origin", "*");
			res.header("Access-Control-Allow-Headers", "origin, content-type, accept");
			
			if (req.method().equals("OPTIONS")) {
				res.send(true);
				return;
			}
			
			
			chain.next(req, res);

			
		});
		
		onStart(registry -> {
			
			DSLContext dslContext = registry.require(DSLContext.class);

			Settings settings = new Settings();
			settings.setRenderSchema(false);

			dslContext.configuration().set(settings);

		});

		get("/",(req,res)->{
			res.redirect("/doc");
		});
		
		// routes
		use(VotingRoundRoutes.class);
		use(WitnessRoutes.class);
		use(BlockRoutes.class);
		use(AccountRoutes.class);
		use(TronRoutes.class);
		use(TransactionRoutes.class);
		use(TokenRoutes.class);
		
		use(new ApiTool()
				 .filter(route -> {
				      return route.pattern().startsWith("/v1");
			    })
				.raml("/doc").swagger("/swagger"));

	}

	public static void main(final String... args) {
		run(ApiApp::new, args);
	}

}

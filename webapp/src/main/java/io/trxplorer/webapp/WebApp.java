package io.trxplorer.webapp;

import org.jooby.Jooby;
import org.jooby.flyway.Flywaydb;
import org.jooby.jdbc.Jdbc;
import org.jooby.jooq.jOOQ;
import org.jooby.json.Jackson;
import org.jooby.pebble.Pebble;
import org.jooby.quartz.Quartz;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;

import io.trxplorer.webapp.filter.SearchEngineFilter;
import io.trxplorer.webapp.job.ChartGeneratorJob;
import io.trxplorer.webapp.route.AccountRoutes;
import io.trxplorer.webapp.route.AssetRoutes;
import io.trxplorer.webapp.route.BlockRoutes;
import io.trxplorer.webapp.route.ChartRoutes;
import io.trxplorer.webapp.route.IndexRoute;
import io.trxplorer.webapp.route.SearchRoutes;
import io.trxplorer.webapp.route.StatusRoute;
import io.trxplorer.webapp.route.TransactionRoutes;
import io.trxplorer.webapp.route.TronRoutes;
import io.trxplorer.webapp.route.WitnessRoutes;

public class WebApp extends Jooby {

	{

		assets("/img/**","/static/assets/img/{0}");
		assets("/js/**","/static/assets/js/{0}");
		assets("/css/**","/static/assets/css/{0}");
		assets("favicon.ico","/static/assets/favicon.ico");
		assets("/robots.txt","/static/robots.txt");
		
		use(new Pebble("templates", ".html"));
		
		use(new WebAppModule());


		use(new Jdbc());
		use(new jOOQ());

		use(new Jackson());
		use(new Flywaydb());

		use(new Quartz(ChartGeneratorJob.class));
		
		on("dev", ()->{
			
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
			
			
		});
		
		onStart(registry -> {
			
			DSLContext dslContext = registry.require(DSLContext.class);

			Settings settings = new Settings();
			settings.setRenderSchema(false);

			dslContext.configuration().set(settings);

		});

		
		// filters
		use("*","/**",new SearchEngineFilter());
				
		
		// routes
		use(IndexRoute.class);
		use(BlockRoutes.class);
		use(TransactionRoutes.class);
		use(AccountRoutes.class);
		use(AssetRoutes.class);
		use(WitnessRoutes.class);
		use(ChartRoutes.class);
		use(SearchRoutes.class);
		use(StatusRoute.class);
		use(TronRoutes.class);
	}

	public static void main(final String... args) {
		run(WebApp::new, args);
	}

}

package io.trxplorer.searchengine;

import org.jooby.Jooby;
import org.jooby.flyway.Flywaydb;
import org.jooby.jdbc.Jdbc;
import org.jooby.jooq.jOOQ;
import org.jooby.json.Jackson;
import org.jooby.quartz.Quartz;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;

import io.redisearch.Query;
import io.redisearch.SearchResult;
import io.redisearch.client.Client;
import io.trxplorer.searchengine.job.IndexerJob;

public class SearchEngineApp extends Jooby {

	{



		use(new Jdbc());
		use(new jOOQ());

		
		use(new Flywaydb());

		use(new RedisModule());
		
		use(new Jackson());
		
		use(new Quartz(IndexerJob.class));
		
		onStart(registry -> {
			
			SearchEngineConfig config = registry.require(SearchEngineConfig.class);
			
			DSLContext dslContext = registry.require(DSLContext.class);

			Settings settings = new Settings();
			settings.setRenderSchema(false);

			dslContext.configuration().set(settings);

		});

		get("/search", (req, res) -> {
			
			String query = req.param("q").value();
			System.out.println("=>query: "+query);
			Client client = req.require(Client.class);
			
			Query q = new Query(query.trim()+"*")
	                .limit(0,5);
			
			SearchResult queryResult = client.search(q);

			res.header("Access-Control-Allow-Origin", "*");
			
			res.send(queryResult.docs);
		});

	}

	public static void main(final String[] args) {
		run(SearchEngineApp::new, args);
	}

}

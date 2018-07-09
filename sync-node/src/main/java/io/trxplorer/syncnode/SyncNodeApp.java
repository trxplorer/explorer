package io.trxplorer.syncnode;

import org.jooby.Jooby;
import org.jooby.flyway.Flywaydb;
import org.jooby.jdbc.Jdbc;
import org.jooby.jooq.jOOQ;
import org.jooby.json.Jackson;
import org.jooby.quartz.Quartz;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.trxplorer.syncnode.job.AccountSyncJob;
import io.trxplorer.syncnode.job.BlockSyncJob;
import io.trxplorer.syncnode.job.ConsolidationJob;
import io.trxplorer.syncnode.job.MarketJob;
import io.trxplorer.syncnode.job.NodeSyncJob;
import io.trxplorer.syncnode.job.SyncNodeJob;
import io.trxplorer.syncnode.job.VotingRoundJob;
import io.trxplorer.syncnode.job.WitnessSyncJob;

public class SyncNodeApp extends Jooby {
	
	private static final Logger logger = LoggerFactory.getLogger(SyncNodeApp.class);
	
	{
		use(new SyncNodeAppModule());


		use(new Jdbc());
		use(new jOOQ());

		use(new Jackson());
		use(new Flywaydb());

		
		use(new Quartz(SyncNodeJob.class,BlockSyncJob.class,WitnessSyncJob.class,AccountSyncJob.class,NodeSyncJob.class,MarketJob.class));

		
		onStart(registry -> {
			
			SyncNodeConfig config = registry.require(SyncNodeConfig.class);
			
			logger.info("=> Sync node up [id = {}]",config.getNodeId());
			
			DSLContext dslContext = registry.require(DSLContext.class);

			Settings settings = new Settings();
			settings.setRenderSchema(false);

			dslContext.configuration().set(settings);

		});

		get("/", (req, res) -> {

			res.send("");
		});

	}

	public static void main(final String[] args) {
		run(SyncNodeApp::new, args);
	}

}

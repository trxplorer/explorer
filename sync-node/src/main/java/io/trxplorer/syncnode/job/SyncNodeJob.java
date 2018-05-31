package io.trxplorer.syncnode.job;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.jooby.quartz.Scheduled;
import org.jooq.DSLContext;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.trxplorer.syncnode.SyncNodeConfig;

@Singleton
@DisallowConcurrentExecution
public class SyncNodeJob {

	private SyncNodeConfig config;
	private DSLContext dslContext;

	@Inject
	public SyncNodeJob(SyncNodeConfig config,DSLContext dslContext){
		this.config = config;
		this.dslContext = dslContext;
	}
	
	
	@Scheduled("10s")
	public void ping() {
		
		this.dslContext.insertInto(SYNC_NODE)
		.set(SYNC_NODE.NODE_ID, this.config.getNodeId())
		.onDuplicateKeyUpdate()
		.set(SYNC_NODE.PING, Timestamp.valueOf(LocalDateTime.now()))
		.execute()
		
		
		;
		
	}
	
}

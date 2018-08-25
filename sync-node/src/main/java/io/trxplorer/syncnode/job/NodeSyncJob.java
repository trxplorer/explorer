package io.trxplorer.syncnode.job;

import org.jooby.quartz.Scheduled;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.trxplorer.syncnode.SyncNodeConfig;
import io.trxplorer.syncnode.service.NodeSyncService;

@Singleton
@DisallowConcurrentExecution
public class NodeSyncJob {
	
	private NodeSyncService nodeSyncService;
	private SyncNodeConfig config;

	@Inject
	public NodeSyncJob(NodeSyncService nodeSyncService,SyncNodeConfig config) {
		this.nodeSyncService = nodeSyncService;
		this.config = config;
	}
	
	@Scheduled("1m")
	public void addNewNodes() throws InterruptedException{
		
		if (!this.config.isNodesJobEnabled()) {
			return;
		}
		
		this.nodeSyncService.syncNodes();
		
		
	}
	
	@Scheduled("1m")
	public void updateNodesStatus() {
		
		if (!this.config.isNodesJobEnabled()) {
			return;
		}
		
		this.nodeSyncService.updateNodesStatus();
		
	}

	@Scheduled("1m")
	public void removeDownNodes() {
		if (!this.config.isNodesJobEnabled()) {
			return;
		}
		this.nodeSyncService.removeDownNodes();
	}
	
	//FIXME: remove when all geo data is populated
	@Scheduled("5m")
	public void updateNodeLocalization() throws UnirestException {
		if (!this.config.isNodesJobEnabled()) {
			return;
		}
		this.nodeSyncService.updateNodesLocalizationData();
		
	}
	

	
	
}

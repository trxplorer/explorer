package io.trxplorer.syncnode;

import java.util.List;
import java.util.Random;

import com.google.inject.Inject;
import com.typesafe.config.Config;

public class SyncNodeConfig{

	private Config config;
	
	private int generatedNodeId; 
	
	@Inject
	public SyncNodeConfig(Config config) {
		this.config = config;
		Random r = new Random();
		this.generatedNodeId = r.nextInt(1000000-1000)+1000;
	}
	
	
	public int getNodeId() {
		int id = this.config.getInt("node.id");
		return id==-1 ? this.generatedNodeId : id;
	}
	
	public String getGeoDbPath() {
		return this.config.getString("geodb.path");
	}
	
	public int getSyncBatchSize() {
		return this.config.getInt("node.syncBatchSize");
	}
	
	public boolean isVoteJobEnabled() {
		return this.config.getBoolean("jobs.votes");
	}
	
	public boolean isNodesJobEnabled() {
		return this.config.getBoolean("jobs.nodes");
	}
	
	public boolean isResyncJobEnabled() {
		return this.config.getBoolean("jobs.resync");
	}
	
	public boolean isWitnessJobEnabled() {
		return this.config.getBoolean("jobs.witness");
	}
	
	public boolean isAccountJobEnabled() {
		return this.config.getBoolean("jobs.account");
	}
	
	public boolean isMartketJobEnabled() {
		return this.config.getBoolean("jobs.market");
	}
	
	public boolean isBlockJobEnabled() {
		return this.config.getBoolean("jobs.block");
	}
	
	public boolean isSoliditySyncEnabled() {
		return this.config.getBoolean("jobs.soliditySync");
	}
	
	public List<String> getSeedNodes() {
		return this.config.getStringList("tron.seedNodes");
	}
}

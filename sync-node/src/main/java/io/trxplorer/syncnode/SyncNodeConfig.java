package io.trxplorer.syncnode;

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
	
	public int getSyncBatchSize() {
		return this.config.getInt("node.syncBatchSize");
	}
	
}

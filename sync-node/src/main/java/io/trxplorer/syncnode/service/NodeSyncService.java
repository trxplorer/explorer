package io.trxplorer.syncnode.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.InsertFinalStep;
import org.jooq.InsertOnDuplicateSetStep;
import org.jooq.Query;
import org.jooq.UpdateConditionStep;
import org.jooq.impl.DSL;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.api.GrpcAPI.Node;

import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import static io.trxplorer.model.Tables.*;

import io.trxplorer.model.tables.records.NodeRecord;
import io.trxplorer.syncnode.SyncNodeConfig;
import io.trxplorer.troncli.TronFullNodeCli;

public class NodeSyncService {

	private DSLContext dslContext;
	private TronFullNodeCli tronFullNodeCli;
	private SyncNodeConfig config;
	
	private static final int NODE_PING_TIMEOUT = 5000;
	
	private static final Logger logger = LoggerFactory.getLogger(AccountSyncService.class);
	
	@Inject
	public NodeSyncService(DSLContext dslContext,TronFullNodeCli tronFullNodeCli,SyncNodeConfig config) {
		this.dslContext = dslContext;
		this.tronFullNodeCli = tronFullNodeCli;
		this.config = config;
	}
	
	public void syncNodes() throws InterruptedException {
		
		HashMap<String, Node> nodesMap = new HashMap<>();
		
		for(String ip:config.getSeedNodes()) {
			TronFullNodeCli cli = new TronFullNodeCli(ip, true);
			try {
				for(Node node:cli.getAllNodes()){
					nodesMap.put(node.getAddress().getHost().toStringUtf8()+":"+node.getAddress().getPort(), node);
				}
			}catch(Throwable e) {
				logger.error("Could get nodes list from {}",ip,e);
			}finally {
				cli.shutdown();	
			}

			
			
		}
		
		List<Node> nodes = new ArrayList<>(nodesMap.values());
		Collections.shuffle(nodes);
		
		for(Node node:nodes) {

			boolean isUp = pingHost(node.getAddress().getHost().toStringUtf8(), node.getAddress().getPort(), NODE_PING_TIMEOUT);
			
			Timestamp lastUp = Timestamp.valueOf(LocalDateTime.now());
			
			if (!isUp) {
				lastUp = null;
			}
			
			this.dslContext.insertInto(NODE)
			.set(NODE.HOST,node.getAddress().getHost().toStringUtf8())
			.set(NODE.PORT,node.getAddress().getPort())
			.set(NODE.DATE_CREATED,Timestamp.valueOf(LocalDateTime.now()))
			.set(NODE.LAST_UPDATED,Timestamp.valueOf(LocalDateTime.now()))
			.set(NODE.LAST_UP,lastUp)
			.set(NODE.UP,isUp ? (byte)1:(byte)0)
			.onDuplicateKeyUpdate()
			.set(NODE.UP,isUp ? (byte)1:(byte)0)
			.execute();
			

		}
		
		
		
		
	}
	
	
	
	public void updateNodesStatus() {

		List<NodeRecord> dbNodes = this.dslContext.select(NODE.fields()).from(NODE).orderBy(NODE.LAST_UPDATED.asc()).limit(50).fetchInto(NodeRecord.class);
		
		
		for(NodeRecord dbNode:dbNodes) {
			
			boolean isUp = pingHost(dbNode.getHost(), dbNode.getPort(), NODE_PING_TIMEOUT);
			
			dbNode.setUp(isUp ? (byte)1:(byte)0);
			
			if (isUp) {
				dbNode.setLastUp(Timestamp.valueOf(LocalDateTime.now()));
			}
			
			dbNode.setLastUpdated(Timestamp.valueOf(LocalDateTime.now()));
			
			this.dslContext.executeUpdate(dbNode);
		}
		
	}
	
	public void removeDownNodes() {
		Timestamp delay = Timestamp.valueOf(LocalDateTime.now().minusHours(1));

		this.dslContext.deleteFrom(NODE)
		.where(NODE.UP.eq((byte)0)
			.and(NODE.LAST_UP.lt(delay)))
		.execute();
		

		this.dslContext.deleteFrom(NODE)
		.where(NODE.UP.eq((byte)0)
			.and(NODE.LAST_UP.isNull())
			.and(NODE.DATE_CREATED.lt(delay))
		)
		
		.execute();		
	}
	
	
	public void updateNodesLocalizationData() throws UnirestException {
		
		List<NodeRecord> dbNodes = this.dslContext.select(NODE.fields()).from(NODE).where(NODE.COUNTRY.isNull()).limit(100).fetchInto(NodeRecord.class);
		
		List<Query> updates = new ArrayList<>();
		
		for(NodeRecord node:dbNodes) {
			
			HttpResponse<JsonNode> resp = Unirest.get("http://ip-api.com/json/"+node.getHost()).asJson();
			
			if (resp!=null) {
				JSONObject info = resp.getBody().getObject();
				
				updates.add(DSL.update(NODE)
				.set(NODE.COUNTRY,info.getString("country"))
				.set(NODE.COUNTRY_CODE,info.getString("countryCode"))
				.set(NODE.CITY,info.getString("city"))
				.set(NODE.LONGITUDE,info.getBigDecimal("lon"))
				.set(NODE.LATITUDE,info.getBigDecimal("lat"))
				.where(NODE.ID.eq(node.getId())));
				
			}

		}
		
		this.dslContext.batch(updates).execute();
		
	}
	
	
	public static boolean pingHost(String host, int port, int timeout) {
	    try (Socket socket = new Socket()) {
	        socket.connect(new InetSocketAddress(host, port), timeout);
	        return true;
	    } catch (IOException e) {
	        return false;
	    }
	}
	
}

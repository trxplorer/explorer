package io.trxplorer.syncnode.service;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.json.JSONObject;
import org.tron.api.GrpcAPI.Node;

import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import static io.trxplorer.model.Tables.*;

import io.trxplorer.model.tables.records.NodeRecord;
import io.trxplorer.troncli.TronFullNodeCli;

public class NodeSyncService {

	private DSLContext dslContext;
	private TronFullNodeCli tronFullNodeCli;

	@Inject
	public NodeSyncService(DSLContext dslContext,TronFullNodeCli tronFullNodeCli) {
		this.dslContext = dslContext;
		this.tronFullNodeCli = tronFullNodeCli;
	}
	
	public void syncNodes() {
		
		List<Node> nodes = this.tronFullNodeCli.getAllNodes();
		
		for(Node node:nodes) {
			
			createNewNodes(node);
			
		}
		
		this.updateNodeStatus(nodes);
	}
	
	
	public void createNewNodes(Node node) {
		
		
		NodeRecord nodeRecord = this.dslContext.select(NODE.fields())
		.from(NODE).where(NODE.HOST.eq(node.getAddress().getHost().toStringUtf8()))
		.and(NODE.PORT.eq(node.getAddress().getPort()))
		.fetchOneInto(NodeRecord.class);
		
		
		if (nodeRecord == null) {
			
			this.dslContext.insertInto(NODE)
			.set(NODE.HOST,node.getAddress().getHost().toStringUtf8())
			.set(NODE.PORT,node.getAddress().getPort())
			.set(NODE.UP,(byte)1)
			.execute();
			
		}
		
	}
	
	public void updateNodeStatus(List<Node> nodes) {

		List<NodeRecord> dbNodes = this.dslContext.select(NODE.fields()).from(NODE).fetchInto(NodeRecord.class);
		
		List<Query> updates = new ArrayList<>();
		
		for(NodeRecord dbNode:dbNodes) {
			
			boolean isUp = nodes.stream().anyMatch((n)->{
				return dbNode.getHost().equals(n.getAddress().getHost().toStringUtf8())&& dbNode.getPort()==n.getAddress().getPort();
			});
			
			updates.add(DSL.update(NODE).set(NODE.UP,isUp ? (byte)1:(byte)0).where(NODE.ID.eq(dbNode.getId())));
			
		}
		
		this.dslContext.batch(updates).execute();
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
	
}

package io.trxplorer.searchengine.job;

import static io.trxplorer.model.Tables.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooby.quartz.Scheduled;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.redisearch.client.Client;
import io.trxplorer.model.tables.pojos.Account;
import io.trxplorer.model.tables.pojos.Witness;
import io.trxplorer.searchengine.SearchEngineConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

@Singleton
@DisallowConcurrentExecution
public class IndexerJob {
	
	private Jedis jedis;
	private Client redisearchClient;
	private DSLContext dslContext;
	
	private final int BATCH_SIZE = 500;

	//Account
	private final int ACCOUNT_TYPE = 1;
	private final String LAST_ACCOUNT_ID_KEY = "lastAccountId";
	
	//Witness
	private final int WITNESS_TYPE = 2;
	private final String LAST_WITNESS_ID_KEY = "lastWitnessId";
	
	//Transaction
	private final int TRANSACTION_TYPE = 3;
	private final String LAST_TRANSACTION_ID_KEY = "lastTxId";	
	
	//Block
	private final int BLOCK_TYPE = 4;
	private final String LAST_BLOCK_ID_KEY = "lastBlockId";	
	
	//Token
	private final int TOKEN_TYPE = 5;
	private final String LAST_TOKEN_ID_KEY = "lastTokenId";
	private SearchEngineConfig config;	
	
	@Inject
	public IndexerJob(Jedis jedis,Client rediseachClient,DSLContext dslContext,SearchEngineConfig config) {
		this.jedis = jedis;
		this.redisearchClient = rediseachClient;
		this.dslContext = dslContext;
		this.config = config;
	}
	
	@Scheduled("10s")
	public void indexAll() {
		
		if (!this.config.isIndexEnabled()) {
			return;
		}
		
		//FIXME: should ideally be parallelized but no transaction for now for redisseach client
		//using them in specific jobs causes error
		//indexAccountAddress();
		//indexWitnessUrl();
		
		//Account
		indexData(LAST_ACCOUNT_ID_KEY, ACCOUNT_TYPE, ACCOUNT.ADDRESS.getName(),  ACCOUNT.ID.getName(), ACCOUNT.getName());
		//Witness
		indexData(LAST_WITNESS_ID_KEY, WITNESS_TYPE, WITNESS.URL.getName(), WITNESS.ID.getName(), WITNESS.getName());
		//Tx
		indexData(LAST_TRANSACTION_ID_KEY, TRANSACTION_TYPE, TRANSACTION.HASH.getName(), TRANSACTION.ID.getName(), TRANSACTION.getName());
		//Block
		indexData(LAST_BLOCK_ID_KEY, BLOCK_TYPE, BLOCK.HASH.getName(), BLOCK.ID.getName(), BLOCK.getName());
		//Token
		indexData(LAST_TOKEN_ID_KEY, TOKEN_TYPE, CONTRACT_ASSET_ISSUE.NAME.getName(), CONTRACT_ASSET_ISSUE.ID.getName(), CONTRACT_ASSET_ISSUE.getName());
	}
	
	
	
	public void indexData(String keyName,int type,String textFieldName,String idFieldName,String tableName) {
		
		String lastId = this.jedis.get(keyName);
		
		System.out.println("=> last "+tableName+" id:"+lastId);
		
		ULong id = lastId == null ? ULong.valueOf(0) : ULong.valueOf(lastId);
		
		Result<Record2<Object, Object>> records = this.dslContext.select(DSL.field(idFieldName),DSL.field(textFieldName)).from(tableName).where(DSL.field(idFieldName).gt(id)).limit(BATCH_SIZE)
		.fetch();
		
		
		for(Record record:records) {
			
			if (record.get(textFieldName) == null || StringUtils.isBlank(record.get(textFieldName).toString()) || record.get(idFieldName)==null) {
				continue;
			}
			
			System.out.println("Save "+tableName+":"+record.get(textFieldName).toString().trim());
			
			saveDocument(record.get(textFieldName).toString().trim(), type, ((BigInteger)record.get(idFieldName)).longValue());
			
			this.jedis.set(keyName, ""+record.get(idFieldName));
			
		}
		
	}
	
	
	
	private void saveDocument(String text,int type,long id) {
		
		HashMap<String, Object> document = new HashMap<>(); 
		
		document.put("text", text);
		document.put("type",type);
		
		try {
			this.redisearchClient.addDocument(type+"_"+id, document);	
		}catch (JedisDataException e) {
			if (!e.getMessage().contains("Document already in index")) {
				throw e;
			}
		}
		
	}
	
}

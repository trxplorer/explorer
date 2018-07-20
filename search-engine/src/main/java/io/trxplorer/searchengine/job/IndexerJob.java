package io.trxplorer.searchengine.job;

import static io.trxplorer.model.Tables.*;

import java.math.BigInteger;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.jooby.quartz.Scheduled;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.quartz.DisallowConcurrentExecution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.redisearch.client.Client;
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
		indexData(LAST_ACCOUNT_ID_KEY, ACCOUNT_TYPE, ACCOUNT.ACCOUNT_NAME.concat(" - ").concat(ACCOUNT.ADDRESS).as("account"),  ACCOUNT.ID, ACCOUNT.getName());
		//Witness
		indexData(LAST_WITNESS_ID_KEY, WITNESS_TYPE, WITNESS.URL, WITNESS.ID, WITNESS.getName());
		//Tx
		//indexData(LAST_TRANSACTION_ID_KEY, TRANSACTION_TYPE, TRANSACTION.HASH.getName(), TRANSACTION.ID.getName(), TRANSACTION.getName());
		//Block
		indexData(LAST_BLOCK_ID_KEY, BLOCK_TYPE, BLOCK.NUM, BLOCK.ID, BLOCK.getName());
		//Token
		indexData(LAST_TOKEN_ID_KEY, TOKEN_TYPE, CONTRACT_ASSET_ISSUE.NAME.concat(" - ").concat(CONTRACT_ASSET_ISSUE.ABBR).as("token"), CONTRACT_ASSET_ISSUE.ID, CONTRACT_ASSET_ISSUE.getName());
	}
	
	
	
	public void indexData(String keyName,int type,Field<?> textField,Field<ULong> idField,String tableName) {
		
		String lastId = this.jedis.get(keyName);
		
		System.out.println("=> last "+tableName+" id:"+lastId);
		
		ULong id = lastId == null ? ULong.valueOf(0) : ULong.valueOf(lastId);
		
		Result<?> records = this.dslContext.select(idField,textField).from(tableName).where(idField.gt(id)).limit(BATCH_SIZE)
		.fetch();
		
		
		for(Record record:records) {
			
			if (record.get(textField.getName()) == null || StringUtils.isBlank(record.get(textField.getName()).toString()) || record.get(idField.getName())==null) {
				continue;
			}
			
			System.out.println("Save "+tableName+":"+record.get(textField.getName()).toString().trim());
			
			saveDocument(record.get(textField.getName()).toString().trim(), type, (record.get(idField.getName(),ULong.class)).longValue());
			
			this.jedis.set(keyName, ""+record.get(idField.getName()));
			
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

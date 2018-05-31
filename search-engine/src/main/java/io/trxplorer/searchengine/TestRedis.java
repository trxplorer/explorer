package io.trxplorer.searchengine;

import java.util.HashMap;
import java.util.Map;

import io.redisearch.Query;
import io.redisearch.Schema;
import io.redisearch.SearchResult;
import io.redisearch.client.Client;
import redis.clients.jedis.Jedis;

public class TestRedis {

	public static void main(String[] args) {
		Client client = new Client("testung", "localhost", 6379);
		
		Jedis jedis = new Jedis();
		//jedis.set("events/city/rome", "32,15,223,828");
		System.out.println(jedis.get("events/city/rome"));
		
		Schema sc = new Schema()
                .addTextField("text", 1.0)
                .addNumericField("type");
		client.dropIndex(true);
		client.createIndex(sc, Client.IndexOptions.Default());
		
		Map<String, Object> fields1 = new HashMap<>();
		fields1.put("text", "http://Mercury.org");
		fields1.put("type", 1);

		Map<String, Object> fields2 = new HashMap<>();
		fields2.put("text", "27cEZa99jVaDkujPwzZuHYgkYNqv6zzYLSP");
		fields2.put("type", 2);

		client.addDocument("doc1", fields1);
		client.addDocument("doc2", fields2);
		
		
		Query q = new Query("http*")
                .limit(0,5);
		
		SearchResult res = client.search(q);

		
		System.out.println(res.docs);
	}
	
}

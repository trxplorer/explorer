package io.trxplorer.searchengine;

import org.jooby.Env;
import org.jooby.Jooby.Module;

import com.google.inject.Binder;
import com.typesafe.config.Config;

import io.redisearch.Schema;
import io.redisearch.client.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

public class RedisModule implements Module{

	@Override
	public void configure(Env env, Config conf, Binder binder) throws Throwable {
		
		String indexName = conf.getString("redis.index");
		String host = conf.getString("redis.host");
		int port = conf.getInt("redis.port");
		
		Client client = new Client(indexName, host, port);
		
		Schema sc = new Schema()
                .addTextField("text", 1.0)
                .addNumericField("type");
		
		try {
			client.createIndex(sc, Client.IndexOptions.Default());
		}catch(JedisDataException e) {
			//FIXME: API doesn't provide a way to handle this properly for now 
			if (!e.getMessage().contains("Index already exists")) {
				e.printStackTrace();	
			}
			
		}
		
		Jedis jedis = new Jedis(host, port);
		
		binder.bind(Client.class).toInstance(client);
		binder.bind(Jedis.class).toInstance(jedis);
		
		
	}

	
	
}

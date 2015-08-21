package org.infinispan.tutorial.mongodb;

import java.util.Map.Entry;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfigurationBuilder;
import org.infinispan.tutorial.embedded.listener.CacheLevelLoggingListener;
import org.infinispan.tutorial.embedded.listener.CacheManagerLevelLoggingListener;


public class MongoDbDemo {

	public static void main(String[] args) throws InterruptedException {
		ConfigurationBuilder b = new ConfigurationBuilder();
		b.eviction().strategy(EvictionStrategy.LRU).maxEntries(10)
		.persistence()
		.passivation(true)
		.addStore(MongoDBStoreConfigurationBuilder.class)
		   .hostname( "localhost" )
		   .port( 27017 )
		   .timeout( 1500 )
		   .acknowledgment( 0 )
//		   .username( "mongoDBUSer" )
//		   .password( "mongoBDPassword" )
		   .database( "infinispan_cachestore" )
		   .collection( "entries" )
		   .preload(true)
		   .purgeOnStartup(true);
		 
		final Configuration config = b.build();
//		MongoDBStoreConfiguration store = (MongoDBStoreConfiguration) config.loaders().cacheLoaders().get(0);
//		System.out.println(store.toString() + store.collection());
		
		EmbeddedCacheManager manager = new DefaultCacheManager();
		manager.addListener(new CacheManagerLevelLoggingListener()); 
		manager.defineConfiguration("mongo-cache", config);
		Cache<String, String> cache = manager.getCache("mongo-cache");
		cache.addListener(new CacheLevelLoggingListener());
		
		for (Entry<String, String> entry : cache.entrySet()) {
	    	  System.out.println(entry.getKey() + ":" + entry.getValue());
	      }
		
		cache.put("key2", "value2");
		
		cache.stop();
	}

}

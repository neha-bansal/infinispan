package org.infinispan.tutorial.embedded.filestore;

import java.util.Map.Entry;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.tutorial.embedded.listener.CacheLevelLoggingListener;
import org.infinispan.tutorial.embedded.listener.CacheManagerLevelLoggingListener;

public class FileCacheStore {

@SuppressWarnings("deprecation")
public static void main(String args[]) throws Exception {
	
//      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan.xml");
      
	  EmbeddedCacheManager manager = new DefaultCacheManager();
      
      manager.addListener(new CacheManagerLevelLoggingListener()); 
      
      Configuration config = new ConfigurationBuilder()
							      .eviction().strategy(EvictionStrategy.LRU).maxEntries(2)
							      .persistence()
							      .passivation(true)
							      .addSingleFileStore()
							      	//.maxEntries(10)
							      	.location("./target/myDataStore") 
							      	//.purgeOnStartup(true)				//file store gets cleared on cache startup
							      .build();
      
      manager.defineConfiguration("file-store-cache", config);
      Cache<Object, Object> cache = manager.getCache("file-store-cache");
      cache.addListener(new CacheLevelLoggingListener());
      
      cache.put("key1", "value1");	//entry is persisted to file store as well if passivation is false else only if entry is evicted.
      cache.put("key2", "value2");
      cache.put("key3", "value3");
      cache.put("key4", "value4");

      System.out.println("======================== entries after first put ============================");
      
      // as per eviction strategy, cache can have only 2 entries, so rest are fetched from file store
      //cache.entrySet() returns a set view of the mappings contained in this cache and cache loader across the entire cluster
      //it does not actually load the entry from cache loader to cache, so no eviction here.
      //but if cache.get("key1"); is used, eviction will take place and some other key will be saved to file store to load key1 in cache.
      
      for (Entry<Object, Object> entry : cache.entrySet()) {
    	  System.out.println(entry.getKey() + ":" + entry.getValue());
      }
      
      //cache.put("key1", "value1 111");
      
      System.out.println("======================== entries after remove ============================");
      
      cache.remove("key4");		//entry is removed from file store as well
      
      for (Entry<Object, Object> entry : cache.entrySet()) {
    	  System.out.println(entry.getKey() + ":" + entry.getValue());
      }
      
      //when cache is stopped, all the cache entries are passivated(saved) to the file store(disk)
      cache.stop();
      
      System.out.println("========================= entries after cache restart ===========================");
      
      //when cache is started it is pre loaded with the data from the file store
      cache.start();
      
      for (Entry<Object, Object> entry : cache.entrySet()) {
    	  System.out.println(entry.getKey() + ":" + entry.getValue());
      }
      
      //cache.get("key1");  if cache is not stopped and started again then this statement would fire CACHE_ENTRY_ACTIVATED event.
   }

}

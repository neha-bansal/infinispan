
package org.infinispan.tutorial.embedded.clustering;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.infinispan.tutorial.embedded.clustering.util.Assert.assertEqual;
import static org.infinispan.tutorial.embedded.clustering.util.Assert.assertFalse;
import static org.infinispan.tutorial.embedded.clustering.util.Assert.assertTrue;

public class LocalCache {

   public static void main(String args[]) throws Exception {
	   Cache<Object, Object> cache = new DefaultCacheManager().getCache();
      
      // Add a entry
      cache.put("key", "value");
      System.out.println(cache.get("key"));
      // Validate the entry is now in the cache
      assertEqual(1, cache.size());
      assertTrue(cache.containsKey("key"));
      
      // Remove the entry from the cache
      Object v = cache.remove("key");
      System.out.println(cache.get("key"));
      // Validate the entry is no longer in the cache
      assertEqual("value", v);
      assertTrue(cache.isEmpty());

      // Add an entry with the key "key"
      cache.put("key", "value");
      // And replace it if missing
      cache.putIfAbsent("key", "newValue");
      System.out.println(cache.get("key"));
      // Validate that the new value was not added
      assertEqual("value", cache.get("key"));

      cache.clear();
      assertTrue(cache.isEmpty());

      //By default entries are immortal but we can override this on a per-key basis and provide lifespans.
      cache.put("key", "value", 5, SECONDS, 2, SECONDS);
      System.out.println(cache.get("key"));
      assertTrue(cache.containsKey("key"));
      System.out.println("Thread is sleeping...");
      Thread.sleep(3000);
      System.out.println("Thread wakes up...");
      System.out.println(cache.get("key"));
      assertFalse(cache.containsKey("key"));
   }

}

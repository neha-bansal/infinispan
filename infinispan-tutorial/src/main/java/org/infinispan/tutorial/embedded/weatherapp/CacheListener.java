package org.infinispan.tutorial.embedded.weatherapp;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

@Listener(clustered=true)
public class CacheListener {

	@CacheEntryCreated
	public void cacheEntryCreated (CacheEntryCreatedEvent<String, LocationWeather> event) {
		if (event.isPre()) return;
		System.out.printf("-- Entry for %s created by node %s in the cluster\n", event.getKey(), event.getCache());
		if (!event.isOriginLocal()) {
			System.out.printf("-- Entry for %s modified by another node %s in the cluster\n", event.getKey(), event.getCache());
		}
	}
	
	@CacheEntryModified
	public void cacheEntryModified (CacheEntryModifiedEvent<String, LocationWeather> event) {
		if (event.isPre()) return;
		System.out.printf("-- Entry for %s modified by node %s in the cluster\n", event.getKey(), event.getCache());
//		if (!event.isOriginLocal()) {
//			System.out.printf("-- Entry for %s modified by another node %s in the cluster\n", event.getKey(), event.getCache());
//		}
	}
	
	@CacheEntryRemoved
	public void cacheEntryRemoved (CacheEntryRemovedEvent<String, LocationWeather> event) {
		if (event.isPre()) return;
		System.out.printf("-- Entry for %s deleted by node %s in the cluster\n", event.getKey(), event.getCache());
//		if (!event.isOriginLocal()) {
//			System.out.printf("-- Entry for %s modified by another node %s in the cluster\n", event.getKey(), event.getCache());
//		}
	}
	
}

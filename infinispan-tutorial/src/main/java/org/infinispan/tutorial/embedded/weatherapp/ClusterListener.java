package org.infinispan.tutorial.embedded.weatherapp;

import java.util.concurrent.CountDownLatch;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;

@Listener
public class ClusterListener {
	
	CountDownLatch startupCountDownLatch = new CountDownLatch(1);
	CountDownLatch shutdownCountDownLatch = new CountDownLatch(1);
	private int expectedCount;
	
	public ClusterListener(int expectedCount) {
		this.expectedCount = expectedCount;
	}

	@ViewChanged
	public void viewChanged(ViewChangedEvent event) {
		System.out.printf("----------------------- View changed: %s -------------------------\n", event.getNewMembers());
		
		if (event.getCacheManager().getMembers().size() == expectedCount) {
			startupCountDownLatch.countDown();
		} else if (event.getNewMembers().size() < event.getOldMembers().size()) {
			shutdownCountDownLatch.countDown();
		}
	}
	
}

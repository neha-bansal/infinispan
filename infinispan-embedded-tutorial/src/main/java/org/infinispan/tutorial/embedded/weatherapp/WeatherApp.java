package org.infinispan.tutorial.embedded.weatherapp;

import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class WeatherApp {

   static final String[] locations = { /*"Rome, Italy", "Como, Italy", "Basel, Switzerland", "Bern, Switzerland",
         "London, UK", "Newcastle, UK", "Bucarest, Romania", "Cluj-Napoca, Romania", "Ottawa, Canada",
         "Toronto, Canada",*/"Lisbon, Portugal", "Porto, Portugal", "Raleigh, USA", "Washington, USA", "Dublin, Ireland", "Chandigarh, India" };
   private final WeatherService weatherService;
   private final EmbeddedCacheManager cacheManager;
   private final Cache<String, LocationWeather> cache;
   private final ClusterListener clusterListener;
   
   public WeatherApp() throws InterruptedException {
	  GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
	  global.transport().clusterName("WeatherApp");
	  
	  ConfigurationBuilder config = new ConfigurationBuilder();
	  config.expiration().lifespan(5, TimeUnit.SECONDS);
	  config.clustering().cacheMode(CacheMode.DIST_SYNC);
	  
	  cacheManager = new DefaultCacheManager(global.build(), config.build());
	  
	  clusterListener = new ClusterListener(2);
	  cacheManager.addListener(clusterListener); 
	  
	  cache = cacheManager.getCache();
	  cache.addListener(new CacheListener());
      weatherService = initWeatherService(cache);
      
      System.out.println("---- Waiting for cluster to form ----");
      clusterListener.startupCountDownLatch.await();
   }

   private WeatherService initWeatherService(Cache<String, LocationWeather> cache) {
      if (System.getProperty("random.weather.service") != null) {
         return new RandomWeatherService(cache);
      } else {
         return new OpenWeatherMapService(cache);
      }
   }

   public void fetchWeather() {
      System.out.println("---- Fetching weather information ----");
      long start = System.currentTimeMillis();
      for (String location : locations) {
         LocationWeather weather = weatherService.getWeatherForLocation(location);
         System.out.printf("%s - %s\n", location, weather);
      }
      System.out.printf("---- Fetched in %dms ----\n", System.currentTimeMillis() - start);
   }

   public void shutdown() throws InterruptedException {
	   if (!cacheManager.isCoordinator()) {
		   clusterListener.shutdownCountDownLatch.await();
		   Thread.sleep(10000);
	   }
	   cacheManager.stop();
   }

   public static void main(String[] args) throws Exception {
      WeatherApp app = new WeatherApp();
      
      if (app.cacheManager.isCoordinator()) {
    	  
    	  app.fetchWeather();
    	  
    	  app.fetchWeather();
    	  
    	  Thread.sleep(5000); 
    	  app.fetchWeather();
      }


      app.shutdown();
   }

}
